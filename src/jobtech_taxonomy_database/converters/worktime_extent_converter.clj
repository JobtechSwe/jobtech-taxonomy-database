(ns jobtech-taxonomy-database.converters.worktime-extent-converter
  (:gen-class)
  (:require [datomic.client.api :as d]
            [jobtech-taxonomy-database.schema :refer :all :as schema]
            [jobtech-taxonomy-database.legacy-migration :refer :all]
            [jobtech-taxonomy-database.config :refer :all]
            [jobtech-taxonomy-database.datomic-connection :refer :all :as conn]))

;;FIXME Henrik, fortsätt på denna!

(defn ^:private fake-id
  "Temporary function until new id function with nano-ids is imported into this project."
  [id]
  (format "%010d" id))

(defn converter
  "Immutable language converter."
  [data]
  [{:concept/id (str (:arbetstidsid  data))
    :concept/description       (:beteckning data)
    :concept/preferred-term     (str  (:arbetstidsid  data))
    :concept/alternative-terms #{(str  (:arbetstidsid  data))}}
   {:db/id  (str  (:arbetstidsid  data))
    :term/base-form (:beteckning data)}])

(defn convert
  ""
  []
  (mapcat converter  (fetch-data get-worktime-extent)))

(defn writer
  ""
  [data]
  (d/transact (get-conn) {:tx-data data}))
