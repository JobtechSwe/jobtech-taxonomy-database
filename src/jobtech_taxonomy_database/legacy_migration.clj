(ns jobtech-taxonomy-database.legacy-migration
  (:gen-class)
  (:require [hugsql.core :as hugsql]
            [jobtech-taxonomy-database.config :refer :all]))

(hugsql/def-db-fns "jobtech_taxonomy_database/sql/legacy-taxonomy.sql")

(hugsql/def-sqlvec-fns "jobtech_taxonomy_database/sql/legacy-taxonomy.sql")

(defn get-skillheadlines
  "Get all skill headlines for `main-headline-id`. Wrap the db specification."
  [main-headline-id]
  (get-skillheadlines-converter-backend (get-legacydb-config) main-headline-id))

(defn get-skillmainheadlines
  "Get all skill main headlines. Wrap the db specification."
  []
  (get-skillmainheadlines-backend (get-legacydb-config)))

(defn fetch-data [hugsql-function]
  "Call database with a hugsql function"
  (hugsql-function (get-legacydb-config)))
