; THIS CODE SHOULDN'T BE USED! 
; Editorial team has instructued us not to convert SUN at the moment (May 2019). 
; A new version of SUN is released during spring 2019. 
; The new version will be edited by editorial team and ready to be written to Datomic in September at the latest.

(ns jobtech-taxonomy-database.converters.SUN-education-field-converter
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as legacy-migration]
            [jobtech-taxonomy-database.converters.nano-id-assigner :as nano-id-assigner]))

(defn ^:private make-tempid-concept
  "Create temporary ID for transaction purpose"
  [category id]
  (str "temp-id-" category "-" id))

(defn convert-relation
  "Create Datomic schema relationship structure"
  [concept-1 concept-2 relationship-type]
  {:relation/concept-1 concept-1
   :relation/concept-2 concept-2
   :relation/type      (keyword relationship-type)})

(defn convert-term
  "Create Datomic schema term structure"
  [temp-id term]
  {:db/id          temp-id
   :term/base-form term})

(defn convert-concept
  "Create Datomic schema concept structure"
  [temp-id nano-id term category legacy-id code]
  {:concept/id                                   nano-id
   :concept/description                          term
   :concept/preferred-term                       temp-id
   :concept/category                             (keyword category)
   :concept.external-database.ams-taxonomy-67/id legacy-id
   :concept.external-standard/SUN-field-code     code})

(defn converter
  "Converter for SUN education fields 1, 2 & 3.
  Creates relationship between entities in adjacent taxonomy ranks"
  [data category parent-category]
  ;TODO Check below
  (let [legacy-id (str (:id data))
        term (str (:term data))
        code (if (:code data) (str (:code data))
                 nil)
        nano-id (if (not= legacy-id nil) (nano-id-assigner/get-nano category legacy-id))
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
        converted-relation (when (not= category "sun-education-field-1")
                             (convert-relation
                              temp-id-parent
                              temp-id
                              (str parent-category "-to-" category)))]
    (if converted-relation [converted-concept converted-term converted-relation]
        [converted-concept converted-term])))

(defn convert-field-1
  "Query db for SUN education field 1, convert each entity"
  []
  (mapcat (fn [x] (converter x "sun-education-field-1" nil))
          (legacy-migration/fetch-data legacy-migration/get-sun-field-1)))

(defn convert-field-2
  "Query db for SUN education field 2, convert each entity"
  []
  (mapcat (fn [x] (converter x "sun-education-field-2" "sun-education-field-1"))
          (legacy-migration/fetch-data legacy-migration/get-sun-field-2)))

(defn convert-field-3
  "Query db for SUN education field 3, convert each entity"
  []
  (mapcat (fn [x] (converter x "sun-education-field-3" "sun-education-field-2"))
          (legacy-migration/fetch-data legacy-migration/get-sun-field-3)))

(defn convert
  "Compile converted SUN education fields 1, 2 & 3"
  []
  (concat (convert-field-1) (convert-field-2) (convert-field-3)))

