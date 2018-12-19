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

;(get-skillmainheadlines db)
;(get-skillheadlines db {:id 2})
