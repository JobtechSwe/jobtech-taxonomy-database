(ns jobtech-taxonomy-database.converters.SUN-education-field-legacy-converter
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as lm]
            [jobtech-taxonomy-database.converters.nano-id-assigner :as nano-id-assigner]
            [jobtech-taxonomy-database.converters.converter-util :as u]
            [jobtech-taxonomy-database.types :as t]
            [clojure.spec.alpha :as s]
            [datomic.client.api :as d]
            [jobtech-taxonomy-database.datomic-connection :as conn]
            ))

(def sun-field-key-2000 :concept.external-standard/sun-education-field-code-2000)
(def sun-level-key-2000 :concept.external-standard/sun-education-level-code-2000)
(def sun-legacy-key-prefix "sun-2000")

(defn prefix-sun-2000 [id]
  (str sun-legacy-key-prefix "-" id)
  )


(defn create-sun-2000-concept
  "Creates map for standard concept with default attributes"
  ([instance-type label description legacy-id legacy-id-prefix]
   {:pre [(s/valid? ::t/concept-types instance-type)
          (s/valid? string? label)
          (s/valid? string? description)
          (or (s/valid? string? legacy-id) (s/valid? int? legacy-id))]}

   (let [[concept-id temp-id] (u/get-concept-id-and-temp-id instance-type (prefix-sun-2000 legacy-id))]
     {:db/id                                        temp-id
      :concept/id                                   concept-id
      :concept/definition                           description
      :concept/preferred-label                      label
      :concept/type                                 instance-type
      :concept.external-database.ams-taxonomy-67/id (str legacy-id)})))



(defn convert-eductaion [rows type parent-type legacy-id-prefix code-field-name]
  (mapcat (fn [{:keys [id term code parent-id definition]}]
            (let [concept (-> (create-sun-2000-concept type term (or definition term) id legacy-id-prefix)
                              (assoc code-field-name code))]
              (cond-> []
                true (conj concept)
                parent-id (conj (u/create-broader-relation-to-concept concept (u/create-temp-id parent-type (prefix-sun-2000 parent-id)))))))

          rows))


(defn convert-guide-tree [{:keys [education-level-1-id education-field-3-id]}]
  (u/create-relation
   (u/create-temp-id t/sun-education-level-1 (prefix-sun-2000 education-level-1-id))
   (u/create-temp-id t/sun-education-field-3 (prefix-sun-2000 education-field-3-id))
   t/related)
  )

(defn convert
  "Compile converted SUN education fields 1, 2 & 3"
  []
  (concat
   (convert-eductaion (lm/fetch-data lm/get-sun-field-1) t/sun-education-field-1 nil sun-legacy-key-prefix sun-field-key-2000)
   (convert-eductaion (lm/fetch-data lm/get-sun-field-2) t/sun-education-field-2 t/sun-education-field-1 sun-legacy-key-prefix sun-field-key-2000)
  (convert-eductaion (lm/fetch-data lm/get-sun-field-3) t/sun-education-field-3 t/sun-education-field-2 sun-legacy-key-prefix  sun-field-key-2000)

   (convert-eductaion (lm/fetch-data lm/get-sun-level-1) t/sun-education-level-1 nil sun-legacy-key-prefix sun-level-key-2000)

   (convert-eductaion (lm/fetch-data lm/get-sun-level-2) t/sun-education-level-2 t/sun-education-level-1 sun-legacy-key-prefix sun-level-key-2000)

   (convert-eductaion (lm/fetch-data lm/get-sun-level-3) t/sun-education-level-3 t/sun-education-level-2 sun-legacy-key-prefix sun-level-key-2000)

 (map convert-guide-tree (lm/fetch-data lm/get-sun-2000-guide))

  ))
