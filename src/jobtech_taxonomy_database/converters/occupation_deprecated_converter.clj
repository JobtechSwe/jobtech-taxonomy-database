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
    (remove nil? [
                  (util/create-concept nano-id temp-id term term :occupation-name occupationnameid)
                  (util/create-term nano-id term)
                  (when ssyk-concept-id (util/create-relation temp-id ssyk-concept-id :hyperonym)) ;; sometimes ssyk is -1 and becomes nil
                  (util/create-relation temp-id isco-concept-id :hyperonym)
                  ])
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

  (let [[preferred-term entity-id]  (util/get-preferred-term-by-legacy-id (str occupationnameid) :occupation-name)]
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



(defn convert-new-occupation-collection
  [{:keys [collectionid name]}]
  {:pre [collectionid name]}

  (let [nano-id (get-nano)]
    [
     (util/create-term nano-id name)
     (util/create-concept nano-id (str "occupation-collection-" collectionid) name name :occupation-collection collectionid)
     ])
  )


(defn convert-new-occupation-collection-relation
  [{:keys [collectionid occupationnameid]}]
  {:pre [collectionid occupationnameid]
   :post [ (:relation/concept-1 %)  (:relation/concept-2 %)   (:relation/type %)]
   }
  {:relation/concept-1 (util/get-entity-if-exists-or-temp-id collectionid :occupation-collection )
   :relation/concept-2 (util/get-entity-if-exists-or-temp-id occupationnameid :occupation-name)
   :relation/type    :meronym ; TODO find a better name for this relationship HAS-A ??
   }
  )


(defn convert []
  "Run this function after the database has been loaded"
  (remove empty? (concat
                  (map convert-deprecated-occupation (fetch-data get-deprecated-occupation-name))
                  (mapcat convert-added-occupation-name (fetch-data get-new-occupation-name))
                  (map convert-replaced-by-occuaption-name (fetch-data get-replaced--occupation-name))
                  (mapcat convert-new-occupation-collection (fetch-data get-new-occupation-collection))
                  (map convert-new-occupation-collection-relation (fetch-data get-new-occupation-collection-relations))
                  ))
  )



(comment

  problem med "occupation-name-7774"
  "occupation-name-7706"
  "occupation-name-7739"

  )

(comment

  Nil is not a legal value
  {:cognitect.anomalies/category :cognitect.anomalies/incorrect,
   :cognitect.anomalies/message "Nil is not a legal value",
   :data [:db/add -9223301668108103499 87 nil],
   :db/error :db.error/nil-value,
   :dbs
   [{:database-id "e2f03673-5ce8-4938-ad80-fd1f9a5d1b65",
     :t 15,
     :next-t 16,
     :history false}]}

occupation-name som saknar ssyk
  6160
  7782


  )

#_(def get-concept '[:find ?s ?legacy-id
                   :in $  ;; TODO rename category
                                      :where
                                      [?s :concept.external-database.ams-taxonomy-67/id ?legacy-id]])

#_(defn get-concepts []
  (d/q get-concept (conn/get-db))
  )


;; (d/pull (get-db) '[*] 51800191807793305) ;; fetch whole entity
