(ns jobtech-taxonomy-database.converters.worktime-extent-converter
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as legacy-migration]
            [jobtech-taxonomy-database.converters.nano-id-assigner :as nano-id-assigner]))

(defn converter
  "Immutable work time extent converter."
  [data]
  (let [category-67 :worktime-extent              ;json-nyckeln
        id-67 (str (:arbetstidsid data))          ;ska matcha legacyAmsTaxonomyId i json
        description-67 (:beteckning data)]        ;ska matcha preferredTerm i json
    (let [nano-id (nano-id-assigner/get-nano category-67 (keyword id-67))]
      [{:concept/id                                   nano-id
        :concept/description                          description-67
        :concept/preferred-term                       nano-id
        :concept.external-database.ams-taxonomy-67/id id-67
        :concept/category                             category-67
        :concept.category/sort-order                  (:sortering data)}
       {:db/id          nano-id
        :term/base-form description-67}])))

(defn convert
  "Query db for work time extent, convert each entity"
  []
  (mapcat converter (legacy-migration/fetch-data legacy-migration/get-worktime-extent)))
