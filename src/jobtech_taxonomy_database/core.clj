(ns jobtech-taxonomy-database.core
  (:gen-class)
  (:require [datomic.client.api :as d]
            [jobtech-taxonomy-database.schema :refer :all :as schema]
            [jobtech-taxonomy-database.legacy-migration :refer :all]
            [jobtech-taxonomy-database.datomic-connection :refer :all :as conn]
            [clojure.pprint :as pp]))

(def find-concept-by-preferred-term-query
  '[:find (pull ?c
                [
                 :concept/id
                 :concept/description
                 {:concept/preferred-term [:term/base-form]}
                 {:concept/referring-terms [:term/base-form]}])
    :in $ ?term
    :where [?t :term/base-form ?term]
    [?c :concept/preferred-term ?t]

    ])

;; (d/q find-concept-by-preferred-term-query (get-db) "Kontaktmannaskap")

(defn fake-id "" [id] (format "%010d" id))

(defn legacy-converter
  ""
  []
  (run! (fn [skill-main-headline]
          (let [term-mainhl (get skill-main-headline :term)
                skill-main-headline-id (get skill-main-headline :skillMainHeadlineID)
                concept-mainhl [{:concept/id                (fake-id skill-main-headline-id)
                                 :concept/description       "hrpf"
                                 :concept/preferred-term    [:term/base-form term-mainhl]
                                 :concept/alternative-terms #{[:term/base-form term-mainhl]}
                                 }]]
            (run! (fn [skill-headline]
                    (let [term-hl (get skill-headline :term)
                          skill-headline-id (get skill-headline :skillHeadlineID)
                          concept-hl [{:concept/id                (fake-id skill-headline-id)
                                       :concept/description       "zkrpkt"
                                       :concept/preferred-term    [:term/base-form term-hl]
                                       :concept/alternative-terms #{[:term/base-form term-hl]}
                                       }]
                          relation [{:relation/concept-1 [:concept/id (fake-id skill-main-headline-id)]
                                     :relation/concept-2 [:concept/id (fake-id skill-headline-id)]
                                     :relation/type      :hyponym
                                     }]]
                      (d/transact (get-conn) {:tx-data [[{:term/base-form term-mainhl}]
                                                        concept-mainhl
                                                        [{:term/base-form term-hl}]
                                                        relation
                                                        concept-hl]})))
                  (get-skillheadlines {:id skill-main-headline-id}))))
        (get-skillmainheadlines)))

(defn -main
  []
  (println "**** sucking data from old taxonomy, converting and puking it back into datomic...")
  (legacy-converter)
  (println "**** query datomic to see some results...")
  (clojure.pprint/pprint (d/q '[:find ?x :where [_ :term/base-form ?x]] (get-db)))
  (d/q '[:find ?x :where [_ :concept/id ?x]] (get-db))
  (d/q '[:find ?x :where [_ :relation/concept-1 ?x]] (get-db)))
