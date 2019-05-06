(ns occupation-group-skill-relation-converter
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as lm]
            [jobtech-taxonomy-database.converters.nano-id-assigner :as nano]
            [jobtech-taxonomy-database.converters.converter-util :as util]
            ))


                                        ; (fetch-data get-occupation-group-skill-relation)

                                        ; (lm/fetch-data lm/get-occupation-group-skill-relation)



(defn convert-occupation-group-skill-relation [{:keys [skillid occupationgroupid]}]
  {:pre [skillid occupationgroupid]}
  (let  [skill-entity-id (util/get-concept-by-legacy-id skillid :skill)
         occupation-group-entity-id (util/get-concept-by-legacy-id occupationgroupid :occupation-group)
        ]
    (util/create-relation occupation-group-entity-id skill-entity-id :related )
    )
  )


(defn convert []
  (map convert-occupation-group-skill-relation (lm/fetch-data lm/get-occupation-group-skill-relation))
  )
