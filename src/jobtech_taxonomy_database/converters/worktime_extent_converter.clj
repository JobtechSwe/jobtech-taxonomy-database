(ns jobtech-taxonomy-database.converters.worktime-extent-converter
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as lm]
            [jobtech-taxonomy-database.converters.converter-util :as u]
            [jobtech-taxonomy-database.types :as t]))

(defn converter
  "Immutable work time extent converter."
  [{:keys [beteckning arbetstidsid sortering]}]
  {:pre [beteckning arbetstidsid sortering]}
  (let
   [concept (u/create-concept t/worktime-extent beteckning beteckning arbetstidsid)
    concept-with-extras (assoc concept
                               :concept.category/sort-order sortering)]
    [concept-with-extras]))

(defn convert
  "Query db for work time extent, convert each entity"
  []
  (mapcat converter (lm/fetch-data lm/get-worktime-extent)))
