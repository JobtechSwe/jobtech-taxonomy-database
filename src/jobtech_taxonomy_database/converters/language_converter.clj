(ns jobtech-taxonomy-database.converters.language-converter
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as lm]
            [jobtech-taxonomy-database.converters.converter-util :as u]
            [jobtech-taxonomy-database.types :as t]))

(defn converter
  "Immutable language converter."
  [{:keys [term languageid_2 languagecodeiso languagecodeiso2]}]
  {:pre [term languageid_2 ]}
  (let
      [concept (u/create-concept t/language term term languageid_2)
       concept-with-extras (cond-> concept

                               languagecodeiso
                               (assoc :concept.external-standard/iso-639-3-alpha-3-2007 languagecodeiso)

                               languagecodeiso2
                               (assoc :concept.external-standard/iso-639-3-alpha-2-2007 languagecodeiso2)
                               )
       ]
    [concept-with-extras]))

(defn convert
  "Query db for languages, convert each entity"
  []
  (mapcat converter (lm/fetch-data lm/get-language)))
