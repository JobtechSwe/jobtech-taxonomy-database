(ns jobtech-taxonomy-database.converters.SNI-level-converter
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as lm]
            [jobtech-taxonomy-database.converters.nano-id-assigner :as nano]
            [jobtech-taxonomy-database.converters.converter-util :as u]
            [jobtech-taxonomy-database.types :as t]
            ))

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
     concept-with-extras (assoc concept :concept.external-standard/sni-level-code nacelevel1code)
     concept-term (u/create-term (:concept/id concept) term)]
    [concept-with-extras concept-term]
    ))

#_
(defn convert-sni-level-1
  "Convert one row of legacy sni codes"
  [{:keys [nacelevel1id term nacelevel1code explanatorynotes]}]
  {:pre [nacelevel1id term nacelevel1code]}
  (let [nano-id (nano/get-nano)
        temp-id  (str "sni-level-1-" nacelevel1id)
        concept (u/create-concept nano-id temp-id term (if (not (empty? explanatorynotes)) explanatorynotes term) :sni-level-1 nacelevel1id)]
    [(assoc concept :concept.external-standard/sni-level-code nacelevel1code)
     (u/create-term nano-id term)]
    ))

(defn converter-2
  "Convert one row of legacy sni codes"
  [{:keys [term nacelevel2id nacelevel1id nacelevel2code explanatorynotes]}]
  {:pre [term nacelevel2id nacelevel1id nacelevel2code]}
  (let [concept (u/create-concept
               t/sni-level-2
               term
               (if (not (empty? explanatorynotes)) explanatorynotes term)
               nacelevel2id)
        concept-with-extras (assoc concept :concept.external-standard/sni-level-code nacelevel2code)
        concept-term (u/create-term (:concept/id concept) term)
        relation (u/create-relation (str "sni-level-2-" nacelevel2id) (str "sni-level-1-" nacelevel1id) :narrower)] ;; TODO check if this is the right relationship! /S
    [concept-with-extras
     concept-term
     relation]
    ))

#_
(defn convert-sni-level-2
  "Convert one row of legacy sni codes"
  [{:keys [nacelevel2id nacelevel1id term nacelevel2code explanatorynotes]}]
  {:pre [nacelevel2id nacelevel1id term nacelevel2code explanatorynotes]}
  (let [nano-id (nano/get-nano)
        temp-id  (str "sni-level-2-" nacelevel2id)
        concept (u/create-concept nano-id temp-id term (if (not (empty? explanatorynotes)) explanatorynotes term) :sni-level-2 nacelevel2id)]
    [(assoc concept :concept.external-standard/sni-level-code nacelevel2code)
     (u/create-term nano-id term)
     (u/create-relation temp-id (str "sni-level-1-" nacelevel1id) :hyperonym)]
    ))

(defn convert
  "Convert each SNI codes" []
  (concat
    (mapcat converter-1 (lm/fetch-data lm/get-sni-level-1))
    (mapcat converter-2 (lm/fetch-data lm/get-sni-level-2))
;   (mapcat convert-sni-level-1 (lm/fetch-data lm/get-sni-level-1))
;   (mapcat convert-sni-level-2 (lm/fetch-data lm/get-sni-level-2))
 ))
