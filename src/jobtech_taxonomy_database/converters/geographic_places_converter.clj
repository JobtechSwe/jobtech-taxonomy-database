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


;TODO Continue with relation-converter.
(defn relation-converter
  ""
  [concept-1 concept-2 relationship-type]
  [
   {:relation/concept-1     concept-1
    :relation/concept-2     concept-2
    :relation/type          relationship-type}
   ]
  )


(defn region-converter
  ""
  [nano-id-region term-67-region category-67-region id-67-region nuts]
  [
   (merge
     {:concept/description      term-67-region
      :concept/preferred-term   nano-id-region
      :concept/category         category-67-region
      :concept.taxonomy-67-id   id-67-region
      :concept/id               nano-id-region}
     (when
       (not= nuts nil)
       {:region-nuts-code-level-3 nuts})
     )
   {:db/id                     nano-id-region
    :term/base-form             term-67-region}
   ]
  )

(defn country-converter
  ""
  [nano-id-country term-67-country category-67-country id-67-country country-code]
  [
   {:concept/id                nano-id-country
    :concept/description       term-67-country
    :concept/preferred-term    nano-id-country
    :concept/category          category-67-country
    :concept.taxonomy-67-id    id-67-country
    :concept.external-standard/country-code country-code}
   {:db/id                     nano-id-country
    :term/base-form            term-67-country}
   ]
  )

(defn continent-converter
  ""
  [nano-id-continent term-67-continent category-67-continent id-67-continent]
  [
   {:concept/id                nano-id-continent
    :concept/description       term-67-continent
    :concept/preferred-term    nano-id-continent
    :concept/category          category-67-continent
    :concept.taxonomy-67-id    id-67-continent}
   {:db/id                     nano-id-continent
    :term/base-form            term-67-continent}
   ]
  )

(defn converter
  "Immutable language converter."
  [data]
  (let [
        category-67-continent :continent                        ;json-nyckeln
        category-67-country :country
        category-67-region :eu-region
        id-67-continent (keyword (str (:continent-id data)))    ;ska matcha legacyAmsTaxonomyId i json
        id-67-country (if
                        (not= (:country-id data) nil)
                        (keyword (str (:country-id data))))
        id-67-region (if
                       (not= (:region-eu-id data) nil)
                        (keyword (str (:region-eu-id data))))
        term-67-continent (:continent-term data)
        term-67-country (:country-term data)
        term-67-region (:region-eu-term data)                   ;ska matcha preferredTerm i json
        nano-id-continent (if
                            (not= id-67-continent nil)
                            (get-nano category-67-continent id-67-continent term-67-continent))
        nano-id-country (if
                          (not= id-67-country nil)
                          (get-nano category-67-country id-67-country term-67-country))
        nano-id-region (if
                         (not= id-67-region nil)
                         (get-nano category-67-region id-67-region term-67-region))
        nuts-code (if
                    (not= (:region-nuts-code-level-3 data) nil)
                    (keyword (str (:region-nuts-code-level-3 data))))
        country-code (keyword (:country-code data))
        ]
    (concat
      (if id-67-continent (continent-converter nano-id-continent term-67-continent category-67-continent id-67-continent))
      (if id-67-country (country-converter nano-id-country term-67-country category-67-country id-67-country country-code))
      (if id-67-region (region-converter nano-id-region term-67-region category-67-region id-67-region nuts-code))
      )
    )
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

;TODO Continue with "fetch-all ..."
(defn convert
  ""
  []
  (converter sql-answer-with-nuts))
