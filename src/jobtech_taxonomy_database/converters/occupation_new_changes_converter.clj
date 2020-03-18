(ns jobtech-taxonomy-database.converters.occupation-new-changes-converter
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as lm]
            [jobtech-taxonomy-database.converters.converter-util :as u]
            [jobtech-taxonomy-database.types :as t]))

(defn convert-deprecated-occupation-names [{:keys [occupation-name-id]}]
  {:pre [occupation-name-id]}
  (u/deprecate-concept t/occupation-name occupation-name-id))

(defn create-new-occupation-name
  [{:keys [term occupation-name-id parent-id-ssyk-4  parent-id-isco-4]}]
  {:pre  [term occupation-name-id parent-id-ssyk-4  parent-id-isco-4]}
  (let    [entity-id-parent-ssyk (u/get-entity-id-by-legacy-id parent-id-ssyk-4 t/ssyk-level-4)
           entity-id-parent-isco (u/get-entity-id-by-legacy-id parent-id-isco-4 t/isco-level-4)
           concept (u/create-concept t/occupation-name term term occupation-name-id)]
    (remove nil? [concept
                  (when entity-id-parent-ssyk (u/create-broader-relation-to-concept concept entity-id-parent-ssyk)) ;; sometimes ssyk is -1 and becomes nil
                  (u/create-broader-relation-to-concept concept entity-id-parent-isco)])))

(defn update-occupation-name [{:keys [occupation-name-id-67 term-68]}]
  {:pre [occupation-name-id-67 term-68]}
  (let [entity-id (u/get-entity-id-by-legacy-id occupation-name-id-67 t/occupation-name)]
    (u/update-concept entity-id {:new-term term-68})))

(defn retract-occupation-name-relations-to-parent
  [{:keys [occupation-name-id-67
           parent-id-67
           parent-type]}]
  (concat
   (u/retract-relation-by-legacy-ids-and-types
    occupation-name-id-67
    t/occupation-name
    parent-id-67
    (if (= parent-type "ssyk-level-4") t/ssyk-level-4 t/isco-level-4)
    t/broader)))

(defn convert-new-occupation-name-relation-to-parent
  [{:keys [occupation-name-id-68
           parent-id-68
           parent-type]}]
  (concat
   (u/get-new-relation-by-legacy-ids-and-types
    occupation-name-id-68
    t/occupation-name
    parent-id-68
    (if (= parent-type "ssyk-level-4") t/ssyk-level-4 t/isco-level-4)
    t/broader)))

(defn convert-replaced-by-occupation-name [{:keys [replaced-id replacing-id]}]
  (u/replace-concept replaced-id replacing-id t/occupation-name))

(defn convert-occupation-collection
  [{:keys [collection-id collection-name]}]
  {:pre [collection-id collection-name]}
  (let [concept (u/create-concept t/occupation-collection collection-name collection-name collection-id)]
    [concept]))

(defn convert-occupation-collection-relation
  [{:keys [collection-id collection-name occupation-name-id]}]
  {:pre [collection-id collection-name occupation-name-id]
   :post [(:relation/concept-1 %) (:relation/concept-2 %) (:relation/type %)]}
  (let [entity-id-occupation-name (u/get-entity-if-exists-or-temp-id occupation-name-id t/occupation-name)
        temp-id-collection (u/create-temp-id t/occupation-collection collection-id)
        relation (u/create-relation entity-id-occupation-name temp-id-collection t/related)]
    relation))

(defn update-occupation-field
  [{:keys [occupation-field-id-67
           occupation-field-term-67
           occupation-field-term-68
           occupation-field-description-67
           occupation-field-description-68]}]
  {:pre [occupation-field-id-67
         occupation-field-term-67
         occupation-field-term-68
         occupation-field-description-67
         occupation-field-description-68]}
  (let [entity-id (u/get-entity-id-by-legacy-id occupation-field-id-67 t/occupation-field)
        attribute-map (merge
                       (when (not= occupation-field-term-67 occupation-field-term-68)
                         {:new-term occupation-field-term-68})
                       (when (not= occupation-field-description-67 occupation-field-description-68)
                         {:description occupation-field-description-68}))]
    (u/update-concept entity-id attribute-map)))

(defn convert-new-occupation-field-relation-to-ssyk-4
  [{:keys [ssyk-4-id-68
           parent-id-occupation-field-68]}]
  (concat
   (u/get-new-relation-by-legacy-ids-and-types
    ssyk-4-id-68
    t/ssyk-level-4
    parent-id-occupation-field-68
    t/occupation-field
    t/broader)))

(defn convert-deprecated-occupation-synonyms
  [{:keys [synonym-id]}]
  {:pre [synonym-id]}
  (u/deprecate-concept t/keyword synonym-id))

(defn create-new-occupation-synonyms
  [{:keys [synonym-id synonym-term]}]
  {:pre [synonym-id synonym-term]}
  (let [concept (u/create-concept t/keyword synonym-term synonym-term synonym-id)]
    [concept]))

(defn update-occupation-synonym-term [{:keys [synonym-id-67 synonym-term-68]}]
  {:pre [synonym-id-67 synonym-term-68]}
  (let [entity-id (u/get-entity-id-by-legacy-id synonym-id-67 t/keyword)]
    (u/update-concept entity-id {:new-term synonym-term-68})))

(defn retract-synonym-relations-to-occupation-name
  [{:keys [occupation-name-id-67
           synonym-id-67]}]
  (concat
   (u/retract-relation-by-legacy-ids-and-types
    occupation-name-id-67
    t/occupation-name
    synonym-id-67
    t/keyword
    t/related)))

(defn convert-new-synonym-relation-to-occupation-name
  [{:keys [occupation-name-id-68
           synonym-id-68]}]
  (concat
   (u/get-new-relation-by-legacy-ids-and-types
    occupation-name-id-68
    t/occupation-name
    synonym-id-68
    t/keyword
    t/related)))

(defn update-ssyk-level-4
  [{:keys [id term68]}]
  {:pre [id term68]}
  (let [entity-id (u/get-entity-id-by-legacy-id id t/ssyk-level-4)]
    (u/update-concept entity-id {:new-term term68})))

(defn convert []
  (remove empty?
          (concat
           (mapcat create-new-occupation-name (lm/fetch-data lm/get-new-occupation-name))
           (mapcat convert-new-occupation-name-relation-to-parent (lm/fetch-data lm/get-new-occupation-name-relation-to-parent-isco))
           (mapcat convert-new-occupation-name-relation-to-parent (->> (lm/fetch-data lm/get-new-occupation-name-relation-to-parent-ssyk)
                                                                       (remove #(-> % :parent-id-68 (= -1)))))

           (mapcat convert-occupation-collection (lm/fetch-data lm/get-occupation-collections))

           (mapcat create-new-occupation-synonyms (lm/fetch-data lm/get-new-synonyms))
           (map convert-replaced-by-occupation-name (lm/fetch-data lm/get-replaced-occupation-name))
           (mapcat update-occupation-name (lm/fetch-data lm/get-updated-occupation-name-term))
           (mapcat update-occupation-field (lm/fetch-data lm/get-updated-occupation-field))
           (mapcat update-occupation-synonym-term (lm/fetch-data lm/get-updated-synonym-terms))

           (map convert-occupation-collection-relation (lm/fetch-data lm/get-occupation-collection-relations))
           (mapcat convert-new-occupation-field-relation-to-ssyk-4 (lm/fetch-data lm/get-new-occupation-field-relation-to-ssyk-4))
           (mapcat convert-new-synonym-relation-to-occupation-name (lm/fetch-data lm/get-new-synonym-relation-to-occupation))

           (map convert-deprecated-occupation-names (lm/fetch-data lm/get-deprecated-occupation-name))
           (mapcat retract-occupation-name-relations-to-parent (lm/fetch-data lm/get-deprecated-occupation-name-relation-to-parent-isco))
           (mapcat retract-occupation-name-relations-to-parent (lm/fetch-data lm/get-deprecated-occupation-name-relation-to-parent-ssyk))

           (map convert-deprecated-occupation-synonyms (lm/fetch-data lm/get-deprecated-synonyms))
           (mapcat retract-synonym-relations-to-occupation-name (lm/fetch-data lm/get-deprecated-synonym-relation-to-occupation))
           (mapcat update-ssyk-level-4 (lm/fetch-data lm/get-updated-ssyk-level-4)))))
