(ns jobtech-taxonomy-database.dump-database
  (:gen-class)
  (:require [datomic.client.api :as d]
            [clojure.set :as set]
            [camel-snake-kebab.core :as csk]
            [jobtech-taxonomy-database.config :refer :all]
            [jobtech-taxonomy-database.datomic-connection :refer :all :as conn]
            [cheshire.core :refer :all]))

(def find-concept-by-query
  '[:find (pull ?c [:concept/id
                    :concept/type
                    :concept/preferred-label
                    :concept.external-database.ams-taxonomy-67/id
                    ])
    :in $
    :where [?c :concept/id]])

(defn rename-concept-keys-for-json [concept]
  (set/rename-keys concept {:concept/id :conceptId, :concept.external-database.ams-taxonomy-67/id :legacyAmsTaxonomyId, :concept/preferred-label :preferredLabel, :concept/type :type}))

(defn kebab-case-type [concept]
  (update concept :type csk/->kebab-case-string)
  )

(defn parse-find-concept-datomic-result [result]
  (->> result
       (map first)
       (map rename-concept-keys-for-json)
       (map kebab-case-type)
       ))

(defn reduce-function [accum next]
  (let [type (:type next)
        id (:legacyAmsTaxonomyId next)]
    (assoc-in accum [type id] next)))

;Taxonomy convertion map from old Taxonomy ids to new ones


(defn map-old-taxonomy-ids-to-new-ids []
  (generate-stream (reduce reduce-function {} (parse-find-concept-datomic-result (d/q find-concept-by-query (get-db)))) (clojure.java.io/writer "ny_taxonomy_to_concept.json") {:pretty true}))
