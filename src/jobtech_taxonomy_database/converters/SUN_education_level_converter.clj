(ns jobtech-taxonomy-database.converters.SUN-education-level-converter
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
  {:concept/id                                   nano-id
   :concept/description                          term
   :concept/preferred-term                       temp-id
   :concept/category                             (keyword category)
   :concept.external-database.ams-taxonomy-67/id legacy-id
   :concept.external-standard/SUN-level-code     code})


(defn converter
  "Converter for SUN education levels 1, 2 & 3.
  Creates relationship between entities in adjacent taxonomy ranks"
  [data category parent-category]
  ;TODO Check below
  (let [legacy-id (str (:id data))
        term (str (:term data))
        code (if (:code data) (str (:code data))
                              nil)
        nano-id (if (not= legacy-id nil) (get-nano category legacy-id))
        temp-id (make-tempid-concept category legacy-id)
        temp-id-parent (if (not= (:parent-id data) nil) (make-tempid-concept parent-category (:parent-id data)))
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
        converted-relation (when (not= category "sun-education-level-1")
                             (convert-relation
                               temp-id-parent
                               temp-id
                               (str parent-category "-to-" category)))]
    (if converted-relation [converted-concept converted-term converted-relation]
                           [converted-concept converted-term])))

(defn convert-level-1
  "Query db for SUN education level 1, convert each entity"
  []
  (mapcat (fn [x] (converter x "sun-education-level-1" nil)) (fetch-data get-sun-level-1)))

(defn convert-level-2
  "Query db for SUN education level 2, convert each entity"
  []
  (mapcat (fn [x] (converter x "sun-education-level-2" "sun-education-level-1")) (fetch-data get-sun-level-2)))

(defn convert-level-3
  "Query db for SUN education level 3, convert each entity"
  []
  (mapcat (fn [x] (converter x "sun-education-level-3" "sun-education-level-2")) (fetch-data get-sun-level-3)))

(defn convert
  "Compile converted SUN education levels 1, 2 & 3"
  []
  (concat (convert-level-1) (convert-level-2) (convert-level-3)))
