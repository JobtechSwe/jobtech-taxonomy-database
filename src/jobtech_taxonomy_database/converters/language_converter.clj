(ns jobtech-taxonomy-database.converters.language-converter
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as lm]
            [jobtech-taxonomy-database.converters.converter-util :as u]
            [jobtech-taxonomy-database.types :as t]))

(defn converter
  "Immutable language converter."
  [{:keys [term languageid_2]}]
  {:pre [term languageid_2]}
  (let
    [concept (u/create-concept t/language term term languageid_2)
     concept-term (u/create-term-from-concept concept)]
    [concept concept-term]
    ))

(defn convert
  "Query db for languages, convert each entity"
  []
  (mapcat converter (lm/fetch-data lm/get-language)))
