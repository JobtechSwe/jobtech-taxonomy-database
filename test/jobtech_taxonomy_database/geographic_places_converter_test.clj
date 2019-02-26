(ns jobtech-taxonomy-database.geographic-places-converter-test
  (:require [clojure.test :refer :all]
            [clojure.data :refer :all]
            [clojure.pprint :as pp]
            [jobtech-taxonomy-database.geographic-places-converter-test :refer :all]))

(deftest geographic-places-converter-test
  "For a successfull test, we check the diff result between expected
  and actual output, and expect 'only-in-a' and 'only-in-b' to be
  nil."
  (let* [input '({:answer-with-nuts
                  (
                    {:continent-id             2,
                     :continent-term           "Europa/EU/EES/EURES",
                     :country-id               195,
                     :country-term             "Förenade konungariket, Storbritannien och Nordirland",
                     :region-eu-id             1634,
                     :region-nuts-code-level-3 "UKN05",
                     :region-eu-term           "West and South of Northern Ireland"}
                    )},
                  {:answer-no-country
                   (
                     {:continent-id             0,
                      :continent-term           "Hela världen",
                      :country-id               nil,
                      :country-term             nil,
                      :region-eu-id             nil,
                      :region-nuts-code-level-3 nil,
                      :region-eu-term           nil}
                     )},
                  {:answer-norway
                   (
                     {:continent-id             2,
                      :continent-term           "Europa/EU/EES/EURES",
                      :country-id               155,
                      :country-term             "Norge",
                      :region-eu-id             158,
                      :region-nuts-code-level-3 nil,
                      :region-eu-term           "Akershus"}
                     )},
                  {:answer-no-nuts
                   (
                     {:continent-id             14,
                      :continent-term           "Västafrika",
                      :country-id               134,
                      :country-term             "Mauretanien",
                      :region-eu-id             nil,
                      :region-nuts-code-level-3 nil,
                      :region-eu-term           nil}
                     )}
                  )
         expected-output '[#:concept{:id "0000000002", ;TODO change expected output!
                                     :description "hrpf",
                                     :preferred-term [:term/base-form "Datakunskaper"],
                                     :alternative-terms #{[:term/base-form "Datakunskaper"]}}
                           #:term{:base-form "Datakunskaper"}
                           #:term{:base-form "Databaser"}
                           #:concept{:id "0000001128",
                                     :description "zkrpkt",
                                     :preferred-term [:term/base-form "Databaser"],
                                     :alternative-terms #{[:term/base-form "Databaser"]}}
                           #:relation{:concept-1 [:concept/id "0000000002"],
                                      :concept-2 [:concept/id "0000001128"],
                                      :type :hyponym}
                           #:concept{:id "0000000004",
                                     :description "hrpf",
                                     :preferred-term
                                     [:term/base-form "Övriga kompetensrubriker"],
                                     :alternative-terms
                                     #{[:term/base-form "Övriga kompetensrubriker"]}}
                           #:term{:base-form "Övriga kompetensrubriker"}
                           #:term{:base-form "Kvalitetssystem"}
                           #:concept{:id "0000001353",
                                     :description "zkrpkt",
                                     :preferred-term [:term/base-form "Kvalitetssystem"],

                                     :alternative-terms #{[:term/base-form "Kvalitetssystem"]}}
                           #:relation{:concept-1 [:concept/id "0000000004"],
                                      :concept-2 [:concept/id "0000001353"],
                                      :type :hyponym}]
         output (converter input)
         diff   (diff expected-output output)]

    (testing "geographic-places-converter"
      (is (and (= (first diff) nil)
               (= (second diff) nil))))))
