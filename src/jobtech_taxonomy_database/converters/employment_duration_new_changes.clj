(ns jobtech-taxonomy-database.converters.employment-duration-new-changes
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as lm]
            [jobtech-taxonomy-database.converters.converter-util :as u]
            [jobtech-taxonomy-database.types :as t]
            [jobtech-taxonomy-database.converters.employment-duration-converter :as og-converter]))

(defn convert-new
  [{:keys [id-68 term-68 sortering-68 eures-68]}]
  {:pre [id-68 term-68 sortering-68 eures-68]}
  (if (not (u/get-entity-id-by-legacy-id id-68 t/employment-duration))
    (og-converter/converter {:beteckning term-68
                             :anställningsvaraktighetsid id-68
                             :eureskod eures-68
                             :sortering sortering-68})))

(defn convert-updated
  [{:keys [id-67 term-68 sortering-68 eures-68]}]
  {:pre [id-67 term-68 sortering-68 eures-68]}
  (let [entity-id (u/get-entity-id-by-legacy-id id-67 t/employment-duration)]
    (u/update-concept entity-id {:new-term term-68 :sort sortering-68 :eures eures-68})))

(defn convert-deprecated
  [{:keys [id-67]}]
  {:pre [id-67]}
  (u/deprecate-concept t/employment-duration id-67))

(defn convert []
  "Run this function after the database has been loaded"
  (remove nil? (concat
                (mapcat convert-new (lm/fetch-data lm/get-new-employment-duration))
                (mapcat convert-updated (lm/fetch-data lm/get-updated-employment-duration)))))

