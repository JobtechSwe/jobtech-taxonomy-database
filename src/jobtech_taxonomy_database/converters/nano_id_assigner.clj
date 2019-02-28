(ns jobtech-taxonomy-database.converters.nano-id-assigner
  (:gen-class)
  (:require [cheshire.core :refer :all]
            [nano-id.custom :refer [generate]]))


(def base-58-nano-id (generate "123456789abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ"))

(defn generate-new-id []
  "Specify format and length of nano ID"
  (base-58-nano-id 10)
  )

(defn add-underscore [id]
  (str (subs id 0 4) "_" (subs id 4 7)  "_" (subs id 7 10)  )
  )

(defn generate-new-id-with-underscore []
  (add-underscore (generate-new-id))
  )

(defn open-json
  "Open json, return map with json keyword formatted as clojure keywords."
  []
  (parse-string (slurp "resources/taxonomy_to_concept.json") true)
  )

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

#_(defn get-nano
  "Compare ID + description from sql with json.
  If same, take nanoID from json. Else, generate new nanoID"
  [category-67 id-67 description-67]
  (let [eval-description (compare-descriptions category-67 id-67 description-67)
        nano-id (compare-ids category-67 id-67)]
    (if (and (= "same description" eval-description)
             (not= nano-id "no nano-id")) nano-id (generate-new-id-with-underscore))))

(defn get-nano
  [category id]
  (get-in taxonomy-67 [(keyword category) (keyword id) :conceptId] (generate-new-id-with-underscore))
  )
