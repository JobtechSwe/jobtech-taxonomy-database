(ns jobtech-taxonomy-database.converters.driving-licence-converter
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as lm]
            [jobtech-taxonomy-database.converters.converter-util :as u]
            [jobtech-taxonomy-database.types :as t]))

(defn combination-to-relation [{:keys [kombinationsid körkortsid]}]
  (let [temp-id-1 (u/create-temp-id t/driving-licence-combination kombinationsid)
        temp-id-2 (u/create-temp-id t/driving-licence körkortsid)
        relation (u/create-relation temp-id-2 temp-id-1 t/broader)]
    relation))

(defn convert-driving-licence-combination-grouped [grouped-combinations]
  (let [combination-id (first grouped-combinations)
        temp-id (u/create-temp-id t/driving-licence-combination combination-id)
        relations (map combination-to-relation (second grouped-combinations))
        concept (u/create-concept t/driving-licence-combination temp-id temp-id combination-id)
        concept-term (u/create-term-from-concept concept)]
    (concat
     relations
     [concept
      concept-term])))

(defn driving-licence-converter
  "Immutable driving licence converter."
  [{:keys [term description drivinglicenceid drivinglicencecode displaysortorder]}]
  {:pre [term description drivinglicenceid drivinglicencecode displaysortorder]}
  (let
    [concept (u/create-concept t/driving-licence term description drivinglicenceid)
     concept-with-extras (assoc concept
                           :concept.external-standard/driving-licence-code drivinglicencecode
                           :concept.category/sort-order displaysortorder)
     concept-term (u/create-term-from-concept concept-with-extras)]
    [concept-with-extras concept-term]))

(defn convert
  "Query db for driving licences and driving licence combinations, convert each entity"
  []
  (concat
   (mapcat driving-licence-converter (lm/fetch-data lm/get-driving-licence))
   (mapcat convert-driving-licence-combination-grouped
           (group-by :kombinationsid (lm/fetch-data lm/get-driving-licence-combination)))))
