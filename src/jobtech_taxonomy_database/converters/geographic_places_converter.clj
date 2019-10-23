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
  [{:keys [term id code parent-id code3]}]
  {:pre [term id code parent-id code3]}
  (let [concept (u/create-concept
                 t/country
                 term
                 term
                 id)
        concept-with-extras (-> concept (assoc :concept.external-standard/iso-3166-1-alpha-2-2013 code)
                                        (assoc :concept.external-standard/iso-3166-1-alpha-3-2013 code3)
                                )
        temp-id-parent (u/create-temp-id t/continent parent-id)
        relation (u/create-broader-relation-to-concept concept-with-extras temp-id-parent)]
    [concept-with-extras
     relation]))

(defn converter-regions
  "Convert EU regions (NUTS level 3)"
  [{:keys [term id code parent-id national-code]}]
  {:pre [term id parent-id]}
  (let [concept (u/create-concept
                 t/region
                 term
                 term
                 id)
        concept-with-extras (conj concept
                                  (when code [:concept.external-standard/nuts-level-3-code-2013 code])
                                  (when (and national-code (clojure.string/starts-with? code "SE"))  [:concept.external-standard/national-nuts-level-3-code-2019 national-code])
                                  )
        temp-id-parent (u/create-temp-id t/country parent-id)
        relation (u/create-broader-relation-to-concept concept-with-extras temp-id-parent)]
    [concept-with-extras
     relation]))

(defn converter-municipalities
  "Convert municipalities"
  [{:keys [term id parent-id national-code]}]
  {:pre [term id parent-id  national-code]}
  (let [concept (u/create-concept
                 t/municipality
                 term
                 term
                 id)
        concept-with-extras (conj concept (when national-code [:concept.external-standard/national-nuts-lau-2-code-2015 national-code]))
        temp-id-parent (u/create-temp-id t/region parent-id)
        relation (u/create-broader-relation-to-concept concept-with-extras temp-id-parent)]
    [concept-with-extras
     relation]))

(defn convert
  "Query SQL db for geographical places, convert each entity"
  []
  (concat
   (mapcat converter-continents (lm/fetch-data lm/get-continents))
   (mapcat converter-countries (lm/fetch-data lm/get-countries))
   (mapcat converter-regions (lm/fetch-data lm/get-EU-regions))
   (mapcat converter-municipalities (lm/fetch-data lm/get-municipalities))))
