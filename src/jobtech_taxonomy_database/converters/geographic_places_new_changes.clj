(ns jobtech-taxonomy-database.converters.geographic-places-new-changes
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as lm]
            [jobtech-taxonomy-database.converters.converter-util :as u]
            [jobtech-taxonomy-database.types :as t]))

(defn convert-updated-country
  [{:keys [id-67 term-68 country-code-68]}]
  {:pre [id-67 term-68 country-code-68]}
  (let [entity-id (u/get-entity-id-by-legacy-id id-67 t/country)]
    (u/update-concept entity-id {:new-term term-68 :country-code country-code-68})))

(defn convert []
  "Run this function after the database has been loaded"
  (remove nil?
          (concat
           (mapcat convert-updated-country (lm/fetch-data lm/get-updated-country-term)))))
