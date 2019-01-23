(ns jobtech-taxonomy-database.converters.drivers-license-converter
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
  (let [category-67 :driving-license                        ;json-nyckeln
        id-67 (keyword (str (:drivinglicenceid data)))      ;ska matcha legacyAmsTaxonomyId i json
        description-67 (:term data)]                        ;ska matcha preferredTerm i json
    (let [nano-id (get-nano category-67 id-67 description-67)]
      [{:concept/id                nano-id
        :concept/description       description-67
        :concept/preferred-term    nano-id
        :concept/alternative-terms #{nano-id}}
       {:db/id          nano-id
        :term/base-form description-67}])))

(defn convert
  ""
  []
  (mapcat converter (fetch-data get-drivers-license)))
