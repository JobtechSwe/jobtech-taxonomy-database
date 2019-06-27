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

(defn retract-country-relation-to-continent
  [{:keys [country-id-67
           parent-continent-id-67]}]
  (u/retract-relation-by-legacy-ids-and-types
   country-id-67
   t/country
   parent-continent-id-67
   t/continent
   t/broader))

(defn convert-new-isco-skill-relations
  [{:keys [country-id-68
           parent-continent-id-68]}]
  (u/get-new-relation-by-legacy-ids-and-types
   country-id-68
   t/country
   parent-continent-id-68
   t/continent
   t/broader))

(defn convert []
  "Run this function after the database has been loaded"
  (remove nil?
          (concat
           (mapcat convert-updated-country (lm/fetch-data lm/get-updated-country))
           (mapcat retract-country-relation-to-continent (lm/fetch-data lm/get-deprecated-country-relation-to-continent))
           (mapcat convert-new-isco-skill-relations (lm/fetch-data lm/get-new-country-relation-to-continent)))))
