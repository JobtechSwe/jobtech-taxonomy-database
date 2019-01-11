(ns jobtech-taxonomy-database.language-level-converter
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
  [language-level]
  [{:concept/id (str  (:languagelevelid  language-level))
    :concept/description       (:term language-level)
    :concept/preferred-term     (str  (:languagelevelid  language-level))
    :concept/alternative-terms #{(str  (:languagelevelid  language-level))}}
   {:db/id  (str  (:languagelevelid  language-level))
    :term/base-form (:term language-level)}])

(defn convert [] (mapcat converter  (fetch-data get-language-level)  ))

(defn writer
  [data]

  (d/transact (get-conn) {:tx-data data}))
