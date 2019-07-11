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
             id)
    ;concept-term (u/create-term-from-concept concept) ;; TODO Remove since not being used
    ]
    [concept
     ;concept-term ;; TODO Remove since not being used
     ]))

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
        ;concept-term (u/create-term-from-concept concept-with-extras) ;; TODO Remove since not being used
        temp-id-parent (u/create-temp-id t/continent parent-id)
        relation (u/create-broader-relation-to-concept concept-with-extras temp-id-parent)]
    [concept-with-extras
     ;concept-term ;; TODO Remove since not being used
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
        ;concept-term (u/create-term-from-concept concept-with-extras) ;; TODO Remove since not being used
        temp-id-parent (u/create-temp-id t/country parent-id)
        relation (u/create-broader-relation-to-concept concept-with-extras temp-id-parent)]
    [concept-with-extras
     ;concept-term ;; TODO Remove since not being used
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
        ;concept-term (u/create-term-from-concept concept) ;; TODO Remove since not being used
        temp-id-parent (u/create-temp-id t/region parent-id)
        relation (u/create-broader-relation-to-concept concept temp-id-parent)]
    [concept
     ;concept-term ;; TODO Remove since not being used
     relation]))

(defn convert
  "Query SQL db for geographical places, convert each entity"
  []
  (concat
   (mapcat converter-continents (lm/fetch-data lm/get-continents))
   (mapcat converter-countries (lm/fetch-data lm/get-countries))
   (mapcat converter-regions (lm/fetch-data lm/get-EU-regions))
   (mapcat converter-municipalities (lm/fetch-data lm/get-municipalities))))
