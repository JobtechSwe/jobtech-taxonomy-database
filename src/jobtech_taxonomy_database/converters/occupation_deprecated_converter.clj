(ns jobtech-taxonomy-database.converters.occupation-deprecated-converter
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as lm]
            [jobtech-taxonomy-database.converters.converter-util :as u]
            [jobtech-taxonomy-database.types :as t]))

(defn convert-deprecated-occupation [{:keys [occupation-name-id]}]
  {:pre [occupation-name-id]}
  (u/deprecate-concept t/occupation-name occupation-name-id))

(defn create-new-occupation-name
  [{:keys [term occupation-name-id parent-id-ssyk-4  parent-id-isco-4]}]
  {:pre  [term occupation-name-id parent-id-ssyk-4  parent-id-isco-4]}
  (let    [entity-id-parent-ssyk  (u/get-entity-id-by-legacy-id parent-id-ssyk-4 t/ssyk-level-4)
           entity-id-parent-isco  (u/get-entity-id-by-legacy-id parent-id-isco-4 t/isco-level-4)
           concept (u/create-concept t/occupation-name term term occupation-name-id)
           concept-term (u/create-term-from-concept concept)]
    (remove nil? [concept
                  concept-term
                  (when entity-id-parent-ssyk (u/create-broader-relation-to-concept concept entity-id-parent-ssyk)) ;; sometimes ssyk is -1 and becomes nil
                  (u/create-broader-relation-to-concept concept entity-id-parent-isco)])))

(defn update-occupation-name [{:keys [occupation-name-id-67 term-68]}]
  {:pre [occupation-name-id-67 term-68]}
  (let [entity-id (u/get-entity-id-by-legacy-id occupation-name-id-67 t/occupation-name)]
    (u/update-concept entity-id {:new-term term-68})))

(defn update-occupation-name-relations
  [{:keys [occupation-name-id-67
      parent-id-ssyk-4-67
      parent-id-ssyk-4-68
      parent-id-isco-4-67
      parent-id-isco-4-68]}]
  (concat
   (when (not=
          parent-id-ssyk-4-67 parent-id-ssyk-4-68)
     (u/update-relation-by-legacy-ids-and-types
      occupation-name-id-67
      t/occupation-name
      parent-id-ssyk-4-67
      t/ssyk-level-4
      parent-id-ssyk-4-68
      t/broader))
   (when (not=
          parent-id-isco-4-67 parent-id-isco-4-68)
     (u/update-relation-by-legacy-ids-and-types
      occupation-name-id-67
      t/occupation-name
      parent-id-isco-4-67
      t/isco-level-4
      parent-id-isco-4-68
      t/broader))))

(defn convert-replaced-by-occupation-name [{:keys [replaced-id replacing-id]}]
  (u/replace-concept replaced-id replacing-id t/occupation-name))

;; get-new-occupation-collection

;; TODO below is broken after Henrik's utils fix ;;  du är här

(defn convert-occupation-collection
  [{:keys [collection-id collection-name]}]
  {:pre [collection-id collection-name]}
  (let [concept (u/create-concept t/occupation-collection collection-name collection-name collection-id)
        concept-term (u/create-term-from-concept concept)]
    [concept
     concept-term]))

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

(defn update-occupation-field-relations
  [{:keys [ssyk-4-id-67
           parent-id-occupation-field-67
           parent-id-occupation-field-68]}]
    (u/update-relation-by-legacy-ids-and-types
      ssyk-4-id-67
      t/ssyk-level-4
      parent-id-occupation-field-67
      t/occupation-field
      parent-id-occupation-field-68
      t/broader))

(defn convert []
  "Run this function after the database has been loaded"
  (remove empty?
          (concat
           (map convert-deprecated-occupation (lm/fetch-data lm/get-deprecated-occupation-name))
           (mapcat create-new-occupation-name (lm/fetch-data lm/get-new-occupation-name))
           (mapcat update-occupation-name-relations (lm/fetch-data lm/get-updated-occupation-name-relation-to-parent))
           (map convert-replaced-by-occupation-name (lm/fetch-data lm/get-replaced-occupation-name))
           (mapcat update-occupation-name (lm/fetch-data lm/get-updated-occupation-name-term))
           (mapcat convert-occupation-collection (lm/fetch-data lm/get-occupation-collections))
           (map convert-occupation-collection-relation (lm/fetch-data lm/get-occupation-collection-relations))
           (mapcat update-occupation-field (lm/fetch-data lm/get-updated-occupation-field))
           (mapcat update-occupation-field-relations (lm/fetch-data lm/get-updated-occupation-field-relation-to-ssyk-4))
           )))
