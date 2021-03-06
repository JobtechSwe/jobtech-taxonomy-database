(ns jobtech-taxonomy-database.datomic-connection
  (:gen-class)
  (:require [datomic.client.api :as d]
            [jobtech-taxonomy-database.config :refer :all]
            [jobtech-taxonomy-database.schema :as schema]
            [jobtech-taxonomy-database.converters.version-0 :as version-zero]))


;;;; Private ;;;;

(def ^:private ^{:arglists '([config])} get-conn-with-config
  "create connection unless already created"
  (memoize
   (fn [config]
     (-> config
         :datomic-cfg
         d/client
         (d/connect {:db-name (:datomic-name config)})))))

(defn ^:private get-db-with-config
  [config]
  (d/db (get-conn-with-config config)))

(defn ^:private init-new-db-with-conn [conn]
  (println "Create database schema")
  (d/transact conn {:tx-data (vec (concat
                                   schema/concept-schema
                                   schema/concept-schema-extras
                                   schema/concept-relation-schema
                                   schema/version-schema
                                   schema/user-schema
                                   schema/daynote-schema))}))

;;;; Public ;;;;

;; TODO fix a smoother handling of a default config perhaps with

(defn get-db
  ([] (get-db-with-config (get-datomic-config)))
  ([config] (get-db-with-config config)))

(defn get-conn
  ([] (get-conn-with-config (get-datomic-config)))
  ([config] (get-conn-with-config config)))

(defn init-new-db
  ([] (init-new-db-with-conn (get-conn)))
  ([conn] (init-new-db-with-conn conn)))

(defn get-client
  ([] (d/client (get (get-datomic-config) :datomic-cfg)))
  ([config] (d/client (get config :datomic-cfg))))

(defn transact-data [data]
  (d/transact (get-conn) {:tx-data data}))

(defn delete-database
  ([] (d/delete-database (get-client) {:db-name (:datomic-name (get-datomic-config))}))
  ([config] (let [db-name (get config :datomic-name)]
              (d/delete-database (get-client config) {:db-name db-name}))))

(defn create-database
  ([] (d/create-database (get-client) {:db-name (:datomic-name (get-datomic-config))}))
  ([config] (let [db-name (get config :datomic-name)]
              (d/create-database (get-client config) {:db-name db-name}))))

(defn list-databases
  ([] (d/list-databases (get-client) {}))
  ([config] (d/list-databases (get-client config) {})))

(defn list-database []
  (d/list-databases (get-client) {}))

(defn delete-databse-loop-fn [the-database]
  (d/delete-database (get-client) {:db-name the-database}))

(defn delete-a-lot-of-databases []
  (map delete-databse-loop-fn [
                               "jobtech-taxonomy-development-2019-10-11-v2"
                               "jobtech-taxonomy-development-2019-10-15-v1"
                               "jobtech-taxonomy-development-2019-10-16-v1"
                               "jobtech-taxonomy-development-2019-10-16-v2"
                               "jobtech-taxonomy-development-2019-10-16-v3"
                               "jobtech-taxonomy-development-2019-10-17-v1"
                               "jobtech-taxonomy-per-dev4-841383691"
                               "jobtech-taxonomy-per-dev4-857150990"
                               "jobtech-taxonomy-per-dev4-896556754"
                               "jobtech-taxonomy-per-dev4-90536461"
                               "jobtech-taxonomy-per-dev4-911106275"
                               "jobtech-taxonomy-per-dev4-928405731"
                               "jobtech-taxonomy-per-dev4-940615547"
                               "jobtech-taxonomy-per-dev4-948533544"
                               "jobtech-taxonomy-per-dev4-949184561"
                               "jobtech-taxonomy-per-dev4-951970430"
                               "jobtech-taxonomy-per-dev4-972583121"
                               "jobtech-taxonomy-per-dev4-989720794"
                               "jobtech-taxonomy-production-2019-10-23-1"
                               "jobtech-taxonomy-production-2019-10-23-2"
                               "jobtech-taxonomy-production-2019-10-23-3"
                               "jobtech-taxonomy-production-2019-10-23-4"
                               "jobtech-taxonomy-production-2019-10-23-5"]))
