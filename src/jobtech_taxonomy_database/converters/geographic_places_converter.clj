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

(defn ^:private make-tempid
  "Turn a string into a temporary ID that will not be used by other converters.
  Accepts a string and returns a string."
  [str]
  (format "skills-converter-%s" str))

(defn ^:private make-tempid-term
  ""
  [term lang]
  (make-tempid (format "term-%s-%s" term lang)))

(defn ^:private make-tempid-concept
  ""
  [type id]
  (make-tempid (format "skill-concept-%s-%d" type id)))


(defn convert-continents
  ""
  [continent]
  (let [country-conv (mapcat (fn [headline]
                                 (convert-head continent headline))
                               (fetch-data get-country-for-continent {:id (get continent :continent_id)})
                               )]
    (concat country-conv
            (let* [category-67 :country
                   id-67 (keyword (str (get continent :continent_id)))           ;ska matcha legacyAmsTaxonomyId i json
                   description-67 (get continent :continent_term)                ;ska matcha preferredTerm i json
                   nano-id (get-nano category-67 id-67 description-67)]
              (list {:db/id                     (make-tempid-concept "continent" (get continent :continent_id))
                     :concept/id                nano-id
                     :concept/description     (get continent :continent_term)
                     :concept/preferred-term    (make-tempid-term (get continent :continent_term); TODO what about this: (get continent :lang))}
                    {:db/id                  (make-tempid-term (get continent :main_term) (get continent :lang))
                     :term/base-form            (get continent :main_term)})))))

(defn convert
  ""
  []
  (distinct                                  ;do this only once per value
    (mapcat (fn [continent]                  ;send each continent to fn and then add result to new collection
              (convert-continents continent))  ;this is what fn does
            (fetch-data get-continent))))    ;this returns a collection with continents