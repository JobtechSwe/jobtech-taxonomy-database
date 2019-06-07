(ns jobtech-taxonomy-database.converters.occupation-deprecated-converter
  (:gen-class)
  (:require [datomic.client.api :as d]
            [jobtech-taxonomy-database.schema :refer :all :as schema]
            [jobtech-taxonomy-database.legacy-migration :refer :all]
            [jobtech-taxonomy-database.config :refer :all]
            [jobtech-taxonomy-database.datomic-connection :refer :all :as conn]
            [jobtech-taxonomy-database.converters.nano-id-assigner :refer :all]
            [cheshire.core :refer :all]
            [jobtech-taxonomy-database.converters.converter-util :as u]
            [jobtech-taxonomy-database.types :as t]
            ))

#_(fetch-data get-deprecated-occupation-name)

(defn convert-deprecated-occupation [{:keys [deprecatedoccupation]}]
  {:pre [deprecatedoccupation]}
  (u/deprecate-concept t/occupation-name deprecatedoccupation )
  )

(defn create-new-occupation-name
  [{:keys [term occupationgroupid occupationnameid localegroupid]}]
  {:pre   [term occupationgroupid occupationnameid localegroupid]}

  (let    [entity-id-parent-ssyk  (u/get-entity-id-by-legacy-id localegroupid t/ssyk-level-4)
           entity-id-parent-isco  (u/get-entity-id-by-legacy-id occupationnameid t/isco-level-4)
           concept (u/create-concept t/occupation-name term term occupationnameid)
           concept-term (u/create-term-from-concept concept)
           ]
    (remove nil? [
                  concept
                  concept-term
                  (when entity-id-parent-ssyk (u/create-broader-relation-to-concept concept entity-id-parent-ssyk )) ;; sometimes ssyk is -1 and becomes nil
                  (u/create-broader-relation-to-concept concept entity-id-parent-isco)
                  ])
    )
  )


(defn update-occupation-name-preferred-label [{:keys [occupation-name-id-67 term-68 ]}]
  {:pre [occupation-name-id-67 term-68 ]}
  (let [entity-id  (u/get-entity-id-by-legacy-id occupation-name-id-67 t/occupation-name )]
    (u/update-preferred-term entity-id term-68 term-68  ))
  )


(defn update-occupation-name-relations [{:keys [occupation-name-id-67 parent-id-ssyk-4-67 parent-id-ssyk-4-68 parent-id-isco-4-67 parent-id-isco-4-68]}]
  (concat
   (when (not=
          parent-id-ssyk-4-67 parent-id-ssyk-4-68)
     (u/update-relation-by-legacy-ids-and-types
      occupation-name-id-67
      t/occupation-name
      parent-id-ssyk-4-67
      t/ssyk-level-4
      parent-id-ssyk-4-68
      t/broader
      )
     )
   (when (not=
          parent-id-isco-4-67 parent-id-isco-4-68)
     (u/update-relation-by-legacy-ids-and-types
      occupation-name-id-67
      t/occupation-name
      parent-id-isco-4-67
      t/isco-level-4
      parent-id-isco-4-68
      t/broader
      )
     )
     )
   )



(defn convert-replaced-by-occuaption-name [ {:keys [occupationnameid occupationnameidref]}]
  (let [old-concept  (u/get-entity-id-by-legacy-id (str occupationnameid) :occupation-name)
        replaced-by-concept-id (get-entity-if-exists-or-temp-id occupationnameidref)
        ]
    {:db/id old-concept
     :concept/replaced-by replaced-by-concept-id
     }
    )
  )


;; TODO below is broken after Henrik's utils fix
(defn convert-new-occupation-collection
  [{:keys [collectionid name]}]
  {:pre [collectionid name]}

  (let [nano-id (get-nano)]
    [
     (u/create-term nano-id name)
     (u/create-concept nano-id (str "occupation-collection-" collectionid) name name :occupation-collection collectionid)
     ])
  )


(defn convert-new-occupation-collection-relation
  [{:keys [collectionid occupationnameid]}]
  {:pre [collectionid occupationnameid]
   :post [ (:relation/concept-1 %)  (:relation/concept-2 %)   (:relation/type %)]
   }
  {:relation/concept-1 (u/get-entity-if-exists-or-temp-id collectionid :occupation-collection )
   :relation/concept-2 (u/get-entity-if-exists-or-temp-id occupationnameid :occupation-name)
   :relation/type    :meronym ; TODO find a better name for this relationship HAS-A ??
   }
  )


(defn convert []
  "Run this function after the database has been loaded"
  (remove empty? (concat
                  (map convert-deprecated-occupation (fetch-data get-deprecated-occupation-name))
                 ;; (mapcat convert-added-occupation-name (fetch-data get-new-occupation-name))
    ;; TODO kolla att denna finns med!              ;; (map convert-replaced-by-occuaption-name (fetch-data get-replaced--occupation-name))
                  (mapcat convert-new-occupation-collection (fetch-data get-new-occupation-collection))
                  (map convert-new-occupation-collection-relation (fetch-data get-new-occupation-collection-relations))
                  ))
  )
;; => #'jobtech-taxonomy-database.converters.occupation-deprecated-converter/convert


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
