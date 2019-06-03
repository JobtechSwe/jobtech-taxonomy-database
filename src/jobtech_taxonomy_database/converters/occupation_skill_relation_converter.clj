(ns jobtech-taxonomy-database.converters.occupation-skill-relation-converter
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as lm]
            [jobtech-taxonomy-database.converters.converter-util :as u]
            [jobtech-taxonomy-database.types :as t]))

(defn convert-ssyk-4-skill
  [{:keys [skill-id ssyk-4-id]}]
  {:pre [skill-id ssyk-4-id]}
  (let [temp-id-ssyk (u/get-entity-if-exists-or-temp-id ssyk-4-id t/ssyk-level-4)
        temp-id-skill (u/get-entity-if-exists-or-temp-id skill-id t/skill)
        relation (u/create-relation temp-id-ssyk temp-id-skill t/ssyk-4-to-skill)]
    relation))

(defn convert-isco-4-skill
  [{:keys [skill-id isco-4-id]}]
  {:pre [skill-id isco-4-id]}
  (let [temp-id-isco (u/get-entity-if-exists-or-temp-id isco-4-id t/isco-level-4)
        temp-id-skill (u/get-entity-if-exists-or-temp-id skill-id t/skill)
        relation (u/create-relation temp-id-isco temp-id-skill t/isco-4-to-skill)]
    relation))

(defn convert
  ""
  []
  (concat
    (map convert-ssyk-4-skill (lm/fetch-data lm/get-ssyk-4-skill-relation))
    (map convert-isco-4-skill (lm/fetch-data lm/get-isco-4-skill-relation))
    ))
