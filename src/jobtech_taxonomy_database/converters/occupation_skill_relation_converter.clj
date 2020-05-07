(ns jobtech-taxonomy-database.converters.occupation-skill-relation-converter
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as lm]
            [jobtech-taxonomy-database.converters.converter-util :as u]
            [jobtech-taxonomy-database.types :as t]))

(defn fetch-ssyk-skill-relations-v67 []
  (concat (clojure.set/difference
           (set (lm/fetch-data lm/get-ssyk-4-skill-relation-inherited-v67))
           (set (lm/fetch-data lm/get-ssyk-4-relation-restricted-v67)))
          (lm/fetch-data lm/get-ssyk-4-skill-relation-swedish-v67)))


(defn convert-ssyk-4-skills [ssyk-4-skills]
  (let [
        ssyk-4-ids (u/type+legacy-ids->entity-ids-or-temp-ids t/ssyk-level-4 (map :ssyk-4-id ssyk-4-skills))
        skill-ids (u/type+legacy-ids->entity-ids-or-temp-ids t/skill (map :skill-id ssyk-4-skills))]
    ssyk-4-ids
    skill-ids
    (map (fn [{:keys [skill-id ssyk-4-id]}]
           (u/create-relation
            (get ssyk-4-ids (str ssyk-4-id))
            (get skill-ids (str skill-id))
            t/related))
         ssyk-4-skills)))

(defn convert-isco-4-skills []
  (let [isco-4-skills (lm/fetch-data lm/get-isco-4-skill-relation)
        isco-4-ids (u/type+legacy-ids->entity-ids-or-temp-ids t/isco-level-4 (map :isco-4-id isco-4-skills))
        skill-ids (u/type+legacy-ids->entity-ids-or-temp-ids t/skill (map :skill-id isco-4-skills))]
    (map (fn [{:keys [skill-id isco-4-id]}]
           (u/create-relation
            (get isco-4-ids (str isco-4-id))
            (get skill-ids (str skill-id))
            t/related))
         isco-4-skills)))

(defn convert []
  (concat
   (convert-ssyk-4-skills (fetch-ssyk-skill-relations-v67))
   ))


;; (convert-isco-4-skills)
