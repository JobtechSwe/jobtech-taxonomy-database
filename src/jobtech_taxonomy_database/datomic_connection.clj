(ns jobtech-taxonomy-database.datomic-connection
  (:gen-class)
  (:require [datomic.client.api :as d]
            [jobtech-taxonomy-database.config :refer :all]
            [jobtech-taxonomy-database.schema :refer :all :as schema]))

(def find-concept-by-preferred-term-query
  '[:find (pull ?c
                [:concept/id
                 :concept/description
                 {:concept/preferred-term [:term/base-form]}
                 {:concept/referring-terms [:term/base-form]}])
    :in $ ?term
    :where [?t :term/base-form ?term]
    [?c :concept/preferred-term ?t]])
;; (d/q find-concept-by-preferred-term-query (get-db) "Kontaktmannaskap")

;;;; Private ;;;;


(declare db-conn)

(defn ^:private get-conn-with-config
  [config]
  ;; create connection unless already created
  (if (not (bound? #'db-conn))
    (let [client (d/client (get config :datomic-cfg))]
      (def db-conn (d/connect client {:db-name (get config :datomic-name)}))))
  db-conn)

(defn ^:private get-db-with-config
  [config]
  (d/db (get-conn-with-config config)))

(defn ^:private init-new-db-with-conn [conn]
  (d/transact conn {:tx-data (vec (concat schema/term-schema
                                          schema/concept-schema
                                          schema/concept-schema-extras
                                          schema/concept-relation-schema))}))

;;;; Public ;;;;

;; TODO fix a smoother handling of a default config perhaps with
;; some type of optional argument if clojure has that? TODO2: learn clojure...

(defn get-db
  ([]       (get-db-with-config (get-datomic-config)))
  ([config] (get-db-with-config config)))

(defn get-conn
  ([]       (get-conn-with-config (get-datomic-config)))
  ([config] (get-conn-with-config config)))

(defn init-new-db
  ([]       (init-new-db-with-conn (get-conn)))
  ([conn]   (init-new-db-with-conn conn)))


(defn get-client
  ([]       (d/client (get (get-datomic-config) :datomic-cfg)))
  ([config] (d/client (get config :datomic-cfg))))

(defn transact-data [data]
  (d/transact (get-conn) {:tx-data data} ))


;(def database-name "jobtech-taxonomy-development")
;(def database-name "jobtech-taxonomy-production")

(def database-name "jobtech-taxonomy-henrik-dev")

(defn delete-database
  ([]       (d/delete-database (get-client) {:db-name database-name}  ))
  ([config] (let [db-name (get config :datomic-name)]
              (d/delete-database (get-client config) {:db-name db-name}  ))))

(defn create-database
  ([]       (d/create-database (get-client) {:db-name database-name}  ))
  ([config] (let [db-name (get config :datomic-name)]
              (d/create-database (get-client config) {:db-name db-name}  ))))

(defn list-databases
  ([]       (d/list-databases (get-client) {:db-name database-name}  ))
  ([config] (d/list-databases (get-client config) {:db-name nil}  )))


;; (d/create-database client {:db-name "jobtech-taxonomy-development"}  )
#_("demo"
   "jobtech-taxonomy-development"
   "jobtech-taxonomy-henrik-dev"
   "jobtech-taxonomy-joakim-dev"
   "jobtech-taxonomy-production"
   "jobtech-taxonomy-simon-dev"
   "taxonomy_v13")
