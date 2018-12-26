(ns jobtech-taxonomy-database.datomic-connection
  (:gen-class)
  (:require [datomic.client.api :as d]
            [jobtech-taxonomy-database.util :refer :all]
            [jobtech-taxonomy-database.schema :refer :all :as schema]))

;; TODO fix a smoother handling of a default config perhaps with
;; some type of optional argument if clojure has that? TODO2: learn clojure...


;;;; Private ;;;;

(defn ^:private get-conn-with-config
  [config]
  ;; create connection unless already created
  (if (not (resolve 'db-conn))
    (let [client (d/client (get config :datomic-cfg))]
      (def db-conn (d/connect client {:db-name (get config :datomic-name)}))))
  db-conn)

(defn ^:private get-db-with-config
  [config]
  ;; create connection unless already created
  (if (not (resolve 'db-handler))
    (def db-handler (d/db (get-conn-with-config config))))
  db-handler)

(defn ^:private init-new-db-with-conn [conn]
  (d/transact conn {:tx-data [schema/term-schema
                              schema/concept-schema
                              schema/concept-schema-extras
                              schema/concept-relation-schema ]}))

;;;; Public ;;;;

(defn get-db
  ([]       (get-db-with-config (get-config)))
  ([config] (get-db-with-config config)))

(defn get-conn
  ([]       (get-conn-with-config (get-config)))
  ([config] (get-conn-with-config config)))

(defn init-new-db
  ([]       (init-new-db-with-conn (get-conn)))
  ([conn]   (init-new-db-with-conn conn)))
