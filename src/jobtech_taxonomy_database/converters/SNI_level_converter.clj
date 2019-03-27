(ns jobtech-taxonomy-database.converters.SNI-level-converter
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as lm]
            [jobtech-taxonomy-database.converters.nano-id-assigner :as nano]
            [jobtech-taxonomy-database.converters.converter-util :as util]
            ))

(defn convert-sni-level-1
  "Convert one row of legacy sni codes"
  [{:keys [nacelevel1id term nacelevel1code explanatorynotes]}]
  {:pre [nacelevel1id term nacelevel1code]}

  (let [nano-id (nano/get-nano)
        temp-id  (str "sni-level-1-" nacelevel1id)
        concept (util/create-concept nano-id temp-id term (if (not (empty? explanatorynotes)) explanatorynotes term) :sni-level-1 nacelevel1id)
        ]
    [
     (assoc concept :concept.external-standard/sni-level-code nacelevel1code)
     (util/create-term nano-id term)
     ]
    )
  )

(defn convert-sni-level-2
  "Convert one row of legacy sni codes"
  [{:keys [nacelevel2id nacelevel1id term nacelevel2code explanatorynotes]}]
  {:pre [nacelevel2id nacelevel1id term nacelevel2code explanatorynotes]}

  (let [nano-id (nano/get-nano)
        temp-id  (str "sni-level-2-" nacelevel2id)
        concept (util/create-concept nano-id temp-id term (if (not (empty? explanatorynotes)) explanatorynotes term) :sni-level-2 nacelevel2id)
        ]
    [
     (assoc concept :concept.external-standard/sni-level-code nacelevel2code)
     (util/create-term nano-id term)
     (util/create-relation temp-id (str "sni-level-1-" nacelevel1id) :hyperonym)
     ]
    )
  )

(defn convert
  "Convert each SNI codes"
  []

  (concat
   (mapcat convert-sni-level-1 (lm/fetch-data lm/get-sni-level-1))
   (mapcat convert-sni-level-2 (lm/fetch-data lm/get-sni-level-2)))
  )
