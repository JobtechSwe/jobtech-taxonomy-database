(ns jobtech-taxonomy-database.converters.occupation-skill-relation-new-changes
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as lm]
            [jobtech-taxonomy-database.converters.converter-util :as u]
            [jobtech-taxonomy-database.types :as t]))

(defn retract-isco-skill-relations
  [{:keys [skill-id-67
           isco-4-id-67]}]
  {:pre [skill-id-67
         isco-4-id-67]}
  (u/retract-relation-by-legacy-ids-and-types
   isco-4-id-67
   t/isco-level-4
   skill-id-67
   t/skill
   t/related))

(defn convert-new-isco-skill-relations
  [{:keys [skill-id-68
           isco-4-id-68]}]
  {:pre [skill-id-68
         isco-4-id-68]}
  (u/get-new-relation-by-legacy-ids-and-types
   skill-id-68
   t/skill
   isco-4-id-68
   t/isco-level-4
   t/related))

(defn retract-ssyk-skill-relations
  [{:keys [skill-id-67
           ssyk-4-id-67]}]
  {:pre [skill-id-67
         ssyk-4-id-67]}
  (u/retract-relation-by-legacy-ids-and-types
   ssyk-4-id-67
   t/ssyk-level-4
   skill-id-67
   t/skill
   t/related))

(defn convert
  ""
  []
  (remove nil?
          (concat
           (mapcat retract-isco-skill-relations (lm/fetch-data lm/get-deprecated-isco-4-skill-relation))
           (mapcat convert-new-isco-skill-relations (lm/fetch-data lm/get-new-isco-4-skill-relation))
           (mapcat retract-ssyk-skill-relations (lm/fetch-data lm/get-deprecated-ssyk-4-skill-relation))
           )))
