(ns  jobtech-taxonomy-database.converters.occupation-group-skill-relation-converter
  (:gen-class)
  (:require [datomic.client.api :as d]
            [jobtech-taxonomy-database.schema :refer :all :as schema]
            [jobtech-taxonomy-database.legacy-migration :as lm]
            [jobtech-taxonomy-database.config :refer :all]
            [jobtech-taxonomy-database.datomic-connection :refer :all :as conn]
            [jobtech-taxonomy-database.converters.nano-id-assigner :refer :all]
            [jobtech-taxonomy-database.converters.converter-util :as util]
            [cheshire.core :refer :all]
            ))
                                        ; (fetch-data get-occupation-group-skill-relation)
                                        ; (lm/fetch-data lm/get-occupation-group-skill-relation)

;; (fetch-data get-isco-level-4-skill-relation)

(defn convert-isco-level-4-skill-relation [{:keys [skillid occupationgroupid]}]
  {:pre [skillid occupationgroupid]}
  (let  [skill-entity-id (util/get-concept-by-legacy-id skillid :skill)
         isco-entity-id (util/get-concept-by-legacy-id  occupationgroupid :isco)
         ]
    (util/create-relation isco-entity-id skill-entity-id :related-to )
    )
  )


(defn convert-occupation-group-skill-relation [{:keys [skillid localegroupid]}]
  {:pre [skillid localegroupid]}
  (let  [skill-entity-id (util/get-concept-by-legacy-id skillid :skill)
         occupation-group-entity-id (util/get-concept-by-legacy-id localegroupid :occupation-group)
        ]
    (util/create-relation occupation-group-entity-id skill-entity-id :related-to )
    )
  )


(defn convert []

  (concat
   (map convert-occupation-group-skill-relation (lm/fetch-data lm/get-occupation-group-skill-relation))
   (map convert-isco-level-4-skill-relation (lm/fetch-data lm/get-isco-level-4-skill-relation))
   )

  )
