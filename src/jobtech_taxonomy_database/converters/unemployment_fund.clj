(ns jobtech-taxonomy-database.converters.unemployment-fund
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as lm]
            [jobtech-taxonomy-database.converters.converter-util :as u]
            [jobtech-taxonomy-database.types :as t]))

(defn convert-unemployment-fund
  "Convert a Swedish unemployment fund"
  [{:keys [beteckning akasseid akassekod]}]
  {:pre [beteckning akasseid akassekod]}
  (let
    [concept (u/create-concept
       t/unemployment-fund beteckning beteckning akasseid)
     concept-with-extras (assoc
       concept :concept.external-standard/unemployment-fund-code-2017 akassekod)
    ]
    concept-with-extras))

(defn convert
  "Query db for Swedish unemployment funds"
  []
  (concat (map convert-unemployment-fund
    (lm/fetch-data lm/get-unemployment-fund))))
