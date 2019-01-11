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

(defn language-retriever
  "Get all languages from the old database."
  []
  (fetch-data get-language))

(defn language-converter
  "Immutable language converter."
  [language]
  [{:concept/id (str  (:languageid_2 language))
    :concept/description       (:term language)
    :concept/preferred-term     (str  (:languageid_2 language))
    :concept/alternative-terms #{(str  (:languageid_2 language))}}
   {:db/id  (str  (:languageid_2 language))
    :term/base-form (:term language)}])

(defn convert [] (mapcat language-converter  (language-retriever)))

(defn language-writer
  [data]

  (d/transact (get-conn) {:tx-data data}))
