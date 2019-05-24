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
  "Use this insteda of the above two separately"
  [(get-concept-id instance-type legacy-id)
   (create-temp-id instance-type legacy-id)])


(defn create-concept
  ([instance-type label description legacy-id]
   (let [[concept-id temp-id] (get-concept-id-and-temp-id instance-type legacy-id)]
     (create-concept instance-type label description legacy-id concept-id temp-id)))
  ([instance-type label description legacy-id concept-id temp-id]
   {:pre [(s/valid? ::t/concept-types instance-type)
          (s/valid? string? concept-id)
          (s/valid? string? temp-id)
          (s/valid? string? label)
          (s/valid? string? description)
          (or (s/valid? string? legacy-id)(s/valid? int? legacy-id))]}
   {:db/id                                        temp-id
    :concept/id                                   concept-id
    :concept/description                          description
    :concept/preferred-label                      label
    :concept/preferred-term                       concept-id  ; deprecated this one
    :concept/alternative-terms                    #{concept-id}
    :concept/type                                 instance-type
    :concept/category                             (csk/->kebab-case-keyword instance-type) ; deprecated this one
    :concept.external-database.ams-taxonomy-67/id (str legacy-id) ;; TODO rename attribute
    }))


(defn create-term [nano-id term]
  "never mind this, it's not going to be used"
  {:db/id          nano-id
   :term/base-form term})


(defn create-term-from-concept [ {:keys  [:concept/id :concept/preferred-label ]}]
  "Use this instead of above"
  {:db/id id
   :term/base-form preferred-label})


(defn create-relation [concept1 concept2 type]
  "Both concepts should be an entity-id OR temp-id"
  {:pre [(s/valid? ::t/relation-types type)]}
  {:relation/concept-1 concept1
   :relation/concept-2 concept2
   :relation/type type})


(defn create-broader-relation-to-concept [concept broader-temp-id]
  "First input should be a concept, second an entity-ids OR a temp-ids"
  (create-relation (:db/id concept) broader-temp-id t/broader ))

(defn create-narrower-relation-to-concept [concept narrower-temp-id]
  "First input should be a concept, second an entity-ids OR a temp-ids"
  (create-relation (:db/id concept) narrower-temp-id t/narrower ))


(def get-entity-id-by-legacy-id-query '[:find ?s
                                      :in $ ?legacy-id ?type
                                      :where
                                      [?s :concept.external-database.ams-taxonomy-67/id ?legacy-id]
                                      [?s :concept/type ?type]
                                      ])


(defn get-entity-id-by-legacy-id [legacy-id type]
  "Use this, it calls above"
  (ffirst (d/q get-entity-id-by-legacy-id-query (conn/get-db) (str legacy-id) type)))


(def get-entity-id-by-attribute-and-value-query '[:find ?s
                                      :in $ ?attribute ?value ?category
                                      :where
                                      [?s ?attribute ?value]
                                      [?s :concept/category ?category]])


(defn get-concept-by-attribute-and-value [attribute value category]
  "Use this, it calls above"
  (ffirst (d/q get-entity-id-by-attribute-and-value-query (conn/get-db) attribute value category)))


(def get-preferred-term-and-enity-id-by-legacy-id-query '[:find ?term ?s
                                      :in $ ?legacy-id ?category
                                             :where
                                      [?t :term/base-form ?term]
                                      [?s :concept/preferred-term ?t]
                                      [?s :concept.external-database.ams-taxonomy-67/id ?legacy-id]
                                      [?s :concept/category ?category]])


(defn get-preferred-term-by-legacy-id [legacy-id type]
  "Use this, it calls above"
  (first (d/q get-preferred-term-and-enity-id-by-legacy-id-query (conn/get-db) legacy-id type)))


(defn get-concept [entity-id]
  (d/pull (conn/get-db)   [:concept/id
                          :concept/description
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
    {:db/id entity-id  :concept/deprecated true  }))

(defn update-concept [entity-id attr-map]
  (let [temp-id (str (gensym))
        concept {:db/id entity-id}
        concept-with-extras
        (-> concept
            (cond-> (contains? attr-map :new-term)
                    (assoc :concept/preferred-label (:new-term attr-map)
                           :concept/preferred-term temp-id))
            (cond-> (contains? attr-map :description) (assoc :concept/description (:description attr-map))
                    (contains? attr-map :new-term) (assoc :concept/description (:new-term attr-map)))
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
    [concept-with-extras (if (contains? attr-map :new-term) {:db/id temp-id
                                                             :term/base-form (:new-term attr-map)} )]))


(defn update-preferred-term [entity-id new-preferred-term description]
  (let [temp-id (str (gensym))]
    [
     (create-term temp-id new-preferred-term)
     {:db/id entity-id
      :concept/description description
      :concept/preferred-term temp-id
      :concept/preferred-label new-preferred-term
      }]))
