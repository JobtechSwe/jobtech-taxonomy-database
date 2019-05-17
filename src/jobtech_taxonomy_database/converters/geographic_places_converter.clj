(ns jobtech-taxonomy-database.converters.geographic-places-converter
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as legacy-migration]
            [jobtech-taxonomy-database.converters.nano-id-assigner :as nano-id-assigner]
            [jobtech-taxonomy-database.converters.converter-util :as u]
            [jobtech-taxonomy-database.types :as t]))
#_
(defn ^:private make-tempid-concept
  "Create temporary ID for transaction purpose"
  [category id]
  (str "temp-id-" category "-" id))
#_
(defn convert-relation
  "Create Datomic schema relationship structure"
  [concept-1 concept-2 relationship-type]
   {:relation/concept-1     concept-1
    :relation/concept-2     concept-2
    :relation/type          (keyword relationship-type)})
#_
(defn convert-term
  "Create Datomic schema term structure"
  [temp-id term]
  {:db/id                     temp-id
   :term/base-form             term})
#_
(defn convert-concept
  "Create Datomic schema concept structure"
  [temp-id nano-id term category legacy-id code]
   (merge
     {:concept/id                                   nano-id
      :concept/description                          term
      :concept/preferred-term                       temp-id
      :concept/category                             (keyword category)
      :concept.external-database.ams-taxonomy-67/id legacy-id}
     (when
       (and (not= code nil) (= category "region"))
       {:concept.external-standard/nuts-level-3-code code})
     (when
       (and (not= code nil) (= category "country"))
       {:concept.external-standard/country-code code})))

#_
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
                  (nano-id-assigner/get-nano category legacy-id))
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
                             (str parent-category "-to-" category)))]
      (if converted-relation [converted-concept converted-term converted-relation]
                             [converted-concept converted-term])))

(defn converter-continents
  "Convert continents"
  [{:keys [term id]}]
  {:pre [term id]}
  (let
    [concept (u/create-concept
               t/continent
               term
               term
               id)
     concept-term (u/create-term (:concept/id concept) term)]
    [concept concept-term]
    ))

(defn converter-countries
  "Convert countries"
  [{:keys [term id code parent-id]}]
  {:pre [term id code parent-id]}
  (let [concept (u/create-concept
                  t/country
                  term
                  term
                  id)
        concept-with-extras (assoc concept :concept.external-standard/country-code code)
        concept-term (u/create-term-from-concept concept-with-extras)
        temp-id-parent (u/create-temp-id t/continent parent-id)
        relation (u/create-broader-relation-to-concept concept-with-extras temp-id-parent)]
    [concept-with-extras
     concept-term
     relation]
    ))

(defn converter-regions
  "Convert EU regions (NUTS level 3)"
  [{:keys [term id code parent-id]}]
  {:pre [term id parent-id]}
  (let [concept (u/create-concept
                  t/region
                  term
                  term
                  id)
        concept-with-extras (conj concept (when code [:concept.external-standard/nuts-level-3-code code]))
        concept-term (u/create-term-from-concept concept-with-extras)
        temp-id-parent (u/create-temp-id t/country parent-id)
        relation (u/create-broader-relation-to-concept concept-with-extras temp-id-parent)]
    [concept-with-extras
     concept-term
     relation]
    ))

(defn converter-municipalities
  "Convert municipalities"
  [{:keys [term id parent-id]}]
  {:pre [term id parent-id]}
  (let [concept (u/create-concept
                  t/municipality
                  term
                  term
                  id)
        concept-term (u/create-term-from-concept concept)
        temp-id-parent (u/create-temp-id t/region parent-id)
        relation (u/create-broader-relation-to-concept concept temp-id-parent)]
    [concept
     concept-term
     relation]
    ))

#_
(defn convert-continent
  "Query db for continents, convert each entity"
  []
  (mapcat (fn [x] (converter x "continent" nil))
          (legacy-migration/fetch-data legacy-migration/get-continents)))
#_
(defn convert-country
  "Query db for countries, convert each entity"
  []
  (mapcat (fn [x] (converter x "country" "continent"))
          (legacy-migration/fetch-data legacy-migration/get-countries)))
#_
(defn convert-region
  "Query db for EU regions, convert each entity"
  []
  (mapcat (fn [x] (converter x "region" "country"))
          (legacy-migration/fetch-data legacy-migration/get-EU-regions)))
#_
(defn convert-municipality
  "Query db for municipalities, convert each entity"
  []
  (mapcat (fn [x] (converter x "municipality" "region"))
          (legacy-migration/fetch-data legacy-migration/get-municipalities)))
#_
(defn convert
  "Compile converted continents, countries, EU regions and municipalities"
  []
  (concat (convert-continent) (convert-country) (convert-region) (convert-municipality)))

(defn convert
  ""
  []
  (concat
  (mapcat converter-continents (legacy-migration/fetch-data legacy-migration/get-continents))
  (mapcat converter-countries (legacy-migration/fetch-data legacy-migration/get-countries))
  (mapcat converter-regions (legacy-migration/fetch-data legacy-migration/get-EU-regions))
  (mapcat converter-municipalities (legacy-migration/fetch-data legacy-migration/get-municipalities))))

;(def test-var '({:parent-id 59, :id 1, :term "Pirkanmaa", :code "FI197"}
 ;               {:parent-id 59, :id 3, :term "Etela-Savo"}))
