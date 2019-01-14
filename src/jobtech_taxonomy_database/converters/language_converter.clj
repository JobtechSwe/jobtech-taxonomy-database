(ns jobtech-taxonomy-database.converters.language-converter
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
  [data]
  [{:concept/id (str  (:languageid_2 data))
    :concept/description       (:term data)
    :concept/preferred-term     (str  (:languageid_2 data))
    :concept/alternative-terms #{(str  (:languageid_2 data))}}
   {:db/id  (str  (:languageid_2 data))
    :term/base-form (:term data)}])

(defn convert
  ""
  []
  (mapcat language-converter  (language-retriever)))
