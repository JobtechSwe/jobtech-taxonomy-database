(ns jobtech-taxonomy-database.converters.wage-type-converter
  (:gen-class)
  (:require [datomic.client.api :as d]
            [jobtech-taxonomy-database.schema :refer :all :as schema]
            [jobtech-taxonomy-database.legacy-migration :refer :all]
            [jobtech-taxonomy-database.config :refer :all]
            [jobtech-taxonomy-database.datomic-connection :refer :all :as conn]))

(defn ^:private fake-id
  "Temporary function until new id function with nano-ids is imported into this project."
  [id]
  (format "%010d" id))

(defn converter
  "Immutable language converter."
  [data]
  [{:concept/id (str  (:löneformsid  data))
    :concept/description       (:beteckning data)
    :concept/preferred-term     (str  (:löneformsid  data))
    :concept/alternative-terms #{(str  (:löneformsid  data))}}
   {:db/id  (str  (:löneformsid  data))
    :term/base-form (:beteckning data)}])

(defn convert
  ""
  []
  (mapcat converter  (fetch-data get-wage-type)))