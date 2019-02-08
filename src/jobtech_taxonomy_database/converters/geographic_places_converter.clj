(ns jobtech-taxonomy-database.converters.geographic-places-converter
  (:gen-class)
  (:require [datomic.client.api :as d]
            [jobtech-taxonomy-database.schema :refer :all :as schema]
            [jobtech-taxonomy-database.legacy-migration :refer :all]
            [jobtech-taxonomy-database.config :refer :all]
            [jobtech-taxonomy-database.datomic-connection :refer :all :as conn]
            [jobtech-taxonomy-database.converters.nano-id-assigner :refer :all]
            [cheshire.core :refer :all]
            [nano-id.custom :refer [generate]]))

(def one-query-object {:continent-id 2,
        :continent-term "Europa/EU/EES/EURES",
        :country-id 59,
        :country-term "Finland",
        :region-eu-id 1,
        :region-nuts-code-level-3 "FI197",
        :region-eu-term "Pirkanmaa"})

#_
(defn convert
  ""
  []
  (mapcat converter (fetch-data get-geographic-places)))