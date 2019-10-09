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
                    :concept.external-standard/sun-education-field-code-2020
                    :concept.external-standard/sun-education-level-code-2020
                    ])
    :in $
    :where [?c :concept/id]
    ;; [?c :concept.external-standard/sun-education-field-code-2020]
    ]

  )



(defn rename-concept-keys-for-json [concept]
  (set/rename-keys concept {:concept/id :conceptId,
                            :concept.external-database.ams-taxonomy-67/id :legacyAmsTaxonomyId,
                            :concept/preferred-label :preferredLabel, :concept/type :type}))


(defn kebab-case-type [concept]
  (update concept :type csk/->kebab-case-string))

(defn if-sun-code-set-it-as-legacy-id [concept]
  (cond
    (:concept.external-standard/sun-education-field-code-2020 concept)
    (-> concept
        (assoc :concept.external-database.ams-taxonomy-67/id (:concept.external-standard/sun-education-field-code-2020 concept))
        (dissoc :concept.external-standard/sun-education-field-code-2020)
        )
    (:concept.external-standard/sun-education-level-code-2020 concept)
       (-> concept
        (assoc :concept.external-database.ams-taxonomy-67/id (:concept.external-standard/sun-education-level-code-2020 concept))
        (dissoc :concept.external-standard/sun-education-level-code-2020)
        )
       :default concept
    )
  )


(defn parse-find-concept-datomic-result [result]
  (->> result
       (map first)
       (map if-sun-code-set-it-as-legacy-id)
       (map rename-concept-keys-for-json)
       (map kebab-case-type)))

(defn reduce-function [accum next]
  (let [type (:type next)
        id (:legacyAmsTaxonomyId next)]
    (assoc-in accum [type id] next)))

(defn fetch-concepts []
  (d/q find-concept-by-query (get-db))
  )

(defn dump-taxonomy-to-json []
  (generate-stream (reduce reduce-function {} (parse-find-concept-datomic-result
                                               (fetch-concepts)))
                   (clojure.java.io/writer "taxonomy-dump.json")
                   {:pretty true}))
