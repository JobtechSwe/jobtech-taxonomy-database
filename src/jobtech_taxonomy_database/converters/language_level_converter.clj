(ns jobtech-taxonomy-database.converters.language-level-converter
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as lm]
            [jobtech-taxonomy-database.converters.converter-util :as u]
            [jobtech-taxonomy-database.types :as t]))

(defn converter
  "Immutable language level converter."
  [{:keys [term languagelevelid]}]
  {:pre [term languagelevelid]}
  (let
   [concept (u/create-concept t/language-level term term languagelevelid)]
    [concept]))

(defn convert
  "Query db for language levels, convert each entity"
  []
  (mapcat converter (lm/fetch-data lm/get-language-level)))
