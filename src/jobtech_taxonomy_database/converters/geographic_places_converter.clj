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
  ""
  [category id]
  (str "temp-id-" category "-" id))


;(TODO Continue with relation-converter.)
(defn convert-relation
  ""
  [concept-1 concept-2 relationship-type]
  [
   {:relation/concept-1     concept-1
    :relation/concept-2     concept-2
    :relation/type          relationship-type}
   ]
  )


(defn convert-concept
  ""
  [temp-id nano-id term category legacy-id code]
  [
   (merge
     {:concept/id               nano-id
      :concept/description      term
      :concept/preferred-term   temp-id
      :concept/category         category
      :concept.taxonomy-67-id   legacy-id}
     (when
       (and (not= code nil) (= category "region"))
       {:concept.external-standard/nuts-level-3-code code})
     (when
       (and (not= code nil) (= category "country"))
          {:concept.external-standard/country-code code}
     ))
   {:db/id                     temp-id
    :term/base-form             term}
   ]
  )


(defn converter
  "(Immutable language converter.)"
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
                         (make-tempid-concept parent-category (:parent-id data)))]
    (list
      (convert-concept
      temp-id
      nano-id
      term
      category
      legacy-id
      code)
    (convert-relation
      temp-id-parent
      temp-id
      (str parent-category " to " category)))))



(def sql-continents
  "continent"
  '({:id 0, :term "Hela världen"}
    {:id 2, :term "Europa/EU/EES/EURES"}
    {:id 3, :term "Sydamerika"}
    {:id 6, :term "Nordamerika"}
    {:id 8, :term "Antarktis"}))

(defn convert-continent
  ""
  []
  (mapcat (fn [x] (converter x "continent" nil)) sql-continents))

(def sql-countries
  "country"
  '({:parent-id 20, :id 1, :term "Afghanistan", :code "AF"}
     {:parent-id 9, :id 2, :term "Albanien", :code "AL"}
     {:parent-id 12, :id 3, :term "Algeriet", :code "DZ"}
     {:parent-id 26, :id 4, :term "Amerikanska Samoa", :code "AS"}
     {:parent-id 3, :id 5, :term "Amerikanska, mindre uteliggande öar", :code "UM"}))

(defn convert-country
  ""
  []
  (mapcat (fn [x] (converter x "country" "continent")) sql-countries))

(def sql-eu-regions
  "region"
  '({:parent-id 59, :id 1, :term "Pirkanmaa", :code "FI197"}
    {:parent-id 59, :id 3, :term "Etela-Savo", :code "FI1D1"}
    {:parent-id 59, :id 4, :term "Pohjois-Savo", :code "FI1D2"}
    {:parent-id 59, :id 5, :term "Pohjois-Karjala", :code "FI1D3"}
    {:parent-id 59, :id 6, :term "Keski-Suomi", :code "FI193"}))

(defn convert-region
  ""
  []
  (mapcat (fn [x] (converter x "region" "country")) sql-eu-regions))

(def sql-municipalities
  "municipality"
  '({:parent-id 197, :id 1, :term "Upplands Väsby"}
    {:parent-id 197, :id 2, :term "Vallentuna"}
    {:parent-id 197, :id 3, :term "Österåker"}
    {:parent-id 197, :id 4, :term "Värmdö"}
    {:parent-id 197, :id 5, :term "Järfälla"}))

(defn convert-municipality
  ""
  []
  (mapcat (fn [x] (converter x "municipality" "region")) sql-municipalities))


#_
(defn convert
  ""
  []
  (mapcat converter sql-answers-four-answers))

#_(defn convert
    ""
    []
    (mapcat converter (fetch-data get-geographic-places)))



(def sql-answers-four-answers
  '({:continent-id 2,
   :continent-term "Europa/EU/EES/EURES",
   :country-id 195,
   :country-term "Förenade konungariket, Storbritannien och Nordirland",
   :country-code "GB",
   :region-eu-id 1634,
   :region-nuts-code-level-3 "UKN05",
   :region-eu-term "West and South of Northern Ireland"}
  {:continent-id 2,
   :continent-term "Europa/EU/EES/EURES",
   :country-id 155,
   :country-term "Norge",
   :country-code "NO",
   :region-eu-id 158,
   :region-nuts-code-level-3 nil,
   :region-eu-term "Akershus"}
  {:continent-id 14,
   :continent-term "Västafrika",
   :country-id 134,
   :country-term "Mauretanien",
   :country-code "MR",
   :region-eu-id nil,
   :region-nuts-code-level-3 nil,
   :region-eu-term nil}
  {:continent-id 0,
   :continent-term "Hela världen",
   :country-id nil,
   :country-term nil,
   :country-code nil,
   :region-eu-id nil,
   :region-nuts-code-level-3 nil,
   :region-eu-term nil})
  )


(def sql-answer-with-nuts
  {:continent-id 2,
   :continent-term "Europa/EU/EES/EURES",
   :country-id 195,
   :country-term "Förenade konungariket, Storbritannien och Nordirland",
   :country-code "GB",
   :region-eu-id 1634,
   :region-nuts-code-level-3 "UKN05",
   :region-eu-term "West and South of Northern Ireland"})

(def sql-answer-norway
  {:continent-id 2,
   :continent-term "Europa/EU/EES/EURES",
   :country-id 155,
   :country-term "Norge",
   :country-code "NO",
   :region-eu-id 158,
   :region-nuts-code-level-3 nil,
   :region-eu-term "Akershus"})

(def sql-answer-no-nuts
  {:continent-id 14,
   :continent-term "Västafrika",
   :country-id 134,
   :country-term "Mauretanien",
   :country-code "MR",
   :region-eu-id nil,
   :region-nuts-code-level-3 nil,
   :region-eu-term nil})

(def sql-answer-no-country
  {:continent-id 0,
   :continent-term "Hela världen",
   :country-id nil,
   :country-term nil,
   :country-code nil,
   :region-eu-id nil,
   :region-nuts-code-level-3 nil,
   :region-eu-term nil})
