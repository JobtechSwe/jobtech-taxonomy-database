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


(defn converter
  "Immutable language converter."
  [data]
  (let [category-67-continent :continent                    ;json-nyckeln
        category-67-country :country
        category-67-region :eu-region
        id-67-continent (keyword (str (:continent-id data)))   ;ska matcha legacyAmsTaxonomyId i json
        id-67-country (keyword (str (:country-id data)))
        id-67-region (keyword (str (:region-eu-id data)))
        term-67-continent (:continent-term data)
        term-67-country (:country-term data)
        term-67-region (:region-eu-term data)]                        ;ska matcha preferredTerm i json
    (let [nano-id-continent (get-nano category-67-continent id-67-continent term-67-continent)
          nano-id-country (get-nano category-67-country id-67-country term-67-country)
          nano-id-region (get-nano category-67-region id-67-region term-67-region)]
      (list
        {:concept/id                nano-id-continent
        :concept/description       term-67-continent
        :concept/preferred-term    nano-id-continent
         :concept/category         category-67-continent}
       {:db/id          nano-id-continent
        :term/base-form term-67-continent}
       {:relation/concept-1     nano-id-continent
        :relation/concept-2     nano-id-country
        :relation/type          :continent-to-country}
       {:concept/id                nano-id-country
        :concept/description       term-67-country
        :concept/preferred-term    nano-id-country
        :concept/category           category-67-country}
       {:db/id          nano-id-country
        :term/base-form term-67-country}
       {:relation/concept-1     nano-id-country
        :relation/concept-2     nano-id-region
        :relation/type          :country-to-region-eu}
       {:concept/id                nano-id-region
        :concept/description       term-67-region
        :concept/preferred-term    nano-id-region
        :concept/category           category-67-region
        :concept.external-standard/nuts-code (:region-nuts-code-level-3 data)}
       {:db/id          nano-id-region
        :term/base-form term-67-region}
        ))))

(def sql-answer-with-nuts
  {:continent-id 2,
   :continent-term "Europa/EU/EES/EURES",
   :country-id 195,
   :country-term "Förenade konungariket, Storbritannien och Nordirland",
   :region-eu-id 1634,
   :region-nuts-code-level-3 "UKN05",
   :region-eu-term "West and South of Northern Ireland"})

(def sql-answer-norway
  {:continent-id 2,
   :continent-term "Europa/EU/EES/EURES",
   :country-id 155,
   :country-term "Norge",
   :region-eu-id 158,
   :region-nuts-code-level-3 nil,
   :region-eu-term "Akershus"})

(def sql-answer-no-nuts
  {:continent-id 14,
   :continent-term "Västafrika",
   :country-id 134,
   :country-term "Mauretanien",
   :region-eu-id nil,
   :region-nuts-code-level-3 nil,
   :region-eu-term nil})

(def sql-answer-no-country
  {:continent-id 0,
   :continent-term "Hela världen",
   :country-id nil,
   :country-term nil,
   :region-eu-id nil,
   :region-nuts-code-level-3 nil,
   :region-eu-term nil})

(defn convert
  ""
  []
  (converter sql-answer-no-nuts))