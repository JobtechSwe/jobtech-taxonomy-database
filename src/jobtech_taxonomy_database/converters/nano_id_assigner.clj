(ns jobtech-taxonomy-database.converters.nano-id-assigner
  (:gen-class)
  (:require [cheshire.core :as json]
            [jobtech-taxonomy-database.nano-id :as nano]))

(def taxonomy
  (json/parse-string (slurp "resources/taxonomy-to-concept.json") true))

(defn get-nano [category id]
  {:pre [category id]}
  (get-in taxonomy [(keyword category) (keyword id) :conceptId] (nano/generate-new-id-with-underscore)))
