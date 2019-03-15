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
  (let [category-67 :driving-license                ;json-nyckeln
        id-67 (str (:drivinglicenceid data))        ;ska matcha legacyAmsTaxonomyId i json
        description-67 (:term data)]                ;ska matcha preferredTerm i json
    (let [nano-id (get-nano category-67 (keyword id-67))]
      [{:concept/id                                     nano-id
        :concept/description                            (:description data)
        :concept/preferred-term                         nano-id
        :concept.external-database.ams-taxonomy-67/id   id-67
        :concept/category                               category-67
        :concept.category/sort-order                    (:displaysortorder data)
        :concept.external-standard/drivers-licence-code (:drivinglicencecode data)
        }
       {:db/id          nano-id
        :term/base-form description-67}])))

(defn convert
  ""
  []
  (mapcat converter (fetch-data get-drivers-license)))
