(ns jobtech-taxonomy-database.converters.country-converter
  (:gen-class)
  (:require [datomic.client.api :as d]
            [jobtech-taxonomy-database.schema :refer :all :as schema]
            [jobtech-taxonomy-database.legacy-migration :refer :all]
            [jobtech-taxonomy-database.config :refer :all]
            [jobtech-taxonomy-database.datomic-connection :refer :all :as conn]
            [cheshire.core :refer :all] ;TODO add library to other files
            [nano-id.custom :refer [generate]])) ;TODO add library to other files

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
  (let [nano (get-in (open-json) [:country (keyword (str id)) :conceptId])] ;TODO in other files: change "country" to relevant keyword
    (if nano nano (generate-new-id))))

(defn converter
  "Immutable language converter."
  [data]
  (let [nano-id (find-id  (:countryid  data))] ;TODO in other files: change "countryid" to relevant keyword
  [{:concept/id nano-id
    :concept/description       (:term data)
    :concept/preferred-term     nano-id
    :concept/alternative-terms #{nano-id}}
   {:db/id  nano-id
    :term/base-form (:term data)}]))

(defn convert
  ""
  []
  (mapcat converter  (fetch-data get-country)))
