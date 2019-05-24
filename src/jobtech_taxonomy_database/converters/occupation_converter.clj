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
  (let [concept (u/create-concept t/occupation-field term description localefieldid )
        concept-term (u/create-term (:concept/id concept) term)]
    [concept
     concept-term]))

(defn convert-ssyk-level-4
  [{:keys [localecode term description localegroupid localefieldid localelevel3id]}]
  {:pre [localecode term description localegroupid localefieldid localelevel3id]}
  (let [temp-id-parent-field (u/create-temp-id t/occupation-field localefieldid)
        temp-id-parent-ssyk-level-3 (u/create-temp-id t/ssyk-level-3 localelevel3id)
        concept (u/create-concept t/occupation-group term description localegroupid )
        concept-with-extras (assoc concept :concept.external-standard/ssyk-2012 localecode)
        concept-term (u/create-term-from-concept concept-with-extras)]
    [concept-with-extras
     concept-term
     (u/create-broader-relation-to-concept concept temp-id-parent-field)
     (u/create-broader-relation-to-concept concept temp-id-parent-ssyk-level-3)]))

(defn convert-ssyk-level-3
  [{:keys [localelevel3id localecodelevel3 term localelevel2id]}]
  {:pre [localelevel3id localecodelevel3 term localelevel2id]}
  (let [temp-id-parent-level-2 (u/create-temp-id t/ssyk-level-2 localelevel2id)
        concept (u/create-concept t/ssyk-level-3 term term localelevel3id)
        concept-with-extras (assoc concept :concept.external-standard/ssyk-2012 localecodelevel3)
        concept-term (u/create-term-from-concept concept-with-extras)]
    [concept-with-extras
     concept-term
     (u/create-broader-relation-to-concept concept temp-id-parent-level-2)]))

(defn convert-ssyk-level-2
  [{:keys [localelevel2id localelevel1id localecodelevel2 term]}]
  {:pre [localelevel2id localelevel1id localecodelevel2 term]}
  (let [temp-id-parent-level-1 (u/create-temp-id t/ssyk-level-1 localelevel1id)
        concept (u/create-concept t/ssyk-level-2 term term localelevel2id)
        concept-with-extras (assoc concept :concept.external-standard/ssyk-2012 localecodelevel2)
        concept-term (u/create-term-from-concept concept-with-extras)]
    [concept-with-extras
     concept-term
     (u/create-broader-relation-to-concept concept-with-extras temp-id-parent-level-1)]))

(defn convert-ssyk-level-1
  [{:keys [localelevel1id localecodelevel1 term]}]
  {:pre [localelevel1id localecodelevel1 term]}
  (let [concept (u/create-concept t/ssyk-level-1 term term localelevel1id)
        concept-with-extras (assoc concept :concept.external-standard/ssyk-2012 localecodelevel1)
        concept-term (u/create-term-from-concept concept-with-extras)]
    [concept-with-extras
     concept-term]))

(defn convert-occupation-name
  [{:keys [term occupationgroupid occupationnameid localegroupid]}]
  {:pre [term occupationgroupid occupationnameid localegroupid]}
  (let [
        temp-id-parent-ssyk (u/create-temp-id t/occupation-group localegroupid)
        temp-id-parent-isco (u/create-temp-id t/isco-level-4 occupationgroupid)
        concept (u/create-concept t/occupation-name term term occupationnameid)
        concept-term (u/create-term-from-concept concept)]
    [concept
     concept-term
     (u/create-broader-relation-to-concept concept temp-id-parent-ssyk)
     (u/create-broader-relation-to-concept concept temp-id-parent-isco)]))

(defn convert-isco-level-4
  [{:keys [isco occupationfieldid term occupationgroupid description]}]
  {:pre [isco occupationfieldid term occupationgroupid description]}
  (let [temp-id-parent-level-1 (u/create-temp-id t/isco-level-1 occupationfieldid)
        concept (u/create-concept t/isco-level-4 term description occupationgroupid )
        concept-with-extras (assoc concept :concept.external-standard/isco-08 isco)
        concept-term (u/create-term-from-concept concept-with-extras)]
    [concept-with-extras
     concept-term
     (u/create-broader-relation-to-concept concept temp-id-parent-level-1)]))

(defn convert-isco-level-1
  [{:keys [occupationfieldid term  description]}]
  {:pre [occupationfieldid term description]}
  (let [concept (u/create-concept t/isco-level-1 term description occupationfieldid)
        concept-term (u/create-term-from-concept concept)]
    [concept
     concept-term]))

(defn convert-occupation-name-affinity
  [{:keys [affinityid occupationnameid percentage]}]
  {:pre [affinityid occupationnameid percentage]}
  (let [temp-id-affinity-from-concept (u/create-temp-id t/occupation-name affinityid)
        temp-id-affinity-to-concept (u/create-temp-id t/occupation-name occupationnameid)
        relation (u/create-relation  temp-id-affinity-from-concept temp-id-affinity-to-concept t/occupation-name-affinity)
        relation-with-affinity-percentage (assoc relation :relation/affinity-percentage percentage )]
    relation-with-affinity-percentage))

#_
(def get-concept-by-legacy-id-query '[:find ?s
                                      :in $ ?legacy-id ?category
                                      :where
                                      [?s :concept.external-database.ams-taxonomy-67/id ?legacy-id]
                                      [?s :concept/category ?category]])
#_
(defn get-concept-by-legacy-id [legacy-id category]
  (ffirst (d/q get-concept-by-legacy-id-query (get-db) legacy-id category)))

;; FEL!! får alla concept typ??
#_(d/q get-concept-by-legacy-id-query (get-db) "7577" :occupation-name)

(defn convert-ssyk-skill
  "This one has to be transacted to the database after skill and occupation-group has been added to the database"
  [{:keys [skillid localegroupid]}]
   {:pre [skillid localegroupid]}
  {:relation/concept-1 (u/get-entity-id-by-legacy-id localegroupid t/occupation-group)
   :relation/concept-2 (u/get-entity-id-by-legacy-id skillid t/skill)
   :relation/type      t/occupation-group-to-skill
   }
  )

(defn convert-occupation-collection
  [{:keys [collectionid name]}]
  {:pre [collectionid name]}
  (let [concept (u/create-concept t/occupation-collection name name collectionid)
        concept-term (u/create-term-from-concept concept)]
    [concept
     concept-term]))

(defn convert-occupation-collection-relation
  [{:keys [collectionid occupationnameid]}]
  {:pre [collectionid occupationnameid]}
  {:relation/concept-1 (u/create-temp-id t/occupation-collection collectionid)
   :relation/concept-2 (u/create-temp-id t/occupation-name occupationnameid)
   :relation/type t/related
   }
  )

(defn convert-occupation-name-replacement
  [{:keys [occupationnameidref occupationnameid term]}]
  {:pre [occupationnameidref occupationnameid term]}
  (let [temp-id-replaced-by (u/get-entity-if-exists-or-temp-id occupationnameidref t/occupation-name )
        concept (u/create-concept t/occupation-name term term occupationnameid )
        concept-deprecated-true (assoc concept :concept/deprecated true)
        concept-replaced-by (assoc concept-deprecated-true :concept/replaced-by temp-id-replaced-by)]
        ;; Utred hur vi hanterar replaced by many different??
    [concept-replaced-by
     (u/create-term-from-concept concept-replaced-by)]))

(defn convert-popular-synonym
  [{:keys [popularsynonymid term]}]
  {:pre [popularsynonymid term]}

  (let [ concept (u/create-concept t/keyword-type term term popularsynonymid )
        ]
    [concept
     (u/create-term-from-concept concept)
     ]
    )
  )

(defn convert-popular-synonym-occupation-name-relation
  [{:keys [occupationnameid popularsynonymid]}]
  {:pre [occupationnameid popularsynonymid]}
  (u/create-relation (u/create-temp-id t/keyword-type popularsynonymid)
                     (u/create-temp-id t/occupation-name occupationnameid)
                     t/related
                     ))

(defn convert-occupation-group-isco-relation [{:keys [occupationgroupid localegroupid]}]
  {:pre  [occupationgroupid localegroupid]}

  (u/create-relation (u/create-temp-id t/occupation-group localegroupid)
                     (u/create-temp-id t/isco-level-4 occupationgroupid)
                     t/related
                     )
  )
;; (def a-occgr-isco-level-4-rel (first (fetch-data get-occupation-group-isco-level-4-level-4-relation)))

(defn convert
  ""
  []
  (concat
    (mapcat convert-occupation-field (fetch-data  get-occupation-field))
    (mapcat convert-ssyk-level-4 (fetch-data get-occupation-group-ssyk))
    (mapcat convert-ssyk-level-3 (fetch-data get-ssyk-level-3))
    (mapcat convert-ssyk-level-2 (fetch-data get-ssyk-level-2))
    (mapcat convert-ssyk-level-1 (fetch-data get-ssyk-level-1))
    (mapcat convert-occupation-name (fetch-data get-occupation-name))
    (mapcat convert-isco-level-4 (fetch-data get-isco-level-4))
    (mapcat convert-isco-level-1 (fetch-data get-isco-level-1))
    (map convert-occupation-name-affinity (fetch-data get-occupation-name-affinity))
    (mapcat convert-occupation-collection (fetch-data get-occupation-collection))
   (map convert-occupation-collection-relation (fetch-data get-occupation-collection-relations))
   (mapcat convert-popular-synonym (fetch-data get-popular-synonym))
   (map convert-popular-synonym-occupation-name-relation (fetch-data get-occupation-name-synonym))
   (map convert-occupation-group-isco-relation  (remove #(= -2 (:localegroupid %)  )  (fetch-data get-occupation-group-isco-level-4-relation)))
   (mapcat convert-occupation-name-replacement (fetch-data get-occupation-names-reference))
   )
  )
