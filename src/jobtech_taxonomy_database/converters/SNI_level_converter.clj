(ns jobtech-taxonomy-database.converters.SNI-level-converter
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as lm]
            [jobtech-taxonomy-database.converters.converter-util :as u]
            [jobtech-taxonomy-database.types :as t]))

(defn converter-1
  "Convert one row of legacy sni codes"
  [{:keys [term nacelevel1id nacelevel1code explanatorynotes]}]
  {:pre [term nacelevel1id nacelevel1code]}
  (let
   [concept (u/create-concept
             t/sni-level-1
             term
             (if (not (empty? explanatorynotes)) explanatorynotes term)
             nacelevel1id)
    concept-with-extras (assoc concept :concept.external-standard/sni-level-code-2007 nacelevel1code)]
    [concept-with-extras]))

(defn converter-2
  "Convert one row of legacy sni codes"
  [{:keys [term nacelevel2id nacelevel1id nacelevel2code explanatorynotes]}]
  {:pre [term nacelevel2id nacelevel1id nacelevel2code]}
  (let [concept (u/create-concept
                 t/sni-level-2
                 term
                 (if (not (empty? explanatorynotes)) explanatorynotes term)
                 nacelevel2id)
        concept-with-extras (assoc concept :concept.external-standard/sni-level-code-2007 nacelevel2code)
        temp-id-parent (u/create-temp-id t/sni-level-1 nacelevel1id)
        relation (u/create-broader-relation-to-concept concept-with-extras temp-id-parent)]
    [concept-with-extras
     relation]))

(defn convert
  "Query db for SNI codes, convert each entity"
  []
  (concat
   (mapcat converter-1 (lm/fetch-data lm/get-sni-level-1))
   (mapcat converter-2 (lm/fetch-data lm/get-sni-level-2))))
