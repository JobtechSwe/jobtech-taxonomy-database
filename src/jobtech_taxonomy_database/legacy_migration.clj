(ns jobtech-taxonomy-database.legacy-migration
  (:gen-class)
  (:require
   [hugsql.core :as hugsql]
   ))


(def db
  {
   :subprotocol "mssql"
   :subname "//localhost:1433"
   :user "SA"
   :password "Taxonomy123!"
   :sslmode "require"
   })

(hugsql/def-db-fns "jobtech_taxonomy_database/sql/legacy-taxonomy.sql")

(hugsql/def-sqlvec-fns "jobtech_taxonomy_database/sql/legacy-taxonomy.sql")


(defn get-skillheadlines
  "Get all skill headlines for `main-headline-id`. Wrap the db specification."
  [main-headline-id]
  (get-skillheadlines-backend db main-headline-id))

(defn get-skillmainheadlines
  "Get all skill main headlines. Wrap the db specification."
  []
  (get-skillmainheadlines-backend db))
