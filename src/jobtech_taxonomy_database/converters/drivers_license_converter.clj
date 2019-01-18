(ns jobtech-taxonomy-database.converters.drivers-license-converter
  (:gen-class)
  (:require [datomic.client.api :as d]
            [jobtech-taxonomy-database.schema :refer :all :as schema]
            [jobtech-taxonomy-database.legacy-migration :refer :all]
            [jobtech-taxonomy-database.config :refer :all]
            [jobtech-taxonomy-database.datomic-connection :refer :all :as conn]
            [cheshire.core :refer :all]
            [nano-id.custom :refer [generate]]))

(def base-59-nano-id (generate "123456789abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ_"))

(defn generate-new-id
  "Specify format and length of nano ID"
  []
  (base-59-nano-id 10))

(defn open-json
  "Open json file, return as map with json keyword formatted as clojure keywords."
  []
  (parse-string (slurp "resources/taxonomy_to_concept.json") true))

(defn find-id
  "Open json, lookup NANO-ID in {'country':{<id>:{'conceptID': NANO-ID}}}"
  [id]
  (let [nano (get-in (open-json) [:driving-license (keyword (str id)) :conceptId])]
    (if nano nano (generate-new-id))))

(defn converter
  "Immutable language converter."
  [data]
  (let [nano-id (find-id  (:drivinglicenceid  data))]
  [{:concept/id nano-id
    :concept/description       (:term data)
    :concept/preferred-term     nano-id
    :concept/alternative-terms #{nano-id}}
   {:db/id  nano-id
    :term/base-form (:term data)}]))

(defn convert
  ""
  []
  (mapcat converter  (fetch-data get-drivers-license)))
