(ns jobtech-taxonomy-database.converters.version-68
  (:gen-class)
  (:require [datomic.client.api :as d]
            [jobtech-taxonomy-database.legacy-migration :as legacy-migration]
            [jobtech-taxonomy-database.converters.nano-id-assigner :as nano]
            [jobtech-taxonomy-database.datomic-connection :refer :all :as conn]
            [camel-snake-kebab.core :as csk]
            [clojure.spec.alpha :as s]
            [jobtech-taxonomy-database.types :as t])
  )


#_(def get-relation
    '[:find (pull ?r ["*" {:relation/concept-1 [*]
                           :relation/concept-2 [*]
                           }

                      ])
      :in $ ?relation-type ?legacy-id-1  ?legacy-id-2
      :where
      [?r :relation/type ?relation-type]
      [?r :relation/concept-1 ?c1]
      [?c1 :concept.external-database.ams-taxonomy-67/id ?legacy-id-1]
      [?r :relation/concept-2 ?c2]
      [?c2 :concept.external-database.ams-taxonomy-67/id ?legacy-id-2]
      ]
    )

;; (first (d/q get-relation (conn/get-db) "isco_4_to_skill" "41"  "606897" ))

;; (d/transact (get-conn) {:tx-data [{:taxonomy-version/id 67}]})

;; (d/q '[:find (pull ?v [*])  :in $ :where [?v :taxonomy-version/id]] (get-db) )

(def get-version
  '[:find ?e
    :in $
    :where [?e :taxonomy-version/id 67]
    ]
  )

(defn get-verion-67-entity []
  (ffirst (d/q get-version (get-db))))


#_(defn convert []
  (let [version-db-id (get-verion-67-entity)]
    [{:db/id version-db-id
      :taxonomy-version/id 68}]
    )
  )

(defn convert []
  [{:taxonomy-version/id 68}]
  )
