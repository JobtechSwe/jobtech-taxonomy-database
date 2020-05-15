(ns jobtech-taxonomy-database.converters.driving-licence-converter
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as lm]
            [jobtech-taxonomy-database.converters.converter-util :as u]
            [jobtech-taxonomy-database.types :as t]))

(def driver-licence-id->implicit-ids
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

(defn implicit-licence-ref [old-id]
  {:db/id (u/create-temp-id t/driving-licence old-id)})

(defn driving-licence-converter [{:keys [term description drivinglicenceid drivinglicencecode displaysortorder]}]
  {:pre [term description drivinglicenceid drivinglicencecode displaysortorder]}
  (let [implicit-ids (driver-licence-id->implicit-ids drivinglicenceid)]
    (->> implicit-ids
         (map #(u/create-relation (u/create-temp-id t/driving-licence %)
                                  (u/create-temp-id t/driving-licence drivinglicenceid)
                                  t/broader))
         (cons (assoc (u/create-concept t/driving-licence term description drivinglicenceid)
                 :concept.external-standard/driving-licence-code-2013 drivinglicencecode
                 :concept/sort-order displaysortorder
                 :concept.external-standard/implicit-driving-licences (map implicit-licence-ref implicit-ids))))))

(defn convert
  "Query db for driving licences and driving licence combinations, convert each entity"
  []
  (mapcat driving-licence-converter (lm/fetch-data lm/get-driving-licence)))
