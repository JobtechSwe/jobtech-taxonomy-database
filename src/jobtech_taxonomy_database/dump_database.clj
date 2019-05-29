(ns jobtech-taxonomy-database.dump-database
  (:gen-class)
  (:require [datomic.client.api :as d]
            [clojure.set :as set]
            [jobtech-taxonomy-database.config :refer :all]
            [jobtech-taxonomy-database.datomic-connection :refer :all :as conn]
            [cheshire.core :refer :all]
            )
  )

(def find-concept-by-query
  '[:find (pull ?c [:concept/id
                    ;  :concept/description
                    :concept/category
                    ;    :concept/deprecated
                    {:concept/preferred-term [:term/base-form]}
                    ;  {:concept/referring-terms [:term/base-form]}
                    :concept.external-database.ams-taxonomy-67/id
                    ])
    :in $
    :where [?c :concept/id]])

(defn rename-concept-keys-for-json [concept]
  (set/rename-keys concept {:concept/id :conceptId, :concept.external-database.ams-taxonomy-67/id :legacyAmsTaxonomyId, :concept/preferred-term :preferredLabel, :concept/category :type}))

(defn lift-term [concept]
  (assoc (dissoc concept :preferred-term)
    :concept/preferred-term (get-in concept [:concept/preferred-term :term/base-form])))

(defn parse-find-concept-datomic-result [result]
  (->> result
       (map first)
       (map #(lift-term %))
       ;  (map #(update % :concept/category name))
       (map rename-concept-keys-for-json)
       )
  )

(defn reduceFunction [accum next]
  (let [
        type (:type next)
        id (:legacyAmsTaxonomyId next)
        ]
    (
      assoc-in accum [type id] next)
    ))

;Taxonomy convertion map from old Taxonomy ids to new ones
(defn mappOldTaxonomyIdsToNewIds []
    (generate-stream (reduce reduceFunction {} (parse-find-concept-datomic-result (d/q find-concept-by-query (get-db)))) (clojure.java.io/writer "ny_taxonomy_to_concept.json"){:pretty true})
    )

