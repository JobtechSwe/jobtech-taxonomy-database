(ns jobtech-taxonomy-database.converters.language-level-converter
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as legacy-migration]
            [jobtech-taxonomy-database.converters.nano-id-assigner :as nano-id-assigner]
            [jobtech-taxonomy-database.converters.converter-util :as u]
            [jobtech-taxonomy-database.types :as t]))

(defn converter
  "Immutable language level converter."
  [{:keys [term languagelevelid]}]
  {:pre [term languagelevelid]}
  (let
    [concept (u/create-concept t/language-level term term languagelevelid)
     concept-term (u/create-term (:concept/id concept) term)]
    [concept concept-term]
    ))

#_
  (let [category-67 :language-level                ;json-nyckeln
        id-67 (str (:languagelevelid data))        ;ska matcha legacyAmsTaxonomyId i json
        description-67 (:term data)]               ;ska matcha preferredTerm i json
    (let [nano-id (nano-id-assigner/get-nano category-67 (keyword id-67))]
      [{:concept/id                                   nano-id
        :concept/description                          description-67
        :concept/preferred-term                       nano-id
        :concept.external-database.ams-taxonomy-67/id id-67
        :concept/category                             category-67}
       {:db/id          nano-id
        :term/base-form description-67}]))

(defn convert
  "Query db for language levels, convert each entity"
  []
  (mapcat converter (legacy-migration/fetch-data legacy-migration/get-language-level)))
