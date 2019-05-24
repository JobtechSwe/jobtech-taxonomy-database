(ns jobtech-taxonomy-database.converters.skills-converter-new-changes
  (:gen-class)
  (:require [datomic.client.api :as d]
            [clojure.pprint :as pp]
            [jobtech-taxonomy-database.schema :refer :all :as schema]
            [jobtech-taxonomy-database.legacy-migration :refer :all]
            [jobtech-taxonomy-database.config :refer :all]
            [jobtech-taxonomy-database.datomic-connection :refer :all :as conn]
            [jobtech-taxonomy-database.converters.nano-id-assigner :refer :all]
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
      [concept concept-term concept-relation
       ])))

(defn convert-deprecated-skill [{:keys [id-67]}]
  {:pre [id-67]}()
  (u/deprecate-concept t/skill id-67))



;; TODO utred alternative-terms, behöver man lägga till den gamla termen i alternative-terms, finns i datomic historien så det går att slå upp tidigare preferred-term
(defn convert-updated-skill [{:keys [skillid term]}]
  (let [entity-id (u/get-entity-id-by-legacy-id skillid t/skill)]
    (u/update-preferred-term entity-id term term)
    )
  )


(defn convert-replaced-skill [{:keys [skillid skillidref]}]
  (let [entity-id-old-skill (u/get-entity-id-by-legacy-id skillid t/skill)
        entity-id-new-skill (u/get-entity-id-by-legacy-id skillidref t/skill)
        ]
    (when (and entity-id-old-skill entity-id-new-skill)
      {:db/id entity-id-old-skill
       :concept/replaced-by entity-id-new-skill})))

(defn convert []
  (remove nil? (concat
                (mapcat convert-new-skill (fetch-data get-new-skill))
                (map convert-deprecated-skill (fetch-data get-deprecated-skill))
                (mapcat convert-updated-skill (fetch-data get-updated-skill))
                (map convert-replaced-skill (fetch-data get-replaced-skill)))))

(comment
  {:db/id 16110044370254510,
   :concept/preferred-term "G__22687",
   :concept/alternative-terms
   [45097568924940977]}

  {:concept/id "ikTr_8Sw_jNn",
   :concept/description "Webbdesign-Worldpress",
   :concept/preferred-term {:db/id 63916809945936970,
                            :term/base-form "Webbdesign-Worldpress"},
   :concept/category :skill,
   :concept.external-database.ams-taxonomy-67/id "609196"}

  {:db/id "G__22689", :term/base-form "Webbdesign-Wordpress"}

  )
