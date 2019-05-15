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
  "- Checks if the instance-type has a valid value
  - Returns nano-id"
  (nano/get-nano (csk/->kebab-case-string instance-type) (str legacy-id)))


(defn get-temp-id [instance-type legacy-id]
  (str (csk/->kebab-case-string instance-type)  "-" legacy-id))


(defn- get-concept-id-and-temp-id [instance-type legacy-id]
  [(get-concept-id instance-type legacy-id)
   (get-temp-id instance-type legacy-id)])


(defn create-concept
  ([instance-type label description legacy-id]
   (let [[concept-id temp-id] (get-concept-id-and-temp-id instance-type legacy-id)]
     (create-concept instance-type label description legacy-id concept-id temp-id)))
  ([instance-type label description legacy-id concept-id temp-id]
   {:pre [
          (s/valid? ::t/concept-types instance-type)
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
    :concept/instance-type                         instance-type
    :concept/category                             (csk/->kebab-case-keyword instance-type) ; deprecated this one
    :concept.external-database.ams-taxonomy-67/id (str legacy-id);; TODO rename attribute
    }))


(defn create-term [nano-id term]
  {:db/id          nano-id
   :term/base-form term})


(defn create-term-from-concept [ {:keys  [:concept/id :concept/preferred-label ]}]
  "never mind this, it's not going to be used"
  {:db/id id
   :term/base-form preferred-label})


(defn create-relation [concept1 concept2 type]
  "concept1 should be an entity-id OR temp-id"
  {:pre [(s/valid? ::t/relation-types type)]}
  {:relation/concept-1 concept1
   :relation/concept-2 concept2
   :relation/type type})


(defn create-broader-relation-to-concept [concept broader-temp-id]
  "inputs should be entity-ids OR temp-ids"
  (create-relation (:db/id concept) broader-temp-id t/broader ))


(def get-concept-by-legacy-id-query '[:find ?s
                                      :in $ ?legacy-id ?category
                                      :where
                                      [?s :concept.external-database.ams-taxonomy-67/id ?legacy-id]
                                      [?s :concept/category ?category]
                                      ])

#_
(def get-entity-id-query
  "This function should replace the above function (get-concept-by-legacy-id-query) in the future.
  Call function with legacy-id and type"
    '[:find ?s
    :in $ ?legacy-id ?type
    :where
    [?s :concept.external-database.ams-taxonomy-67/id ?legacy-id]
    [?s :concept/type ?type]
    ])

(defn get-concept-by-legacy-id [legacy-id category]
  (ffirst (d/q get-concept-by-legacy-id-query (conn/get-db) (str legacy-id) category)))


(def get-concept-by-attribute-and-value-query '[:find ?s
                                      :in $ ?attribute ?value ?category
                                      :where
                                      [?s ?attribute ?value]
                                      [?s :concept/category ?category]])


(defn get-concept-by-attribute-and-value [attribute value category]
  (ffirst (d/q get-concept-by-attribute-and-value-query (conn/get-db) attribute value category)))


(def get-preferred-term-and-enity-id-by-legacy-id-query '[:find ?term ?s
                                      :in $ ?legacy-id ?category
                                             :where
                                      [?t :term/base-form ?term]
                                      [?s :concept/preferred-term ?t]
                                      [?s :concept.external-database.ams-taxonomy-67/id ?legacy-id]
                                      [?s :concept/category ?category]])


(defn get-preferred-term-by-legacy-id [legacy-id category]
  (first (d/q get-preferred-term-and-enity-id-by-legacy-id-query (conn/get-db) legacy-id category)))


(defn get-concept [entity-id]
  (d/pull (conn/get-db)   [:concept/id
                          :concept/description
                          {:concept/preferred-term [:db/id :term/base-form]}
                          :concept/category
                          :concept.external-database.ams-taxonomy-67/id]
          entity-id))


(defn get-entity-if-exists-or-temp-id [legacy-id category]
  (if-let [entity-id (get-concept-by-legacy-id legacy-id category)]
    entity-id
    (str  (name category) "-" legacy-id)))


(defn deprecate-concept [category legacy-id]
  {:pre [legacy-id category]}
  (if-let [entity-id (get-concept-by-legacy-id (str legacy-id) category)]
    {:db/id entity-id  :concept/deprecated true  }))


(defn update-preferred-term [new-preferred-term old-preferred-term-id entity-id]
  "refactor this later when all concepts has alternative-terms"
  (let [temp-id (str (gensym))]
    [
     (create-term temp-id new-preferred-term)
     {:db/id entity-id
      :concept/description new-preferred-term
      :concept/preferred-term temp-id
      :concept/alternative-terms [old-preferred-term-id]}]))
