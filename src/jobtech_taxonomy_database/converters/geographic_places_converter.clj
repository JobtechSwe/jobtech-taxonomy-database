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

(defn ^:private make-tempid-concept
  "Create temporary ID for transaction purpose"
  [category id]
  (str "temp-id-" category "-" id))

(defn convert-relation
  "Create Datomic schema relationship structure"
  [concept-1 concept-2 relationship-type]
   {:relation/concept-1     concept-1
    :relation/concept-2     concept-2
    :relation/type          (keyword relationship-type)})

(defn convert-term
  "Create Datomic schema term structure"
  [temp-id term]
  {:db/id                     temp-id
   :term/base-form             term})

(defn convert-concept
  "Create Datomic schema concept structure"
  [temp-id nano-id term category legacy-id code]
   (merge
     {:concept/id               nano-id
      :concept/description      term
      :concept/preferred-term   temp-id
      :concept/category         (keyword category)
      :concept.taxonomy-67-id   legacy-id}
     (when
       (and (not= code nil) (= category "region"))
       {:concept.external-standard/nuts-level-3-code code})
     (when
       (and (not= code nil) (= category "country"))
       {:concept.external-standard/country-code code})))


(defn converter
  "Converter for continents, countries, EU regions and municipalities.
  Creates relationship between entities in adjacent taxonomy ranks"
  [data category parent-category]
  (let [legacy-id (str (:id data))
        term (str (:term data))
        code (if (:code data)
               (str (:code data))
               nil)
        nano-id (if (not= legacy-id nil)
                  (get-nano category legacy-id))
        temp-id (make-tempid-concept category legacy-id)
        temp-id-parent (if (not= (:parent-id data) nil)
                         (make-tempid-concept parent-category (:parent-id data)))
        converted-concept (convert-concept
                              temp-id
                              nano-id
                              term
                              category
                              legacy-id
                              code)
        converted-term (convert-term
                         temp-id
                         term)
        converted-relation (when (not= category "continent")
                             (convert-relation
                             temp-id-parent
                             temp-id
                             (str parent-category " to " category)))]
      (if converted-relation [converted-concept converted-term converted-relation]
                             [converted-concept converted-term])))

(defn convert-continent
  "Query db for continents, convert each entity"
  []
  (mapcat (fn [x] (converter x "continent" nil)) (fetch-data get-continents)))

(defn convert-country
  "Query db for countries, convert each entity"
  []
  (mapcat (fn [x] (converter x "country" "continent")) (fetch-data get-countries)))

(defn convert-region
  "Query db for EU regions, convert each entity"
  []
  (mapcat (fn [x] (converter x "region" "country")) (fetch-data get-EU-regions)))

(defn convert-municipality
  "Query db for municipalities, convert each entity"
  []
  (mapcat (fn [x] (converter x "municipality" "region")) (fetch-data get-municipalities)))

(defn convert
  "Compile converted continents, countries, EU regions and municipalities"
  []
  (concat (convert-continent) (convert-country) (convert-region) (convert-municipality)))
