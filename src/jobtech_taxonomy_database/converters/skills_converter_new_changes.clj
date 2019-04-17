(ns jobtech-taxonomy-database.converters.skills-converter-new-changes
  (:gen-class)
  (:require [datomic.client.api :as d]
            [clojure.pprint :as pp]
            [jobtech-taxonomy-database.schema :refer :all :as schema]
            [jobtech-taxonomy-database.legacy-migration :refer :all]
            [jobtech-taxonomy-database.config :refer :all]
            [jobtech-taxonomy-database.datomic-connection :refer :all :as conn]
            [jobtech-taxonomy-database.converters.nano-id-assigner :refer :all]
            [jobtech-taxonomy-database.converters.converter-util :as util]
            ))

;; (fetch-data get-new-skill)

(defn convert-new-skill [{:keys [id-68 term-68 skill-headline]}]
  {:pre [id-68 term-68 skill-headline]}

  (if (not (util/get-concept-by-legacy-id id-68 :skill ))
    (let [
          nano-id (get-nano)
          temp-id (str "skill-" id-68)
          concept (util/create-concept nano-id
                                       temp-id
                                       term-68
                                       term-68
                                       :skill
                                       id-68
                                       )]
      [
       concept
       (util/create-relation temp-id
                             (util/get-entity-if-exists-or-temp-id skill-headline :skill-headline)
                             :hyperonym
                             )
       ]
      )
    []
    )
  )


(defn convert-deprecated-skill [{:keys [id-67]}]
  {:pre [id-67]}
  (util/deprecate-concept-with-legacy-id-and-category :skill id-67))

(defn convert-updated-skill [{:keys [skillid term]}]
  (if-let [entity-id (util/get-concept-by-legacy-id skillid :skill)]
    (util/update-preferred-term term entity-id)
    []
    )

  )


;; TODO kolla skill-collection, skill-reference
