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

(defn convert-deprecated-occupation [{:keys [occupation-name-id]}]
  {:pre [occupation-name-id]}
  (u/deprecate-concept t/occupation-name occupation-name-id )
  )

(defn create-new-occupation-name
  [{:keys [term occupation-name-id parent-id-ssyk-4  parent-id-isco-4]}]
  {:pre  [term occupation-name-id parent-id-ssyk-4  parent-id-isco-4]}

  (let    [entity-id-parent-ssyk  (u/get-entity-id-by-legacy-id parent-id-ssyk-4 t/ssyk-level-4)
           entity-id-parent-isco  (u/get-entity-id-by-legacy-id parent-id-isco-4 t/isco-level-4)
           concept (u/create-concept t/occupation-name term term occupation-name-id)
           concept-term (u/create-term-from-concept concept)
           ]
    (remove nil? [
                  concept
                  concept-term
                  (when entity-id-parent-ssyk (u/create-broader-relation-to-concept concept entity-id-parent-ssyk )) ;; sometimes ssyk is -1 and becomes nil
                  (u/create-broader-relation-to-concept concept entity-id-parent-isco)
                  ])))

(defn update-occupation-name-preferred-label [{:keys [occupation-name-id-67 term-68 ]}]
  {:pre [occupation-name-id-67 term-68 ]}
  (let [entity-id  (u/get-entity-id-by-legacy-id occupation-name-id-67 t/occupation-name )]
    (u/update-preferred-term entity-id term-68 term-68  ))
  )


(defn update-occupation-name-relations [{:keys [occupation-name-id-67
                                                parent-id-ssyk-4-67
                                                parent-id-ssyk-4-68
                                                parent-id-isco-4-67
                                                parent-id-isco-4-68]}]
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
      ))))

(defn convert-replaced-by-occuaption-name [ {:keys [replaced-id replacing-id]}]
  (u/replace-concept replaced-id replacing-id t/occupation-name)
  )

;; get-new-occupation-collection

;; TODO below is broken after Henrik's utils fix ;;  du är här
(defn convert-new-occupation-collection
  [{:keys [collection-id collection-name]}]
  {:pre [collection-id collection-name]}

  (let [concept (u/create-concept t/occupation-collection
                                  collection-name
                                  collection-name
                                  collection-id)
        term (u/create-term-from-concept concept)
        ]
    [concept
     term
     ]
    ))

;; get-new-occupation-collection-relations
(defn convert-new-occupation-collection-relation
  [{:keys [collection-id occupation-name-id]}]
  {:pre [collection-id occupation-name-id]
   :post [ (:relation/concept-1 %)  (:relation/concept-2 %)   (:relation/type %)]
   }
  (let [
        concept-entity-id-1 (u/get-entity-if-exists-or-temp-id collection-id t/occupation-collection)
        concept-entity-id-2 (u/get-entity-if-exists-or-temp-id occupation-name-id t/occupation-name)
        ]
    (u/create-relation concept-entity-id-1 concept-entity-id-2 t/related )
    )
  )


(defn convert []
  "Run this function after the database has been loaded"
  (remove empty?
    (concat
       (map convert-deprecated-occupation (fetch-data get-deprecated-occupation-name))
       (map create-new-occupation-name (fetch-data get-new-occupation-name))
       (map update-occupation-name-relations (fetch-data get-updated-occupation-name-relation-to-parent))
       (map convert-replaced-by-occuaption-name  (fetch-data get-replaced-occupation-name))
       (mapcat convert-new-occupation-collection (fetch-data get-new-occupation-collection))
       (map convert-new-occupation-collection-relation (fetch-data get-new-occupation-collection-relations))
       )))














