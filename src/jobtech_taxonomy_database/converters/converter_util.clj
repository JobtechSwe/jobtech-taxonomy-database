(ns jobtech-taxonomy-database.converters.converter-util
  (:gen-class)
  (:require [datomic.client.api :as d]
            [jobtech-taxonomy-database.legacy-migration :as legacy-migration]
            [jobtech-taxonomy-database.converters.nano-id-assigner :as nano-id-assigner]
            [jobtech-taxonomy-database.datomic-connection :refer :all :as conn]
            ))


(defn create-concept [nano-id temp-id term description category legacy-ams-db-id]
  {:db/id                                        temp-id
   :concept/id                                   nano-id
   :concept/description                          description
   :concept/preferred-label                      term
   :concept/preferred-term                       nano-id
   :concept/alternative-terms                    #{nano-id}
   :concept/category                             category
   :concept.external-database.ams-taxonomy-67/id (str legacy-ams-db-id);; todo rename attribute
   }
  )

(defn create-term [nano-id term]
  {:db/id          nano-id
   :term/base-form term}
  )

(defn create-relation [concept1 concept2 type]
  {:relation/concept-1 concept1
   :relation/concept-2 concept2
   :relation/type type
   }
  )

(def get-concept-by-legacy-id-query '[:find ?s
                                      :in $ ?legacy-id ?category
                                      :where
                                      [?s :concept.external-database.ams-taxonomy-67/id ?legacy-id]
                                      [?s :concept/category ?category]
                                      ])

(defn get-concept-by-legacy-id [legacy-id category]
  (ffirst (d/q get-concept-by-legacy-id-query (conn/get-db) (str legacy-id) category))
  )


(def get-concept-by-attribute-and-value-query '[:find ?s
                                      :in $ ?attribute ?value ?category
                                      :where
                                      [?s ?attribute ?value]
                                      [?s :concept/category ?category]
                                      ])

(defn get-concept-by-attribute-and-value [attribute value category]
  (ffirst (d/q get-concept-by-attribute-and-value-query (conn/get-db) attribute value category))
  )

(def get-preferred-term-and-enity-id-by-legacy-id-query '[:find ?term ?s
                                      :in $ ?legacy-id ?category
                                             :where
                                      [?t :term/base-form ?term]
                                      [?s :concept/preferred-term ?t]
                                      [?s :concept.external-database.ams-taxonomy-67/id ?legacy-id]
                                      [?s :concept/category ?category]
                                      ])

(defn get-preferred-term-by-legacy-id [legacy-id category]
  (first (d/q get-preferred-term-and-enity-id-by-legacy-id-query (conn/get-db) legacy-id category))
  )



(defn get-concept [entity-id]
  (d/pull (conn/get-db)   [:concept/id
                          :concept/description
                          {:concept/preferred-term [:db/id :term/base-form]}
                          :concept/category
                          :concept.external-database.ams-taxonomy-67/id]
          entity-id)
  )


(defn get-entity-if-exists-or-temp-id [legacy-id category]
  (if-let [entity-id (get-concept-by-legacy-id legacy-id category)]
    entity-id
    (str  (name category) "-" legacy-id)
    )
  )


(defn deprecate-concept [category legacy-id]
  {:pre [legacy-id category]}
  (if-let [entity-id (get-concept-by-legacy-id (str legacy-id) category)]
    {:db/id entity-id  :concept/deprecated true  }
    )
  )

(defn update-preferred-term [new-preferred-term old-preferred-term-id entity-id]
  "refactor this later when all concepts has alternative-terms"
  (let [temp-id (str (gensym))]
    [
     (create-term temp-id new-preferred-term)
     {:db/id entity-id
      :concept/description new-preferred-term
      :concept/preferred-term temp-id
      :concept/alternative-terms [old-preferred-term-id]
      }
     ])
  )
