(ns jobtech-taxonomy-database.converters.occupation-converter
  (:gen-class)
  (:require [datomic.client.api :as d]
            [jobtech-taxonomy-database.schema :refer :all :as schema]
            [jobtech-taxonomy-database.legacy-migration :refer :all]
            [jobtech-taxonomy-database.config :refer :all]
            [jobtech-taxonomy-database.datomic-connection :refer :all :as conn]
            [jobtech-taxonomy-database.converters.nano-id-assigner :refer :all]
            [cheshire.core :refer :all]
            [jobtech-taxonomy-database.types :as t]
            [jobtech-taxonomy-database.converters.converter-util :as u]
            ))

(comment
  (fetch-data get-occupation-group-ssyk)
  (fetch-data get-occupation-field)
  )

(defn convert-occupation-field
  [{:keys [term description localefieldid]}]
  {:pre [term description  localefieldid]}
  (let [concept (u/create-concept t/occupation-field term description localefieldid )]
    [
     concept
     (u/create-term (:concept/id concept) term)
     ]
    )
  )

(defn  convert-ssyk
  [{:keys [localecode term description localegroupid localefieldid localelevel3id]}]
  {:pre [localecode term description localegroupid localefieldid localelevel3id]}

  (let [
        temp-id-field (u/get-temp-id t/occupation-field localefieldid)
        temp-id-ssyk-level-3 (u/get-temp-id t/ssyk-level-3 localelevel3id)
        concept (u/create-concept t/occupation-group term description localegroupid )
        concept-with-ssyk (assoc concept :concept.external-standard/ssyk-2012 localecode)
        {concept-id :concept/id
         temp-id :db/id  } concept
        ]
    [
     concept-with-ssyk
     (u/create-term concept-id term)
     (u/create-relation temp-id temp-id-field t/broader)
     (u/create-relation temp-id temp-id-ssyk-level-3 t/broader)
     ]
    )
  )

(defn convert-ssyk-level-3
  [{:keys [localelevel3id localecodelevel3 term localelevel2id]}]
  {:pre [localelevel3id localecodelevel3 term localelevel2id]}
  (let [
        temp-id-level-2 (u/get-temp-id t/ssyk-level-2 localelevel2id)
        concept (u/create-concept t/ssyk-level-3 term term localelevel3id)
        concept-ssyk (assoc concept :concept.external-standard/ssyk-2012 localecodelevel3)
        {concept-id :concept/id
         temp-id    :db/id  } concept
        ]
    [concept-ssyk
     (u/create-term concept-id term)
     (u/create-relation temp-id temp-id-level-2 t/broader)
     ]
    )
  )

(defn convert-ssyk-level-2
  [{:keys [localelevel2id localelevel1id localecodelevel2 term]}]
  {:pre [localelevel2id localelevel1id localecodelevel2 term]}
  (let [
        concept (u/create-concept t/ssyk-level-2 term term localelevel2id)
        temp-id-level-1 (u/get-temp-id t/ssyk-level-1 localelevel1id)
        concept-ssyk (assoc concept :concept.external-standard/ssyk-2012 localecodelevel2)
        {concept-id :concept/id
         temp-id    :db/id  } concept
        ]
    [
     concept-ssyk
     (u/create-term concept-id term)
     (u/create-relation temp-id temp-id-level-1 t/broader)
     ]
    )
  )

(defn convert-ssyk-level-1
  [{:keys [localelevel1id localecodelevel1 term]}]
  {:pre [localelevel1id localecodelevel1 term]}
  (let [
        concept (u/create-concept t/ssyk-level-1 term term localelevel1id)
        concept-ssyk (assoc concept :concept.external-standard/ssyk-2012 localecodelevel1)
        ]
    [
     concept-ssyk
     (u/create-term-from-concept concept)
     ]
    )
  )

(defn convert-occupation-name
  [{:keys [term occupationgroupid occupationnameid localegroupid]}]
  {:pre [term occupationgroupid occupationnameid localegroupid]}
  (let [nano-id (get-nano "occupation-name" (str occupationnameid))
        temp-id (str "occupation-name-" occupationnameid)
        temp-id-ssyk (str "occupation-group-" localegroupid )
        temp-id-isco (str "isco-"  occupationgroupid)
        ]
    [
     (u/create-concept nano-id temp-id term term :occupation-name occupationnameid)
     (u/create-term nano-id term)
     (u/create-relation temp-id temp-id-ssyk :hyperonym) ;; TODO decide how to model this properly
     (u/create-relation temp-id temp-id-isco :hyperonym)
     ]
    )
 )


(defn convert-isco
  [{:keys [isco ilo occupationfieldid term occupationgroupid description]}]
  {:pre [isco ilo occupationfieldid term occupationgroupid description]}

  (let [nano-id (get-nano)
        temp-id (str "isco-" occupationgroupid)
        concept (u/create-concept nano-id temp-id term description :isco occupationgroupid)
        concept-with-isco (assoc concept :concept.external-standard/isco-08 isco)
        ]
    [
     concept-with-isco
     (u/create-term nano-id term)
     ]
    )
  )


;; Get tomt svar
(defn convert-occupation-name-affinity
  [{:keys [affinityid occupationnameid percentage]}]
  {:pre [affinityid occupationnameid percentage]}

  (let [temp-id-affinity-from-concept (str "occupation-name-"  affinityid )
        temp-id-affinity-to-concept (str "occupation-name-" occupationnameid)
        ]
    {:relation/concept-1 temp-id-affinity-from-concept
     :relation/concept-2 temp-id-affinity-to-concept
     :relation/type      :occupation-name-affinity
     :relation/affinity-percentage percentage
     })
  )

(def get-concept-by-legacy-id-query '[:find ?s
                                      :in $ ?legacy-id ?category
                                      :where
                                      [?s :concept.external-database.ams-taxonomy-67/id ?legacy-id]
                                      [?s :concept/category ?category]
                   ])

(defn get-concept-by-legacy-id [legacy-id category]
  (ffirst (d/q get-concept-by-legacy-id-query (get-db) legacy-id category))
  )


;; FEL!! får alla concept typ??
#_(d/q get-concept-by-legacy-id-query (get-db) "7577" :occupation-name)

(defn convert-ssyk-skill
  "This one has to be transacted to the database after skill and occupation-group has been added to the database"
  [{:keys [skillid localegroupid]}]
   {:pre [skillid localegroupid]}
  {:relation/concept-1 (get-concept-by-legacy-id localegroupid :occupation-group)
   :relation/concept-2 (get-concept-by-legacy-id skillid :skill)
   :relation/type :occupation-group-to-skill ;; TODO make up a better name? yrkesmall
   }
  )

(defn convert-occupation-collection
  [{:keys [collectionid name]}]
  {:pre [collectionid name]}

  (let [nano-id (get-nano)]
    [
     (u/create-term nano-id name)
     (u/create-concept nano-id (str "occupation-collection-" collectionid) name name :occupation-collection collectionid)
     ])
  )

(defn convert-occupation-collection-relation
  [{:keys [collectionid occupationnameid]}]
  {:pre [collectionid occupationnameid]}

  {:relation/concept-1 (str "occupation-collection-" collectionid)
   :relation/concept-2 (str "occupation-name-" occupationnameid)
   :relation/type    :meronym ; TODO find a better name for this relationship HAS-A ??
   }

  )

(defn convert-occupation-name-replacement
  [{:keys [occupationnameidref occupationnameid term]}]
  {:pre [occupationnameidref occupationnameid term]}
  (let [
        nano-id (get-nano :occupation-name occupationnameid)
        temp-id (str "occupation-name-id-" occupationnameid)
        temp-replaced-by-id (str "occupation-name-id-" occupationnameidref)
        concept (u/create-concept nano-id temp-id term term :occupation-name occupationnameidref)
        deprecated-concept (assoc concept :concept/deprecated true)
        replaced-by-concept (assoc deprecated-concept :concept/replaced-by temp-replaced-by-id)  ;; Utred hur vi hanterar replaced by many different??
        ]
    [replaced-by-concept
     (u/create-term nano-id term)
     ]
    )
  )

(defn convert-popular-synonym
  [{:keys [popularsynonymid term]}]
  {:pre [popularsynonymid term]}

  (let [nano-id (get-nano)
        temp-id (str "popular-synonym-" popularsynonymid)
        concept (u/create-concept nano-id temp-id term term :keyword popularsynonymid)
        ]
    [concept
     (u/create-term nano-id term)
     ]
    )
  )

(defn convert-popular-synonym-occupation-name-relation
  [{:keys [occupationnameid popularsynonymid]}]
  {:pre [occupationnameid popularsynonymid]}
  {:relation/concept-1 (str "popular-synonym-" popularsynonymid)
   :relation/concept-2 (str "occupation-name-" occupationnameid)
   :relation/type :related-to ;; TODO Find better name than related to
   }
  )

(defn convert-occupation-group-isco-relation [{:keys [occupationgroupid localegroupid]}]
  {:pre  [occupationgroupid localegroupid]}
  {:relation/concept-1  (str "occupation-group-" localegroupid )
   :relation/concept-2  (str "isco-" occupationgroupid)
   :relation/type :related-to ;; TODO Find better name than related to
   }
  )
;; (def a-occgr-isco-rel (first (fetch-data get-occupation-group-isco-level-4-relation)))

(defn convert
  ""
  []
  (concat
   (mapcat convert-occupation-name (fetch-data get-occupation-name))
   (mapcat convert-ssyk (fetch-data get-occupation-group-ssyk))
   (mapcat convert-ssyk-level-3 (fetch-data get-ssyk-level-3))
   (mapcat convert-ssyk-level-2 (fetch-data get-ssyk-level-2))
   (mapcat convert-ssyk-level-1 (fetch-data get-ssyk-level-1))
   (mapcat convert-occupation-field (fetch-data  get-occupation-field))
   (mapcat convert-isco (fetch-data get-isco-level-4))
   (map convert-occupation-name-affinity (fetch-data get-occupation-name-affinity))
   (mapcat convert-occupation-collection (fetch-data get-occupation-collection))
   (map convert-occupation-collection-relation (fetch-data get-occupation-collection-relations))
   (mapcat convert-popular-synonym (fetch-data get-popular-synonym))
   (map convert-popular-synonym-occupation-name-relation (fetch-data get-occupation-name-synonym))
   (map convert-occupation-group-isco-relation  (remove #(= -2 (:localegroupid %)  )  (fetch-data get-occupation-group-isco-level-4-relation)))
   )
  )
