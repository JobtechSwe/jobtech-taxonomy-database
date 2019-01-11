(ns jobtech-taxonomy-database.country-converter
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
  [{:concept/id (str  (:countryid  data))
    :concept/description       (:term data)
    :concept/preferred-term     (str  (:countryid  data))
    :concept/alternative-terms #{(str  (:countryid  data))}}
   {:db/id  (str  (:countryid  data))
    :term/base-form (:term data)}])


(defn convert [] (mapcat converter  (fetch-data get-country)))


(defn writer
  [data]

  (d/transact (get-conn) {:tx-data data}))
