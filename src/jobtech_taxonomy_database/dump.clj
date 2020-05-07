(ns jobtech-taxonomy-database.dump
  (:gen-class)
  (:require [datahike.api :as d]
            [clojure.set :as set]
            [clojure.java.io :as io]
            [clojure.walk :as walk]
            [jobtech-taxonomy-database.datomic-connection :refer :all]
            [cheshire.core :as json]
            [jobtech-taxonomy-database.config :as config]))

(def find-concept-by-query
  '[:find (pull ?c [:concept/id
                    :concept/type
                    :concept/preferred-label
                    :concept.external-database.ams-taxonomy-67/id
                    :concept.external-standard/sun-education-field-code-2020
                    :concept.external-standard/sun-education-level-code-2020])
    :where [?c :concept/id]])

(defn rename-concept-keys-for-json [concept]
  (set/rename-keys concept {:concept/id :conceptId
                            :concept.external-database.ams-taxonomy-67/id :legacyAmsTaxonomyId
                            :concept.external-standard/sun-education-field-code-2020 :legacyAmsTaxonomyId
                            :concept.external-standard/sun-education-level-code-2020 :legacyAmsTaxonomyId
                            :concept/preferred-label :preferredLabel
                            :concept/type :type}))

(defn dump-taxonomy-to-json []
  (let [[concept->taxonomy taxonomy->concept]
        (->> (d/q find-concept-by-query (get-db))
             (map first)
             (map rename-concept-keys-for-json)
             (reduce (fn [[acc-concept->taxonomy acc-taxonomy->concept] concept]
                       [(assoc acc-concept->taxonomy (:conceptId concept) (dissoc concept :conceptId))
                        (assoc-in acc-taxonomy->concept [(:type concept) (:legacyAmsTaxonomyId concept)] concept)])
                     [{} {}])
             (walk/postwalk #(cond->> % (map? %) (into (sorted-map)))))]
    (json/generate-stream concept->taxonomy (io/writer "resources/concept-to-taxonomy.json") {:pretty true})
    (json/generate-stream taxonomy->concept (io/writer "resources/taxonomy-to-concept.json") {:pretty true})))

(defn -main [& args]
  (when-not (:datomic-name (config/get-datomic-config))
    (binding [*out* *err*]
      (println "Usage: lein run -m jobtech-taxonomy-database.dump 'my-database-name'"))
    (System/exit 1))
  (dump-taxonomy-to-json))