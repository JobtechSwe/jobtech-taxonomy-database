(ns jobtech-taxonomy-database.datomic-connection
  (:gen-class)
  (:require [datahike.api :as d]
            [jobtech-taxonomy-database.config :as config]
            [jobtech-taxonomy-database.schema :as schema]))

(def ^:private ^{:arglists '([config])} get-conn-with-config
  "create connection unless already created"
  (memoize
   (fn [config]
     (d/connect (:datahike-cfg config)))))

(defn get-db
  ([] (get-db (config/get-datomic-config)))
  ([config] (d/db (get-conn-with-config config))))

(defn get-conn
  ([] (get-conn (config/get-datomic-config)))
  ([config] (get-conn-with-config config)))

(defn init-new-db
  ([] (init-new-db (get-conn)))
  ([conn]
   (println "Create database schema")
   (d/transact conn (vec (concat
                          schema/concept-schema
                          schema/concept-schema-extras
                          schema/concept-relation-schema
                          schema/version-schema
                          schema/user-schema
                          schema/daynote-schema)))))

(defn transact-data [data]
  (d/transact (get-conn) data))

(defn delete-database
  ([] (delete-database (config/get-datomic-config)))
  ([config] (d/delete-database (:datahike-cfg config))))

(defn create-database
  ([] (create-database (config/get-datomic-config)))
  ([config] (d/create-database (:datahike-cfg config))))