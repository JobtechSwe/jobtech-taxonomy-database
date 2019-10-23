(ns jobtech-taxonomy-database.converters.driving-licence-converter
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as lm]
            [jobtech-taxonomy-database.converters.converter-util :as u]
            [jobtech-taxonomy-database.types :as t]))

(def combination-mapping ;; key is drivers licence ID, value array contains implicit IDs
  {16 []
   2 [16]
   17 [16 2]
   10 [16 2 17]
   3 [16]
   18 [16 3]
   6 [16 3 18]
   19 [16 3]
   15 [16 3 18 6 19]
   5 [16 3 19]
   8 [16 3 18 6 19 15 5]
   12 [16 3]
   14 [16 3 18 6 12]
   4 [16 3 12]
   7 [16 3 12 6 12 14 4]})

(defn format-implicit-licences
  ""
  [old-id]
  {:db/id (u/create-temp-id t/driving-licence old-id)})

(defn driving-licence-converter
  "Immutable driving licence converter."
  [{:keys [term description drivinglicenceid drivinglicencecode displaysortorder]}]
  {:pre [term description drivinglicenceid drivinglicencecode displaysortorder]}
  (let
   [implicit-licences (map format-implicit-licences (combination-mapping drivinglicenceid))
    concept (u/create-concept t/driving-licence term description drivinglicenceid)
    concept-with-extras (assoc concept
                               :concept.external-standard/driving-licence-code-2013 drivinglicencecode
                               :concept.category/sort-order displaysortorder)
    concept-with-extra-extras (conj concept-with-extras (when (not-empty implicit-licences)
                                                          [:concept.implicit-driving-licences implicit-licences]))]
    [concept-with-extra-extras]))

(defn convert
  "Query db for driving licences and driving licence combinations, convert each entity"
  []
  (mapcat driving-licence-converter (lm/fetch-data lm/get-driving-licence)))
