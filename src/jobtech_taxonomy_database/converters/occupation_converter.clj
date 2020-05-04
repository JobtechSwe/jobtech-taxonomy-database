(ns jobtech-taxonomy-database.converters.occupation-converter
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as lm]
            [jobtech-taxonomy-database.types :as t]
            [jobtech-taxonomy-database.converters.converter-util :as u]))

(defn convert-occupation-name
  [{:keys [occupation-name-term occupation-name-id parent-id-ssyk-4 parent-id-isco-4]}]
  {:pre [occupation-name-term occupation-name-id parent-id-ssyk-4 parent-id-isco-4]}
  (let [temp-id-parent-ssyk (u/create-temp-id t/ssyk-level-4 parent-id-ssyk-4)
        temp-id-parent-isco (u/create-temp-id t/isco-level-4 parent-id-isco-4)
        concept (u/create-concept t/occupation-name occupation-name-term occupation-name-term occupation-name-id)
        relation-to-parent-ssyk (u/create-broader-relation-to-concept concept temp-id-parent-ssyk)
        relation-to-parent-isco (u/create-broader-relation-to-concept concept temp-id-parent-isco)]
    [concept
     relation-to-parent-ssyk
     relation-to-parent-isco]))

(defn convert-occupation-field
  [{:keys [occupation-field-id occupation-field-term occupation-field-description]}]
  {:pre [occupation-field-id occupation-field-term occupation-field-description]}
  (let [concept (u/create-concept t/occupation-field
                                  occupation-field-term
                                  occupation-field-description
                                  occupation-field-id)]
    [concept]))

(defn convert-ssyk-level-4
  [{:keys [ssyk-4-id ssyk-4-term ssyk-4-code ssyk-4-description parent-id-ssyk-3 parent-id-occupation-field]}]
  {:pre [ssyk-4-id ssyk-4-term ssyk-4-code ssyk-4-description parent-id-ssyk-3 parent-id-occupation-field]}
  (let [temp-id-parent-field (u/create-temp-id t/occupation-field parent-id-occupation-field)
        temp-id-parent-ssyk-level-3 (u/create-temp-id t/ssyk-level-3 parent-id-ssyk-3)
        concept (u/create-concept t/ssyk-level-4 ssyk-4-term ssyk-4-description ssyk-4-id)
        concept-with-extras (assoc concept :concept.external-standard/ssyk-code-2012 ssyk-4-code)
        relation-to-parent-ssyk (u/create-broader-relation-to-concept concept-with-extras temp-id-parent-ssyk-level-3)
        relation-to-parent-field (u/create-broader-relation-to-concept concept-with-extras temp-id-parent-field)]
    [concept-with-extras
     relation-to-parent-ssyk
     relation-to-parent-field]))

(defn convert-ssyk-level-3
  [{:keys [ssyk-3-id ssyk-3-code ssyk-3-term parent-id-ssyk-2]}]
  {:pre [ssyk-3-id ssyk-3-code ssyk-3-term parent-id-ssyk-2]}
  (let [temp-id-parent-ssyk-2 (u/create-temp-id t/ssyk-level-2 parent-id-ssyk-2)
        concept (u/create-concept t/ssyk-level-3 ssyk-3-term ssyk-3-term ssyk-3-id)
        concept-with-extras (assoc concept :concept.external-standard/ssyk-code-2012 ssyk-3-code)
        relation-to-parent (u/create-broader-relation-to-concept concept-with-extras temp-id-parent-ssyk-2)]
    [concept-with-extras
     relation-to-parent]))

(defn convert-ssyk-level-2
  [{:keys [ssyk-2-id ssyk-2-term ssyk-2-code parent-id-ssyk-1]}]
  {:pre [ssyk-2-id ssyk-2-term ssyk-2-code parent-id-ssyk-1]}
  (let [temp-id-parent-level-1 (u/create-temp-id t/ssyk-level-1 parent-id-ssyk-1)
        concept (u/create-concept t/ssyk-level-2 ssyk-2-term ssyk-2-term ssyk-2-id)
        concept-with-extras (assoc concept :concept.external-standard/ssyk-code-2012 ssyk-2-code)
        relation-to-parent-ssyk-1 (u/create-broader-relation-to-concept concept-with-extras temp-id-parent-level-1)]
    [concept-with-extras
     relation-to-parent-ssyk-1]))

(defn convert-ssyk-level-1
  [{:keys [ssyk-1-id ssyk-1-term ssyk-1-code]}]
  {:pre [ssyk-1-id ssyk-1-term ssyk-1-code]}
  (let [concept (u/create-concept t/ssyk-level-1 ssyk-1-term ssyk-1-term ssyk-1-id)
        concept-with-extras (assoc concept :concept.external-standard/ssyk-code-2012 ssyk-1-code)]
    [concept-with-extras]))

(defn convert-isco-level-4
  [{:keys [isco-4-id isco-4-term isco-4-description isco-4-isco-code parent-id-isco-1]}]
  {:pre [isco-4-id isco-4-term isco-4-description isco-4-isco-code parent-id-isco-1]}
  (let [;; temp-id-parent-isco-1 (u/create-temp-id t/isco-level-1 parent-id-isco-1)
        concept (u/create-concept t/isco-level-4 isco-4-term isco-4-description isco-4-id)
        concept-with-extras (assoc concept :concept.external-standard/isco-code-08 isco-4-isco-code)
        ;;relation-to-parent (u/create-broader-relation-to-concept concept-with-extras temp-id-parent-isco-1)
        ]
    concept-with-extras))

#_(defn convert-isco-level-1
    [{:keys [isco-1-id isco-1-term isco-1-description]}]
    {:pre [isco-1-id isco-1-term isco-1-description]}
    (let [concept (u/create-concept t/isco-level-1 isco-1-term isco-1-description isco-1-id)]
      [concept]))

(defn convert-occupation-name-affinity
  [{:keys [affinity-to-occupation-name-id affinity-from-occupation-name-id percentage]}]
  {:pre [affinity-to-occupation-name-id affinity-from-occupation-name-id percentage]}
  (let [temp-id-affinity-from-concept (u/create-temp-id
                                       t/occupation-name
                                       affinity-from-occupation-name-id)
        temp-id-affinity-to-concept (u/create-temp-id
                                     t/occupation-name
                                     affinity-to-occupation-name-id)
        relation (u/create-relation temp-id-affinity-from-concept temp-id-affinity-to-concept t/substitutability)
        relation-with-affinity-percentage (assoc relation :relation/substitutability-percentage percentage)]
    relation-with-affinity-percentage))

(defn convert-popular-synonym
  [{:keys [synonym-id synonym-term]}]
  {:pre [synonym-id synonym-term]}
  (let [concept (u/create-concept t/keyword synonym-term synonym-term synonym-id)]
    [concept]))

(defn convert-popular-synonym-relation
  [{:keys [synonym-id synonym-term occupation-name-id]}]
  {:pre [synonym-id synonym-term occupation-name-id]}
  (let [temp-id-occupation-name (u/create-temp-id
                                 t/occupation-name
                                 occupation-name-id)
        temp-id-synonym (u/create-temp-id
                         t/keyword
                         synonym-id)
        relation (u/create-relation temp-id-occupation-name temp-id-synonym t/related)]
    relation))

(defn convert-ssyk-4-isco-4-relation
  [{:keys [isco-4-id ssyk-4-id]}]
  {:pre  [isco-4-id ssyk-4-id]}
  (let [temp-id-ssyk (u/create-temp-id t/ssyk-level-4 ssyk-4-id)
        temp-id-isco (u/create-temp-id t/isco-level-4 isco-4-id)
        relation (u/create-relation temp-id-ssyk temp-id-isco t/related)]
    relation))

(defn convert-replaced-occupation-name
  [{:keys [deprecated-occupation-name-id deprecated-occupation-name-term replacing-occupation-name-id]}]
  {:pre [deprecated-occupation-name-id deprecated-occupation-name-term replacing-occupation-name-id]}
  (let [temp-id-replacing-concept (u/create-temp-id t/occupation-name replacing-occupation-name-id)
        concept (u/create-concept t/occupation-name
                                  deprecated-occupation-name-term
                                  deprecated-occupation-name-term
                                  deprecated-occupation-name-id)
        concept-deprecated-true (assoc concept :concept/deprecated true)
        concept-replaced (assoc concept-deprecated-true :concept/replaced-by temp-id-replacing-concept)]
    ;; TODO Utred hur vi hanterar replaced by many different??
    [concept-replaced]))

(defn convert-ais-occupation-collection [{:keys [collection-id collection-name]}]
  {:pre  [collection-id collection-name]}
  (let [concept (u/create-concept t/occupation-collection collection-name collection-name collection-id)]
    [concept]))

(defn convert-ais-occupation-collection-relation
  [{:keys [collection-id occupation-name-id]}]
  {:pre [collection-id occupation-name-id]
   :post [(:relation/concept-1 %) (:relation/concept-2 %) (:relation/type %)]}
  (let [entity-id-occupation-name (u/get-entity-if-exists-or-temp-id occupation-name-id t/occupation-name)
        temp-id-collection (u/create-temp-id t/occupation-collection collection-id)
        relation (u/create-relation entity-id-occupation-name temp-id-collection t/related)]
    relation))


(defn convert
  ""
  []
  (concat
   (mapcat convert-occupation-name (lm/fetch-data lm/get-occupation-name))
   (mapcat convert-ssyk-level-4 (lm/fetch-data lm/get-ssyk-4))
   (mapcat convert-ssyk-level-3 (lm/fetch-data lm/get-ssyk-level-3))
   (mapcat convert-ssyk-level-2 (lm/fetch-data lm/get-ssyk-level-2))
   (mapcat convert-ssyk-level-1 (lm/fetch-data lm/get-ssyk-level-1))
   (mapcat convert-occupation-field (lm/fetch-data lm/get-occupation-field))
   (map convert-isco-level-4 (lm/fetch-data lm/get-isco-level-4))
   (map convert-occupation-name-affinity (lm/fetch-data lm/get-occupation-name-affinity))
   (mapcat convert-popular-synonym (lm/fetch-data lm/get-popular-synonym-occupation))
   (map convert-popular-synonym-relation (lm/fetch-data lm/get-popular-synonym-occupation-relation))
   (map convert-ssyk-4-isco-4-relation (remove #(= -1 (:ssyk-4-id %)) (remove #(= -2 (:ssyk-4-id %)) (lm/fetch-data lm/get-ssyk-4-isco-4-relation))))
   ;; (mapcat convert-replaced-occupation-name (lm/fetch-data lm/get-replaced-occupation-names-reference))
   (mapcat convert-ais-occupation-collection (lm/fetch-data lm/get-ais-occupation-collection))
   (map convert-ais-occupation-collection-relation (lm/fetch-data lm/get-ais-occupation-collection-relations))
   ))
