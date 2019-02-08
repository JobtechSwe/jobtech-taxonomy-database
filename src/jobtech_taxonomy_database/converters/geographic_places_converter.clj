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


(defn ^:private fake-id
  "Temporary function until new id function with nano-ids is imported into this project.
  Accepts an integer and returns a string."
  [type id]
  (format "fake-id-%s" (format "skill-concept-%s-%d" type id)))



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


(defn convert-term
  "Convert single term."
  [term]
  (let [conv-term {:db/id  (make-tempid-term (get term :term) "502")
                   :term/base-form (get term :term)}]
    conv-term))


(defn convert-region
  "Convert a single region"
  [country region-id]
  (let [region-conv (map (fn [region]
                          (convert-term region))
                        (fetch-data get-eu-region { :id region-id }))]

    (concat region-conv
            (list {:relation/concept-1     (make-tempid-concept "country" (get country :country_id))
                   :relation/concept-2     (make-tempid-concept "region" region-id)
                   :relation/type          :country-to-region}
                  {:db/id                  (make-tempid-concept "region" region-id)
                   :concept/id             (fake-id "region" region-id)
                   :concept/description    (format "region %s" (fake-id "region" region-id))
                   :concept/preferred-term (get (first region-conv) :db/id) ;;FIXME
                   :concept/alternative-terms (get (first region-conv) :db/id) ;;FIXME
                   })
            )))


(defn convert-country
  ""
  [continent country]
  (let [regions-conv (mapcat (fn [id]
                             (convert-region country (get id :region_id)))
                           (fetch-data get-eu-region { :id (get country :country_id) }))
        language-id "502"]
    (concat regions-conv
            (list {:relation/concept-1     (make-tempid-concept "continent" (get continent :continent_id))
                   :relation/concept-2     (make-tempid-concept "country" (get country :country_id))
                   :relation/type          :continent-to-country}
                  {:db/id                  (make-tempid-concept "country" (get country :country_id))
                   :concept/id             (fake-id "country" (get country :country_id))
                   :concept/description    (get country :country_term)
                   :concept/preferred-term (make-tempid-term (get country :country_term) language-id)}
                  {:db/id  (make-tempid-term (get country :country_term) language-id)
                   :term/base-form (get country :country_term)})
            )))


(defn convert-continents
  ""
  [continent]
  (let [country-conv (mapcat (fn [headline]
                                 (convert-country continent headline))
                               (fetch-data get-country-for-continent {:id (get continent :continent_id)})
                               )]
    (concat country-conv
            (let* [category-67 :country
                   id-67 (keyword (str (get continent :continent_id)))           ;ska matcha legacyAmsTaxonomyId i json
                   description-67 (get continent :continent_term)                ;ska matcha preferredTerm i json
                   nano-id (get-nano category-67 id-67 description-67)
                   language-id "502"]
              (list {:db/id                     (make-tempid-concept "continent" (get continent :continent_id))
                     :concept/id                nano-id
                     :concept/description     (get continent :continent_term)
                     :concept/preferred-term    (make-tempid-term (get continent :continent_term) (language-id))}; TODO should '502' be string?
                    {:db/id                  (make-tempid-term (get continent :main_term) language-id)
                     :term/base-form            (get continent :main_term)})))))

(defn convert
  ""
  []
  (distinct                                  ;do this only once per value
    (mapcat (fn [continent]                  ;send each continent to fn and then add result to new collection
              (convert-continents continent))  ;this is what fn does
            (fetch-data get-continent))))    ;this returns a collection with continents