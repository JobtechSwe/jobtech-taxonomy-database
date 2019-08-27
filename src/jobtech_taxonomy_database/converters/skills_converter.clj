(ns jobtech-taxonomy-database.converters.skills-converter
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as lm]
            [jobtech-taxonomy-database.converters.converter-util :as u]
            [jobtech-taxonomy-database.types :as t]))

(defn converter-headline
  "Convert headlines"
  [{:keys [term skillheadlineid]}]
  {:pre [term skillheadlineid]}
  (let
   [concept (u/create-concept
             t/skill-headline
             term
             term
             skillheadlineid)
    ]
    [concept
     ]))

(defn converter-skill
  "Convert skills"
  [{:keys [term skillid skillheadlineid]}]
  {:pre [term skillid skillheadlineid]}
  (let [concept (u/create-concept
                 t/skill
                 term
                 term
                 skillid)
        temp-id-parent (u/create-temp-id t/skill-headline skillheadlineid)
        relation (u/create-broader-relation-to-concept concept temp-id-parent)]
    [concept
     relation]))

(defn convert
  "Query db for skills and skill headlines, convert each entity" []
  (concat
   (mapcat converter-headline (lm/fetch-data lm/get-skill-headlines))
   (mapcat converter-skill (lm/fetch-data lm/get-skills))))
