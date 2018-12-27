(ns jobtech-taxonomy-database.skills-converter-test
  (:require [clojure.test :refer :all]
            [clojure.data :refer :all]
            [clojure.pprint :as pp]
            [jobtech-taxonomy-database.skills-converter :refer :all]))

(deftest skill-converter-test
  "For a successfull test, we check the diff result between expected
  and actual output, and expect 'only-in-a' and 'only-in-b' to be
  nil."
  (let* [input '({:headlines
                 ({:modificationdate_4 #inst "2007-11-20T13:51:00.000000000-00:00",
                  :skillmainheadlineid 2,
                  :term_2 "Datakunskaper",
                  :skillheadlineid_2 1128,
                  :skillmainheadlineid_2 2,
                  :languageid_2 502,
                  :modificationdate_3 #inst "2007-11-20T13:51:00.000000000-00:00",
                  :term "Databaser",
                  :modificationdate_2 #inst "2006-09-15T06:43:49.993000000-00:00",
                  :modificationdate #inst "2006-09-15T06:43:49.993000000-00:00",
                  :skillheadlineid 1128,
                  :skillmainheadlineid_3 2,
                  :languageid 502}),
                :main-headline
                {:skillmainheadlineid 2,
                 :languageid 502,
                 :term "Datakunskaper",
                 :modificationdate #inst "2007-11-20T13:51:00.000000000-00:00"}}
               {:headlines
                ({:modificationdate_4 #inst "2006-09-15T06:41:23.813000000-00:00",
                  :skillmainheadlineid 4,
                  :term_2 "Övriga kompetensrubriker",
                  :skillheadlineid_2 1353,
                  :skillmainheadlineid_2 4,
                  :languageid_2 502,
                  :modificationdate_3 #inst "2006-11-16T07:41:24.000000000-00:00",
                  :term "Kvalitetssystem",
                  :modificationdate_2
                  #inst "2006-09-15T06:43:49.993000000-00:00",
                  :modificationdate #inst "2006-09-15T06:43:49.993000000-00:00",
                  :skillheadlineid 1353,
                  :skillmainheadlineid_3 4,
                  :languageid 502}),
                :main-headline
                {:skillmainheadlineid 4,
                 :languageid 502,
                 :term "Övriga kompetensrubriker",
                 :modificationdate #inst "2006-09-15T06:41:23.813000000-00:00"}})
        expected-output '[#:concept{:id "0000000002",
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
         output (skill-converter input)
         diff   (diff expected-output output)]

    (testing "skill-converter"
      (is (and (= (first diff) nil)
               (= (second diff) nil))))))
