(ns jobtech-taxonomy-database.converters.employment-duration-converter
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as lm]
            [jobtech-taxonomy-database.converters.converter-util :as u]
            [jobtech-taxonomy-database.types :as t]))

(defn converter
  "Immutable employment duration converter."
  [{:keys [beteckning anställningsvaraktighetsid eureskod sortering]}]
  {:pre [beteckning anställningsvaraktighetsid]}
  (let
   [concept (u/create-concept t/employment-duration beteckning beteckning anställningsvaraktighetsid)
    concept-with-extras (assoc concept
                               :concept.external-standard/eures-code eureskod
                               :concept.category/sort-order sortering)]
    [concept-with-extras]))

(defn convert
  "Query db for employment duration, convert each entity"
  []
  (mapcat converter (lm/fetch-data lm/get-employment-duration)))
