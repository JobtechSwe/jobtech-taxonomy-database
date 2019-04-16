(ns jobtech-taxonomy-database.converters.occupation-deprecated-converter
  (:gen-class)
  (:require [datomic.client.api :as d]
            [jobtech-taxonomy-database.schema :refer :all :as schema]
            [jobtech-taxonomy-database.legacy-migration :refer :all]
            [jobtech-taxonomy-database.config :refer :all]
            [jobtech-taxonomy-database.datomic-connection :refer :all :as conn]
            [jobtech-taxonomy-database.converters.nano-id-assigner :refer :all]
            [cheshire.core :refer :all]
            [jobtech-taxonomy-database.converters.converter-util :as util]
            ))

#_(fetch-data get-deprecated-occupation-name)

(defn convert-deprecated-occupation [{:keys [deprecatedoccupation]}]
  {:pre [deprecatedoccupation]}
  (let [occupation-to-deprecate-db-id (util/get-concept-by-legacy-id (str deprecatedoccupation) :occupation-name)]
    {:db/id occupation-to-deprecate-db-id
     :concept/deprecated true
     }
    )
  )

(defn create-new-occupation-name
  [{:keys [term occupationgroupid occupationnameid localegroupid]}]
  {:pre   [term occupationgroupid occupationnameid localegroupid]}

  (let    [nano-id (get-nano "occupation-name-" (str occupationnameid))
           temp-id (str "occupation-name-" occupationnameid)
           ssyk-concept-id (util/get-concept-by-legacy-id (str localegroupid) :occupation-group)
           isco-concept-id (util/get-concept-by-legacy-id (str occupationgroupid) :isco)
           ]
    [
     (util/create-concept nano-id temp-id term term :occupation-name occupationnameid)
     (util/create-term nano-id term)
     (util/create-relation temp-id ssyk-concept-id :hyperonym)
     (util/create-relation temp-id isco-concept-id :hyperonym)
     ]
    )
  )

(defn update-preferred-term [new-preferred-term entity-id]
  (let [temp-id (str (gensym))]
    [
     (util/create-term temp-id new-preferred-term)
     {:db/id entity-id
      :concept/preferred-term temp-id
      }
     ])
  )

(defn convert-added-occupation-name
  [{:keys [term occupationgroupid occupationnameid localegroupid] :as data}  ]
  {:pre   [term occupationgroupid occupationnameid localegroupid]}

  (let [[preferred-term entity-id]  (util/get-preferred-term-by-legacy-id (str occupationgroupid) :occupation-name)]
  (cond
      (nil? preferred-term) (create-new-occupation-name data)
      (= term preferred-term) []
      :else  (update-preferred-term preferred-term entity-id)
      ))
  )

#_(defn convert-renamed-occupation-name []
    )

(defn get-entity-if-exists-or-temp-id [legacy-id]

  (if-let [entity-id (util/get-concept-by-legacy-id legacy-id :occupation-name)]
    entity-id
    (str "occupation-name-" legacy-id)
    )
  )

(defn convert-replaced-by-occuaption-name [ {:keys [occupationnameid occupationnameidref]}]
  (let [old-concept  (util/get-concept-by-legacy-id (str occupationnameid) :occupation-name)
        replaced-by-concept-id (get-entity-if-exists-or-temp-id occupationnameidref)
        ]
    {:db/id old-concept
     :concept/replaced-by replaced-by-concept-id
     }
    )
  )

(defn convert []
  "Run this function after the database has been loaded"
  (concat
   (map convert-deprecated-occupation (fetch-data get-deprecated-occupation-name))
   (mapcat convert-added-occupation-name (fetch-data get-new-occupation-name))
   (map convert-replaced-by-occuaption-name (fetch-data get-replaced--occupation-name))
   )
  )

#_(def get-concept '[:find ?s ?legacy-id
                   :in $  ;; TODO rename category
                                      :where
                                      [?s :concept.external-database.ams-taxonomy-67/id ?legacy-id]])

#_(defn get-concepts []
  (d/q get-concept (conn/get-db))
  )


;; (d/pull (get-db) '[*] 51800191807793305) ;; fetch whole entity
