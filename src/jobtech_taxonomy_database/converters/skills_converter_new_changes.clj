(ns jobtech-taxonomy-database.converters.skills-converter-new-changes
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :refer :all]
            [jobtech-taxonomy-database.converters.converter-util :as u]
            [jobtech-taxonomy-database.types :as t]))

(defn convert-new-skill [{:keys [id-68 term-68 skill-headline]}]
  {:pre [id-68 term-68 skill-headline]}
  (if (not (u/get-entity-id-by-legacy-id id-68 t/skill))
    (let [concept (u/create-concept
                   t/skill
                   term-68
                   term-68
                   id-68)
          concept-term (u/create-term-from-concept concept)
          concept-relation (u/create-broader-relation-to-concept
                            concept (u/get-entity-if-exists-or-temp-id skill-headline t/skill-headline))]
      [concept concept-term concept-relation])))

(defn convert-deprecated-skill [{:keys [id-67]}]
  {:pre [id-67]} ()
  (u/deprecate-concept t/skill id-67))

(defn convert-updated-skill [{:keys [id67 term68]}]
  {:pre [id67 term68]}
  (let [entity-id (u/get-entity-id-by-legacy-id id67 t/skill)]
    (u/update-concept entity-id {:new-term term})))

(defn convert-replaced-skill [{:keys [deprecated-id replacing-id]}]
  {:pre [deprecated-id replacing-id]}
  (u/replace-concept deprecated-id replacing-id t/skill))

(defn convert []
  (remove nil? (concat
                (mapcat convert-new-skill (fetch-data get-new-skill))
                (map convert-deprecated-skill (fetch-data get-deprecated-skill))
                (mapcat convert-updated-skill (fetch-data get-updated-skill))
                (map convert-replaced-skill (fetch-data get-replaced-skill)))))
