(ns jobtech-taxonomy-database.dump
  (:gen-class)
  (:require [datomic.client.api :as d]
            [clojure.set :as set]
            [clojure.java.io :as io]
            [clojure.walk :as walk]
            [jobtech-taxonomy-database.datomic-connection :refer :all]
            [cheshire.core :as json]
            [jobtech-taxonomy-database.config :as config]
            [jobtech-taxonomy-database.converters.SUN-education-field-legacy-converter :as sun-con]
            ))


(defn is-sun-2000 [concept]
  (or (:concept.external-standard/sun-education-field-code-2000 concept) (:concept.external-standard/sun-education-level-code-2000 concept))
  )

(defn create-sun-2000-key [legacy-id]
   (str sun-con/sun-legacy-key-prefix "-" legacy-id)
  )

(def find-concept-by-query
  '[:find (pull ?c [:concept/id
                    :concept/type
                    :concept/preferred-label
                    :concept.external-database.ams-taxonomy-67/id
                    :concept.external-standard/sun-education-field-code-2020
                    :concept.external-standard/sun-education-level-code-2020
                    :concept.external-standard/sun-education-field-code-2000
                    :concept.external-standard/sun-education-level-code-2000
                    ])
    :where [?c :concept/id]])

(defn fetch-concepts []
  (d/q find-concept-by-query (get-db))
  )


(defn rename-concept-keys-for-json [concept]
  (set/rename-keys concept {:concept/id :conceptId
                            :concept.external-database.ams-taxonomy-67/id :legacyAmsTaxonomyId
                            :concept.external-standard/sun-education-field-code-2020 :legacyAmsTaxonomyId
                            :concept.external-standard/sun-education-level-code-2020 :legacyAmsTaxonomyId
                            :concept/preferred-label :preferredLabel
                            :concept/type :type}))

(defn build-concept-mappings-reducer-fun [[acc-concept->taxonomy acc-taxonomy->concept] concept]
  [(assoc acc-concept->taxonomy  (:conceptId concept) (dissoc concept :conceptId))
   (if (is-sun-2000 concept)
     (assoc-in acc-taxonomy->concept [(:type concept) (create-sun-2000-key (:legacyAmsTaxonomyId concept))] concept)
     (assoc-in acc-taxonomy->concept [(:type concept) (:legacyAmsTaxonomyId concept)] concept)
     )
   ])

(defn dump-taxonomy-to-json []
  (let [[concept->taxonomy taxonomy->concept]
        (->> (fetch-concepts)
             (map first)
             (map rename-concept-keys-for-json)
             (reduce build-concept-mappings-reducer-fun [{} {}])
             (walk/postwalk #(cond->> % (map? %) (into (sorted-map))))
             )


        ]
    (json/generate-stream concept->taxonomy (io/writer "resources/concept-to-taxonomy.json") {:pretty true})
    (json/generate-stream taxonomy->concept (io/writer "resources/taxonomy-to-concept.json") {:pretty true})))

(defn -main [& args]
  (when-not (:datomic-name (config/get-datomic-config))
    (binding [*out* *err*]
      (println "Usage: lein run -m jobtech-taxonomy-database.dump 'my-database-name'"))
    (System/exit 1))
  (dump-taxonomy-to-json))
