(ns jobtech-taxonomy-database.dump-database
  (:gen-class)
  (:require [datomic.client.api :as d]
            [clojure.set :as set]
            [camel-snake-kebab.core :as csk]
            [jobtech-taxonomy-database.config :refer :all]
            [jobtech-taxonomy-database.datomic-connection :refer :all :as conn]
            [cheshire.core :refer :all]
            [jobtech-taxonomy-database.legacy-migration :as lm]
            ))

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
                   (clojure.java.io/writer "taxonomy-dump-new.json")
                   {:pretty true}))

;; Convert taxonomy-dump.json municipality and ssyk_level_4 id to concept-to-taxonomy-v1.json concept id


;;;;;;;;;;;;;;;;; CONVERT FAULTY CONCEPT ID FILE :::::::::::::::::

(defn slurp-json-file [filename]
  (parse-string (slurp filename) true ))


;; (first (slurp-json-file "resources/taxonomy_to_concept_v67.json"))
;; (first (slurp-json-file "resources/taxonomy-dump.json"))


;; iterate over tax-dump use the legacy id to lookup the concept in the database
;; fetch external standard ssyk2012 or municipality code
;; lookup concept id in taxonomy-to-concept-v67
;; update concept id in taxonomy-dump.json

(defn get-old-dump []
  (slurp-json-file "resources/taxonomy_to_concept_v67.json")
  )

(defn get-newer-dump []
  ;;(slurp-json-file "resources/taxonomy-dump.json")
  (slurp-json-file "taxonomy-dump-new.json")
  )


(def old-dump (memoize get-old-dump))
(def newer-dump (memoize get-newer-dump))

(defn get-ssyk-level-4-from-new-dump []
  (:ssyk-level-4 (get-newer-dump)))


(defn fetch-municipalities []
  (lm/fetch-data lm/get-municipalities))


(defn fetch-regions []
  (lm/fetch-data lm/get-EU-regions)
  )


(defn fetch-ssyk-level-4 []
  (lm/fetch-data lm/get-ssyk-4)
  )


(defn get-legacydb-id-from-ssyk-4 [ssyk4]
  (:ssyk-4-id (first (filter (fn [{:keys [ssyk-4-code]}]  (= ssyk4 ssyk-4-code)  ) (fetch-ssyk-level-4))))
  )


(defn get-legacydb-id-from-municipality-code [old-code]
  (:id (first (filter (fn [{:keys [national-code]}]  (= old-code national-code)  ) (fetch-municipalities))))
  )


(defn get-legacydb-id-from-region-code [old-code]
  (:id (first (filter (fn [{:keys [national-code]}]  (= old-code national-code)  ) (fetch-regions))))
  )

(defn get-ssyk-4-from-legacydb-id [id]
  (:ssyk-4-code(first (filter (fn [{:keys [ssyk-4-id]}]  (= id ssyk-4-id)  ) (fetch-ssyk-level-4))))
  )

(defn get-municipality-code-from-legacy-id [legacy-id]
  (:national-code (first (filter (fn [{:keys [id]}]  (= id legacy-id)  ) (fetch-municipalities))))
  )

(defn get-region-code-from-legacy-id [legacy-id]
  (:national-code (first (filter (fn [{:keys [id]}]  (= id legacy-id)  ) (fetch-regions))))
  )

(defn get-right-drivers-license-concept-id-from-legacy-id [[legacy-id concept]]
  (let [driving-license (:driving-license (old-dump))
        concept (legacy-id driving-license)
        id (:conceptId concept)
        ]
    id
    )
  )

(defn get-code-from-type-and-legacy-db-id [legacy-db-id type]
  (cond
    (= type "ssyk-level-4") (get-ssyk-4-from-legacydb-id (Long/parseLong legacy-db-id))
    (= type "municipality") (get-municipality-code-from-legacy-id (Long/parseLong legacy-db-id))
    (= type "region") (get-region-code-from-legacy-id (Long/parseLong legacy-db-id))
    (= type "driving-licence")  legacy-db-id
    )
  )

(defn get-right-concept-id-from-new-dump [[legacy-db-id concept]]
  (let [id (name legacy-db-id)
        type (:type concept)
        old-type (cond
                   (= type "ssyk-level-4") "occupation-group"
                   (= type "region") "county"
                   (= type "driving-licence") "driving-license"
                   :else type
                   )

        code (get-code-from-type-and-legacy-db-id id type)

        right-concept (if code
                        (let [old-concept ((keyword code) ((keyword old-type) (get-old-dump)))
                              right-concept-id (:conceptId old-concept)
                              ]
                          (if right-concept-id
                            (assoc concept :conceptId right-concept-id )
                            concept
                            )
                          )
                        )
        ]
    [legacy-db-id right-concept]
    )
  )


(defn update-new-dump-with-right-concept-ids []
  (let [ssyk-level-4 (:ssyk-level-4 (newer-dump))
        municipality (:municipality (newer-dump))
        region (:region (newer-dump))
        driving-licence (:driving-licence (newer-dump))

        right-ssyk-level-4 (into {} (map get-right-concept-id-from-new-dump ssyk-level-4))
        right-municipality (into {} (map get-right-concept-id-from-new-dump municipality))
        right-region (into {} (map get-right-concept-id-from-new-dump region))
        right-driving-licence (into {} (map get-right-concept-id-from-new-dump driving-licence))

        newer-dump-with-right-concept-ids (-> (newer-dump)
                                              (assoc :ssyk-level-4 right-ssyk-level-4)
                                              (assoc :municipality right-municipality)
                                              (assoc :region right-region)
                                              (assoc :driving-licence right-driving-licence)
                                              )

        ]
    newer-dump-with-right-concept-ids
    )
  )

(defn save-newer-dump-to-disk []
  (generate-stream (update-new-dump-with-right-concept-ids) (clojure.java.io/writer "right-taxonomy-dump-new.json")
                   {:pretty true})
  )
