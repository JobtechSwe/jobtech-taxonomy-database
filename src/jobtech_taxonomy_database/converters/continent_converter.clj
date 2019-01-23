(ns jobtech-taxonomy-database.converters.continent-converter
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
  "Open json, return map with json keyword formatted as clojure keywords."
  []
  (parse-string (slurp "resources/taxonomy_to_concept.json") true))

(defn get-json-category
  "Return all data in the specific category "
  [category]
  (get-in (open-json) [category]))

(defn find-id
  "Open json, lookup NANO-ID in {<category>:{<id>:{'conceptID': NANO-ID}}}"
  [category id]
  (get-in (get-json-category category) [id :conceptId]))

(defn find-description
  "Open json, lookup DESCRIPTION in {<category>:{<id>:{'preferredTerm': DESCRIPTION}}}"
  [category id]
  (get-in (get-json-category category) [id :preferredTerm]))

(defn compare-ids
  "Compare taxonomy-ID from sql and taxonomy-ID from json"
  [category-67 id-67]
  (let [id (find-id category-67 id-67)]
    (if id id "no nano-id")))

(defn compare-descriptions
  "Compare concept description from sql and concept description from json"
  [category-67 id-67 description-67]
  (let [description (find-description category-67 id-67)]
    (if (= description-67 description) "same description" "different description")))

(defn get-nano
  "Compare ID + description from sql with json.
  If same, take nanoID from json. Else, generate new nanoID"
  [category-67 id-67 description-67]
  (let [eval-description (compare-descriptions category-67 id-67 description-67)
        nano-id (compare-ids category-67 id-67)]
    (if (and (= "same description" eval-description)
             (not= nano-id "no nano-id")
             ) nano-id (generate-new-id))))

(defn converter
  "Immutable language converter."
  [data]
  (let [category-67 :continent ;json-nyckeln
        id-67 (keyword (str (:continentid data))) ;ska matcha legacyAmsTaxonomyId i json
        description-67 (:term data)] ;ska matcha preferredTerm i json
    (let [nano-id (get-nano category-67 id-67 description-67)]
      [{:concept/id               nano-id
        :concept/description      description-67
        :concept/preferred-term   nano-id
        :concept/alternative-terms #{nano-id}}
       {:db/id                    nano-id
        :term/base-form           description-67}])))

(defn convert
  ""
  []
  (mapcat converter  (fetch-data get-continent)))
