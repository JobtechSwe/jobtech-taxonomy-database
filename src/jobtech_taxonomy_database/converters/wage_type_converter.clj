(ns jobtech-taxonomy-database.converters.wage-type-converter
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as lm]
            [jobtech-taxonomy-database.converters.converter-util :as u]
            [jobtech-taxonomy-database.types :as t]))

(defn converter
  "Immutable wage type converter."
  [{:keys [beteckning löneformsid sortering]}]
  {:pre [beteckning löneformsid sortering]}
  (let
   [concept (u/create-concept t/wage-type beteckning beteckning löneformsid)
    concept-with-extras (assoc concept
                               :concept/sort-order sortering)]
    [concept-with-extras]))

(defn convert
  "Query db for wage types, convert each entity"
  []
  (mapcat converter (lm/fetch-data lm/get-wage-type)))
