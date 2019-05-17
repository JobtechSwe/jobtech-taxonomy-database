(ns jobtech-taxonomy-database.converters.employment-type-converter
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as legacy-migration]
            [jobtech-taxonomy-database.converters.nano-id-assigner :as nano-id-assigner]
            [jobtech-taxonomy-database.converters.converter-util :as u]
            [jobtech-taxonomy-database.types :as t]))

(defn converter
  "Immutable employment types converter."
  [{:keys [beteckning anstallningtypjobbid isortering]}]
  {:pre [beteckning anstallningtypjobbid]}
  (let
    [concept (u/create-concept t/employment-type beteckning beteckning anstallningtypjobbid)
     concept-with-extras (assoc concept
                           :concept.category/sort-order isortering)
     concept-term (u/create-term-from-concept concept-with-extras)]
    [concept-with-extras concept-term]
    ))

#_
  (let [category-67 :employment-type              ;json-nyckeln
        id-67 (str (:anstallningtypjobbid data))  ;ska matcha legacyAmsTaxonomyId i json
        description-67 (:beteckning data)]        ;ska matcha preferredTerm i json
    (let [nano-id (nano-id-assigner/get-nano category-67 (keyword id-67))]
      [{:concept/id                                   nano-id
        :concept/description                          description-67
        :concept/preferred-term                       nano-id
        :concept.external-database.ams-taxonomy-67/id id-67
        :concept/category                             category-67
        :concept.category/sort-order                  (:isortering data)
        }
       {:db/id          nano-id
        :term/base-form description-67}]))

(defn convert
  "Query db for employment types, convert each entity"
  []
  (mapcat converter (legacy-migration/fetch-data legacy-migration/get-employment-type)))
