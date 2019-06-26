(ns jobtech-taxonomy-database.converters.skills-converter-new-changes
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as lm]
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

(defn convert-updated-skill [{:keys [skill-id-67 skill-term-68]}]
  {:pre [skill-id-67 skill-term-68]}
  (let [entity-id (u/get-entity-id-by-legacy-id skill-id-67 t/skill)]
    (u/update-concept entity-id {:new-term skill-term-68})))

(defn convert-replaced-skill [{:keys [deprecated-id replacing-id]}]
  {:pre [deprecated-id replacing-id]}
  (u/replace-concept deprecated-id replacing-id t/skill))

(defn retract-skill-relations
  [{:keys [skill-id-67
           parent-headline-id-67]}]
  {:pre [skill-id-67
         parent-headline-id-67]}
  (u/retract-relation-by-legacy-ids-and-types
   skill-id-67
   t/skill
   parent-headline-id-67
   t/skill-headline
   t/broader))

(defn convert-new-skill-relations
  [{:keys [skill-id-68
           parent-headline-id-68]}]
  {:pre [skill-id-68
         parent-headline-id-68]}
  (u/get-new-relation-by-legacy-ids-and-types
   skill-id-68
   t/skill
   parent-headline-id-68
   t/skill-headline
   t/broader))

(defn convert []
  (remove nil? (concat
                (mapcat convert-new-skill (lm/fetch-data lm/get-new-skill))
                (map convert-deprecated-skill (lm/fetch-data lm/get-deprecated-skill))
                (mapcat convert-updated-skill (lm/fetch-data lm/get-updated-skill))
                (map convert-replaced-skill (lm/fetch-data lm/get-replaced-skill))
                (mapcat retract-skill-relations (lm/fetch-data lm/get-deprecated-skill-relation-to-headline))
                ;;TODO Above returns list of vectors, does that work?
                (mapcat convert-new-skill-relations (lm/fetch-data lm/get-new-skill-relation-to-headline)))))
