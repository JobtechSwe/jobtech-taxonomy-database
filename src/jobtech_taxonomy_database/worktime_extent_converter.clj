(ns jobtech-taxonomy-database.worktime-extent-converter
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
  [worktime-extent]
  [{:concept/id (str  (:languagelevelid  worktime-extent))
    :concept/description       (:term worktime-extent)
    :concept/preferred-term     (str  (:languagelevelid  worktime-extent))
    :concept/alternative-terms #{(str  (:languagelevelid  worktime-extent))}}
   {:db/id  (str  (:languagelevelid  worktime-extent))
    :term/base-form (:term worktime-extent)}])

(defn convert [] (mapcat converter  (fetch-data get-worktime-extent)  ))

(defn writer
  [data]

  (d/transact (get-conn) {:tx-data data}))
