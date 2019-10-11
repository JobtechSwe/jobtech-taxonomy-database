(ns jobtech-taxonomy-database.converters.nano-id-assigner
  (:gen-class)
  (:require [cheshire.core :refer :all]
            [clojure.string :as str]
            [jobtech-taxonomy-database.nano-id :as nano]))

(defn open-json
  "Open json, return map with json keyword formatted as clojure keywords."
  []
  (parse-string (slurp "resources/right-taxonomy-dump.json") true))

(def taxonomy-67 (open-json))

(defn get-json-category
  "Return all data in the specific category "
  [category]
  (get-in taxonomy-67 [category]))

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

(defn get-nano [category id]
  {:pre [category id]}
  (get-in taxonomy-67 [(keyword category) (keyword id) :conceptId] (nano/generate-new-id-with-underscore)))

(defn append-line "" [file line]
  (with-open [w (clojure.java.io/writer file :append true)]
    (.write w line)))

(defn get-nano-log-updates "writes to hardcoded filename atm"
  [category id term]
  (let* [lookup-id (get-in taxonomy-67 [(keyword category) (keyword id) :conceptId])]
        (if lookup-id
          lookup-id
          (let [new-id (nano/generate-new-id-with-underscore)]
            (append-line "resources/new-ids.json" (format "{ %s { %s { :preferredTerm \"%s\" :conceptId \"%s\" :type \"%s\" } } }\n" category id term new-id (str/replace category #"^:(.*)$" "$1")))
            new-id))))
