(ns jobtech-taxonomy-database.converters.employment-duration-new-changes
  (:gen-class)
  (:require [jobtech-taxonomy-database.converters.converter-util :as u]
            [jobtech-taxonomy-database.types :as t]
            [jobtech-taxonomy-database.converters.employment-duration-converter :as og-converter]))

(def updates-68-term ;;key is ID, value is term
  {9 "12 månader eller längre"
   2 "6 månader – upp till 12 månader"
   3 "3 månader – upp till 6 månader"
   7 "11 dagar - upp till 3 månader"
   8 "Upp till 10 dagar"})

(def updates-68-eures ;;key is ID, value is EURESkod
  {9 "PF"
   2 "PF"
   3 "TF"
   7 "TF"
   8 "TF"})

(def updates-68-sort  ;;key is ID, value is sortering
  {9 1
   2 2
   3 3
   7 4
   8 5})

(defn convert-new
  []
  (og-converter/converter {:beteckning (updates-68-term 9)
                           :anställningsvaraktighetsid 9
                           :eureskod (updates-68-eures 9)
                           :sortering (updates-68-sort 9)}))

(defn convert-deprecated
  []
  [(u/deprecate-concept t/employment-duration 1)])

(defn convert-updated
  [id]
  (let [term-68 (updates-68-term id)
        sortering-68 (updates-68-sort id)
        eures-68 (updates-68-eures id)
        entity-id (u/get-entity-id-by-legacy-id id t/employment-duration)]
    (u/update-concept entity-id {:new-term term-68 :sort sortering-68 :eures eures-68})))

(defn convert []
  "Run this function after the database has been loaded"
  (remove nil? (concat
                (convert-new)
                (convert-deprecated)
                (mapcat convert-updated [2 3 7 8]))))
