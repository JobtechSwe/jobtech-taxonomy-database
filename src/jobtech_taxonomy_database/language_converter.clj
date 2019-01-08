(ns jobtech-taxonomy-database.language-converter
  (:gen-class)
  (:require [datomic.client.api :as d]
            [jobtech-taxonomy-database.schema :refer :all :as schema]
            [jobtech-taxonomy-database.legacy-migration :refer :all]
            [jobtech-taxonomy-database.config :refer :all]
            [jobtech-taxonomy-database.datomic-connection :refer :all :as conn]))


(defn ^:private fake-id
  "Temporary function until new id function with nano-ids is imported into this project."
  [id]
  (format "%010d" id))

;todo remove below
(def bafar2
  [#:term{:base-form "BAfar"}])

(def bafar1
  [#:concept{:id (str 241),
             :description "BAfar",
             :preferred-term [:term/base-form "BAfar"],
             :alternative-terms #{[:term/base-form "BAfar"]}}
   ])

(defn language-retriever
  "Get all languages from the old database."
  []
  (fetch-data get-language)
  )

(defn language-converter
  "Immutable language converter."
  [language]
  [{:concept/id               (:languageid_2 language)
   :concept/description       (:term language)
   :concept/preferred-term    [:term/base-form (:term language)]
   :concept/alternative-terms #{[:term/base-form (:term language)]}}
  {:term/base-form (:term language)}]
  )

(defn convert [] (map language-converter (language-retriever)))

(defn language-writer
  [data]
  (run! (fn [t]
          (d/transact (get-conn) {:tx-data (vec (list t))}))
        data))