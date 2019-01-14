(ns jobtech-taxonomy-database.legacy-migration
  (:gen-class)
  (:require [hugsql.core :as hugsql]
            [jobtech-taxonomy-database.config :refer :all]))

(hugsql/def-db-fns "jobtech_taxonomy_database/sql/legacy-taxonomy.sql")

(hugsql/def-sqlvec-fns "jobtech_taxonomy_database/sql/legacy-taxonomy.sql")

(defn fetch-data [hugsql-function & args]
  "Call database with a hugsql function"
  (hugsql-function (get-legacydb-config) (if args (first args) nil)))
