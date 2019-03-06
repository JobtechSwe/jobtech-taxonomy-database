(ns jobtech-taxonomy-database.converters.employment-duration-converter
  (:gen-class)
  (:require [datomic.client.api :as d]
            [jobtech-taxonomy-database.schema :refer :all :as schema]
            [jobtech-taxonomy-database.legacy-migration :refer :all]
            [jobtech-taxonomy-database.config :refer :all]
            [jobtech-taxonomy-database.datomic-connection :refer :all :as conn]
            [jobtech-taxonomy-database.converters.nano-id-assigner :refer :all]
            [cheshire.core :refer :all]
            [nano-id.custom :refer [generate]]))

(defn converter
  "Immutable language converter."
  [data]
  (let [category-67 :employment-duration                 ;json-nyckeln
        id-67 (str (:anställningsvaraktighetsid_2 data)) ;ska matcha legacyAmsTaxonomyId i json
        description-67 (:beteckning data)]               ;ska matcha preferredTerm i json
    (let [nano-id (get-nano category-67 (keyword id-67))]
      [{:concept/id                nano-id
        :concept/description       description-67
        :concept/preferred-term    nano-id
        :concept.taxonomy-67-id    id-67
        :concept/category          category-67
        :concept.category/sort-order (:sortering data)
        :concept.external-standard/eures-code (:eureskod data)}
       {:db/id          nano-id
        :term/base-form description-67}])))

(defn convert
  ""
  []
  (mapcat converter (fetch-data get-employment-duration)))
