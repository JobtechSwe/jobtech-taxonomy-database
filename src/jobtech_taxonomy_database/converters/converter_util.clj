(ns ^{:author "Batfish"
      :doc "utils for converting SQL data to Datomic"}
 jobtech-taxonomy-database.converters.converter-util
  (:gen-class)
  (:require [datomic.client.api :as d]
            [jobtech-taxonomy-database.legacy-migration :as legacy-migration]
            [jobtech-taxonomy-database.converters.nano-id-assigner :as nano]
            [jobtech-taxonomy-database.datomic-connection :refer :all :as conn]
            [camel-snake-kebab.core :as csk]
            [clojure.spec.alpha :as s]
            [jobtech-taxonomy-database.types :as t]))

(defn- get-concept-id [instance-type legacy-id]
  "- Checks if the :concept/type has a valid value
  - Returns nano-id"
  (nano/get-nano (csk/->kebab-case-string instance-type) (str legacy-id)))

(defn create-temp-id [instance-type legacy-id]
  (str (csk/->kebab-case-string instance-type)  "-" legacy-id))

(defn- get-concept-id-and-temp-id [instance-type legacy-id]
  "Checks if type is valid, returns nano-id as concept ID, and temp ID (only useful in the specific transaction)"
  [(get-concept-id instance-type legacy-id)
   (create-temp-id instance-type legacy-id)])

(defn create-concept
  "Creates map for standard concept with default attributes"
  ([instance-type label description legacy-id]
   {:pre [(s/valid? ::t/concept-types instance-type)
          (s/valid? string? label)
          (s/valid? string? description)
          (or (s/valid? string? legacy-id) (s/valid? int? legacy-id))]}
   (let [[concept-id temp-id] (get-concept-id-and-temp-id instance-type legacy-id)]
     {:db/id                                        temp-id
      :concept/id                                   concept-id
      :concept/description                          description
      :concept/definition                           description
      :concept/preferred-label                      label
      :concept/preferred-term                       concept-id  ; deprecated this one
      :concept/alternative-terms                    #{concept-id}
      :concept/type                                 instance-type
      :concept/category                             (csk/->kebab-case-keyword instance-type) ; deprecated this one
      :concept.external-database.ams-taxonomy-67/id (str legacy-id) ;; TODO rename attribute
      })))

#_(defn create-term [nano-id term]
    "never mind this, it's not going to be used"
    {:db/id          nano-id
     :term/base-form term})

(defn create-term-from-concept [{:keys  [:concept/id :concept/preferred-label]}]
  " Takes a concept, returns map for preferred-label-concept"
  {:db/id id
   :term/base-form preferred-label})

(defn create-relation [concept1-id concept2-id type]
  "Both parameters should be either entity-id or temp-id"
  {:pre [(s/valid? ::t/relation-types type)]}
  {:relation/concept-1 concept1-id
   :relation/concept-2 concept2-id
   :relation/type type})

(defn create-broader-relation-to-concept [concept broader-temp-id]
  "First input should be a concept, second an entity-id or a temp-id"
  (create-relation (:db/id concept) broader-temp-id t/broader))

(defn create-narrower-relation-to-concept [concept narrower-temp-id]
  "First input should be a concept, second an entity-ids OR a temp-ids"
  (create-relation (:db/id concept) narrower-temp-id t/narrower))

(def get-entity-id-by-legacy-id-query
  '[:find ?s
    :in $ ?legacy-id ?type
    :where
    [?s :concept.external-database.ams-taxonomy-67/id ?legacy-id]
    [?s :concept/type ?type]])

(defn get-entity-id-by-legacy-id [legacy-id type]
  (ffirst (d/q get-entity-id-by-legacy-id-query (conn/get-db) (str legacy-id) type)))

(def get-entity-id-by-attribute-and-value-query
  '[:find ?s
    :in $ ?attribute ?value ?category
    :where
    [?s ?attribute ?value]
    [?s :concept/category ?category]])

(defn get-concept-by-attribute-and-value [attribute value category]
  (ffirst (d/q get-entity-id-by-attribute-and-value-query (conn/get-db) attribute value category)))

(def get-preferred-term-and-entity-id-by-legacy-id-query
  '[:find ?term ?s
    :in $ ?legacy-id ?category
    :where
    [?t :term/base-form ?term]
    [?s :concept/preferred-term ?t]
    [?s :concept.external-database.ams-taxonomy-67/id ?legacy-id]
    [?s :concept/category ?category]])

(defn get-preferred-term-by-legacy-id [legacy-id type]
  (first (d/q get-preferred-term-and-entity-id-by-legacy-id-query (conn/get-db) legacy-id type)))

(defn get-concept [entity-id]
  (d/pull (conn/get-db)   [:concept/id
                           :concept/description
                           :concept/definition
                           {:concept/preferred-term [:db/id :term/base-form]}
                           :concept/category
                           :concept.external-database.ams-taxonomy-67/id]
          entity-id))

(defn get-entity-if-exists-or-temp-id [legacy-id type]
  (if-let [entity-id (get-entity-id-by-legacy-id legacy-id type)]
    entity-id
    (create-temp-id type legacy-id)))

(defn deprecate-concept [type legacy-id]
  {:pre [legacy-id type]}
  (if-let [entity-id (get-entity-id-by-legacy-id (str legacy-id) type)]
    {:db/id entity-id  :concept/deprecated true}))

(defn update-concept [entity-id attr-map]
  (let [temp-id (str (gensym))
        concept {:db/id entity-id}
        concept-with-extras
        (-> concept
            (cond-> (contains? attr-map :new-term)
              (assoc :concept/preferred-label (:new-term attr-map)
                     :concept/preferred-term temp-id))
            (cond-> (contains? attr-map :description) (assoc :concept/description (:description attr-map)
                                                             :concept/definition (:description attr-map)
                                                             )
                    (contains? attr-map :new-term) (assoc :concept/description (:new-term attr-map)
                                                          :concept/definition (:description attr-map)
                                                          )) ;; kommer den här att skriva över description om den har en new-term?
            (cond-> (contains? attr-map :ssyk) (assoc :concept.external-standard/ssyk-2012 (:ssyk attr-map)))
            (cond-> (contains? attr-map :sort) (assoc :concept.category/sort-order (:sort attr-map)))
            (cond-> (contains? attr-map :eures) (assoc :concept.external-standard/eures-code (:eures attr-map)))
            (cond-> (contains? attr-map :driving-licence-code)
              (assoc :concept.external-standard/driving-licence-code (:driving-licence-code attr-map)))
            (cond-> (contains? attr-map :nuts-3) (assoc :concept.external-standard/nuts-level-3-code (:nuts-3 attr-map)))
            (cond-> (contains? attr-map :country-code) (assoc :concept.external-standard/country-code (:country-code attr-map)))
            (cond-> (contains? attr-map :legacy-id) (assoc :concept.external-database.ams-taxonomy-67/id (:legacy-id attr-map)))
            (cond-> (contains? attr-map :isco) (assoc :concept.external-standard/isco-08 (:isco attr-map)))
            (cond-> (contains? attr-map :sni) (assoc :concept.external-standard/sni-level-code (:sni attr-map))))]
    (concat [concept-with-extras]
            (if (contains? attr-map :new-term)
              [{:db/id temp-id :term/base-form (:new-term attr-map)}]))))

(def get-relation-by-legacy-ids-and-types-query
    '[:find ?r ?c1
      :in $ ?legacy-id-1 ?legacy-id-2 ?type-1 ?type-2 ?relation-type
      :where
      [?c1 :concept.external-database.ams-taxonomy-67/id ?legacy-id-1]
      [?c2 :concept.external-database.ams-taxonomy-67/id ?legacy-id-2]
      [?c1 :concept/type ?type-1]
      [?c2 :concept/type ?type-2]
      [?r :relation/concept-1 ?c1]
      [?r :relation/concept-2 ?c2]
      [?r :relation/type ?relation-type]])

(def get-only-relation-by-legacy-ids-and-types-query
  '[:find ?r
    :in $ ?legacy-id-1 ?legacy-id-2 ?type-1 ?type-2 ?relation-type
    :where
    [?c1 :concept.external-database.ams-taxonomy-67/id ?legacy-id-1]
    [?c2 :concept.external-database.ams-taxonomy-67/id ?legacy-id-2]
    [?c1 :concept/type ?type-1]
    [?c2 :concept/type ?type-2]
    [?r :relation/concept-1 ?c1]
    [?r :relation/concept-2 ?c2]
    [?r :relation/type ?relation-type]])


#_(def get-relation
  '[:find (pull ?r ["*" {:relation/concept-1 [*]
                         :relation/concept-2 [*]
                         }

                    ])
    :in $ ?relation-type ?legacy-id-1  ?legacy-id-2
    :where
    [?r :relation/type ?relation-type]
    [?r :relation/concept-1 ?c1]
    [?c1 :concept.external-database.ams-taxonomy-67/id ?legacy-id-1]
    [?r :relation/concept-2 ?c2]
    [?c2 :concept.external-database.ams-taxonomy-67/id ?legacy-id-2]
    ]
  )

;; (first (d/q get-relation (conn/get-db) "isco_4_to_skill" "41"  "606897" ))



(defn get-relation-by-legacy-ids-and-types [legacy-id-1 legacy-id-2 type-1 type-2 relation-type]
    (ffirst (d/q get-relation-by-legacy-ids-and-types-query  (conn/get-db) (str legacy-id-1) (str legacy-id-2) type-1 type-2 relation-type)))

(defn get-only-relation-by-legacy-ids-and-types [legacy-id-1 legacy-id-2 type-1 type-2 relation-type]
  (ffirst (d/q get-only-relation-by-legacy-ids-and-types-query (conn/get-db) (str legacy-id-1) (str legacy-id-2) type-1 type-2 relation-type)))

(defn update-relation-by-legacy-ids-and-types
    [concept-legacy-id
     concept-type
     old-related-concept-legacy-id
     related-concept-type
     new-related-concept-legacy-id
     relation-type]
    "This function will find the relation and retract it.
  And create a new relation with the same relation-type to the new entity.
  The new entity has to exist in the database."
    (let [[relation-entity-id concept-entity-id] (get-relation-by-legacy-ids-and-types
                                                  concept-legacy-id
                                                  old-related-concept-legacy-id
                                                  concept-type
                                                  related-concept-type
                                                  relation-type)
          new-related-concept-entity-id (get-entity-id-by-legacy-id
                                         new-related-concept-legacy-id
                                         related-concept-type)]
      [[:db/retractEntity relation-entity-id]
       (create-relation concept-entity-id new-related-concept-entity-id relation-type)]))

(defn retract-relation-by-legacy-ids-and-types
  [concept-legacy-id
   concept-type
   old-related-concept-legacy-id
   related-concept-type
   relation-type]
  "This function will find the relation and retract it."
  (let [relation-entity-id (get-only-relation-by-legacy-ids-and-types
                              concept-legacy-id
                              old-related-concept-legacy-id
                              concept-type
                              related-concept-type
                              relation-type)]
    [[:db/retractEntity relation-entity-id]]))


(defn get-new-relation-by-legacy-ids-and-types
  [concept-legacy-id
   concept-type
   new-related-concept-legacy-id
   related-concept-type
   relation-type]
  "This function will find the entity ID's of two concepts and create a new relation between them.
  Both concepts has to exist in the database or be part of the current transaction."
  (let [entity-1-id (get-entity-if-exists-or-temp-id concept-legacy-id concept-type)
        entity-2-id (get-entity-if-exists-or-temp-id new-related-concept-legacy-id related-concept-type)]
    [(create-relation  entity-1-id entity-2-id relation-type)]))

(defn replace-concept [replaced-legacy-id replacing-legacy-id type]
  (let [old-concept (get-entity-id-by-legacy-id replaced-legacy-id type)
        replaced-by-concept-id (get-entity-if-exists-or-temp-id replacing-legacy-id type)]
    {:db/id old-concept
     :concept/replaced-by replaced-by-concept-id}))

(comment
  "Some debugging helper functions"
  (def get-a-relation
    '[:find (pull ?r [*])
      :in $ relation-type
      :where
      [?r :relation/type ?relation-type]])

  (def get-all-legacy-id
    '[:find (pull ?r [*])
      :in $ ?legacy-id
      :where
      [?r :concept.external-database.ams-taxonomy-67/id ?legacy-id]]))
