(ns jobtech-taxonomy-database.converters.geographic-places-converter
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as lm]
            [jobtech-taxonomy-database.converters.converter-util :as u]
            [jobtech-taxonomy-database.types :as t]))

(defn converter-continents
  "Convert continents"
  [{:keys [term id]}]
  {:pre [term id]}
  (let
   [concept (u/create-concept
             t/continent
             term
             term
             id)]
    [concept]))

(defn converter-countries
  "Convert countries"
  [{:keys [term id code parent-id]}]
  {:pre [term id code parent-id]}
  (let [concept (u/create-concept
                 t/country
                 term
                 term
                 id)
        concept-with-extras (assoc concept :concept.external-standard/country-code code)
        temp-id-parent (u/create-temp-id t/continent parent-id)
        relation (u/create-broader-relation-to-concept concept-with-extras temp-id-parent)]
    [concept-with-extras
     relation]))

(defn converter-regions
  "Convert EU regions (NUTS level 3)"
  [{:keys [term id code parent-id]}]
  {:pre [term id parent-id]}
  (let [concept (u/create-concept
                 t/region
                 term
                 term
                 id)
        concept-with-extras (conj concept (when code [:concept.external-standard/nuts-level-3-code code]))
        temp-id-parent (u/create-temp-id t/country parent-id)
        relation (u/create-broader-relation-to-concept concept-with-extras temp-id-parent)]
    [concept-with-extras
     relation]))

(defn converter-municipalities
  "Convert municipalities"
  [{:keys [term id parent-id]}]
  {:pre [term id parent-id]}
  (let [concept (u/create-concept
                 t/municipality
                 term
                 term
                 id)
        temp-id-parent (u/create-temp-id t/region parent-id)
        relation (u/create-broader-relation-to-concept concept temp-id-parent)]
    [concept
     relation]))

(defn convert
  "Query SQL db for geographical places, convert each entity"
  []
  (concat
   (mapcat converter-continents (lm/fetch-data lm/get-continents))
   (mapcat converter-countries (lm/fetch-data lm/get-countries))
   (mapcat converter-regions (lm/fetch-data lm/get-EU-regions)) ;; The data is faulty so Im not importing it yet
   (mapcat converter-municipalities (lm/fetch-data lm/get-municipalities))))
