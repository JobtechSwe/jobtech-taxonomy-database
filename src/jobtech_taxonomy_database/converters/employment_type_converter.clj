(ns jobtech-taxonomy-database.converters.employment-type-converter
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as lm]
            [jobtech-taxonomy-database.converters.converter-util :as u]
            [jobtech-taxonomy-database.types :as t]))

(defn converter
  "Immutable employment types converter."
  [{:keys [beteckning anstallningtypjobbid isortering]}]
  {:pre [beteckning anstallningtypjobbid]}
  (let
   [concept (u/create-concept t/employment-type beteckning beteckning anstallningtypjobbid)
    concept-with-extras (assoc concept
                               :concept.category/sort-order isortering)
    ]
    [concept-with-extras
     ]))

(defn convert
  "Query db for employment types, convert each entity"
  []
  (mapcat converter (lm/fetch-data lm/get-employment-type)))
