(ns ^{:author "Batfish"
      :doc    "Utility functions for the jobtech-taxonomy-database project."}
 jobtech-taxonomy-database.converters.converter-util
  (:gen-class)
  (:require [datomic.client.api :as d]
            [jobtech-taxonomy-database.converters.nano-id-assigner :as nano]
            [jobtech-taxonomy-database.datomic-connection :as conn]
            [camel-snake-kebab.core :as csk]
            [clojure.spec.alpha :as s]
            [jobtech-taxonomy-database.types :as t]))

(defn get-concept-id [instance-type legacy-id]
  "- Checks if the :concept/type has a valid value
  - Returns nano-id"
  (nano/get-nano (csk/->kebab-case-string instance-type) (str legacy-id)))

(defn create-temp-id [instance-type legacy-id]
  (str (csk/->kebab-case-string instance-type) "-" legacy-id))

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
      :concept/type                                 instance-type
      :concept.external-database.ams-taxonomy-67/id (str legacy-id) ;; TODO rename attribute
      })))

(defn create-relation [concept1-id concept2-id type]
  "Both parameters should be either entity-id or temp-id"
  {:pre [(s/valid? ::t/relation-types type)]}
  {:relation/concept-1 concept1-id
   :relation/concept-2 concept2-id
   :relation/type      type})

(defn create-broader-relation-to-concept [concept broader-temp-id]
  "First input should be a concept, second an entity-id or a temp-id"
  (create-relation (:db/id concept) broader-temp-id t/broader))

(def get-entity-id-by-legacy-id-query
  '[:find ?s
    :in $ ?legacy-id ?type
    :where
    [?s :concept.external-database.ams-taxonomy-67/id ?legacy-id]
    [?s :concept/type ?type]])

(defn get-entity-id-by-legacy-id [legacy-id type]
  (ffirst (d/q get-entity-id-by-legacy-id-query (conn/get-db) (str legacy-id) type)))

(defn get-concept [entity-id]
  (d/pull (conn/get-db) [:concept/id
                         :concept/description
                         :concept/definition
                         :concept/type
                         :concept.external-database.ams-taxonomy-67/id]
          entity-id))

(defn get-entity-if-exists-or-temp-id [legacy-id type]
  (if-let [entity-id (get-entity-id-by-legacy-id legacy-id type)]
    entity-id
    (create-temp-id type legacy-id)))

(defn deprecate-concept [type legacy-id]
  {:pre [legacy-id type]}
  (if-let [entity-id (get-entity-id-by-legacy-id (str legacy-id) type)]
    {:db/id entity-id :concept/deprecated true}))

(defn update-concept [entity-id attr-map]
  (let [concept {:db/id entity-id}
        concept-with-extras
        (-> concept
            (cond-> (contains? attr-map :new-term)
              (assoc :concept/preferred-label (:new-term attr-map)))
            (cond-> (contains? attr-map :description) (assoc :concept/description (:description attr-map))
                    (contains? attr-map :new-term) (assoc :concept/description (:new-term attr-map)))
            (cond-> (contains? attr-map :description) (assoc :concept/description (:description attr-map)
                                                             :concept/definition (:description attr-map))
                    (contains? attr-map :new-term) (assoc :concept/description (or (:description attr-map) (:new-term attr-map))
                                                          :concept/definition (or (:description attr-map) (:new-term attr-map))))
            (cond-> (contains? attr-map :ssyk) (assoc :concept.external-standard/ssyk-2012 (:ssyk attr-map)))
            (cond-> (contains? attr-map :sort) (assoc :concept.category/sort-order (:sort attr-map)))
            (cond-> (contains? attr-map :eures) (assoc :concept.external-standard/eures-code-2014 (:eures attr-map)))
            (cond-> (contains? attr-map :driving-licence-code)
              (assoc :concept.external-standard/driving-licence-code-2013 (:driving-licence-code attr-map)))
            (cond-> (contains? attr-map :nuts-3) (assoc :concept.external-standard/nuts-level-3-code-2013 (:nuts-3 attr-map)))
            (cond-> (contains? attr-map :country-code) (assoc :concept.external-standard/iso-639-3-alpha-3-2007 (:country-code attr-map)))
            (cond-> (contains? attr-map :legacy-id) (assoc :concept.external-database.ams-taxonomy-67/id (:legacy-id attr-map)))
            (cond-> (contains? attr-map :isco) (assoc :concept.external-standard/isco-08 (:isco attr-map)))
            (cond-> (contains? attr-map :sni) (assoc :concept.external-standard/sni-level-code-2007 (:sni attr-map))))]
    (concat [concept-with-extras])))

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

(defn get-only-relation-by-legacy-ids-and-types [legacy-id-1 legacy-id-2 type-1 type-2 relation-type]
  (ffirst (d/q get-only-relation-by-legacy-ids-and-types-query (conn/get-db) (str legacy-id-1) (str legacy-id-2) type-1 type-2 relation-type)))

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
    [(create-relation entity-1-id entity-2-id relation-type)]))

(defn replace-concept [replaced-legacy-id replacing-legacy-id type]
  (let [old-concept (get-entity-id-by-legacy-id replaced-legacy-id type)
        replaced-by-concept-id (get-entity-if-exists-or-temp-id replacing-legacy-id type)]
    {:db/id               old-concept
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
      [?r :concept.external-database.ams-taxonomy-67/id ?legacy-id]])


  (def get-datomic-attribute
    '[:find (pull ?e [*])
      :in $ ?entity-id
      :where
      [?e :concept.external-database.ams-taxonomy-67/id ?legacy-id]]
    )
  )






(defn find-duplicate-ids [stuff]

  (filter #(< 1 (second %))  (frequencies (map :concept/id (filter :concept/id stuff))))

  )
