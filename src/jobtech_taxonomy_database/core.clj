(ns jobtech-taxonomy-database.core
  (:gen-class)
  (:require [datomic.client.api :as d]
            [jobtech-taxonomy-database.legacy-migration])
  )
(use 'jobtech-taxonomy-database.legacy-migration)



(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))


(def cfg {:server-type :peer-server
          :access-key "myaccesskey"
          :secret "mysecret"
          :endpoint "localhost:8998"})

(def client (d/client cfg))

;; TODO change name of the database to jobtech-taxonomy
(def conn (d/connect client {:db-name "taxonomy_v13"}))



(defn get-db [] (d/db conn))



(def concept-schema
  [
   {
    :db/ident           :concept/id
    :db/valueType       :db.type/string
    :db/cardinality     :db.cardinality/one
    :db/unique          :db.unique/identity
    :db/doc             "Unique identifier for concepts"
   }

   {
    :db/ident           :concept/description
    :db/valueType       :db.type/string
    :db/cardinality     :db.cardinality/one
    :db/unique          :db.unique/identity
    :db/doc             "Text describing the concept, is used for disambiguation."
   }

   {
    :db/ident           :concept/preferred-term
    :db/valueType       :db.type/ref
    :db/cardinality     :db.cardinality/one
    :db/doc             "What we prefer to call the concept"
    }

   {
    :db/ident           :concept/alternative-terms
    :db/cardinality     :db.cardinality/many
    :db/valueType       :db.type/ref
    :db/doc             "All terms referring to this concept"
    }

   {
    :db/ident           :concept/type
    :db/valueType       :db.type/keyword
    :db/cardinality     :db.cardinality/one
    :db/doc             "JobTech types"
   }

   ])


(def concept-schema-extras
  [
   {
    :db/ident           :concept.external-standard/ssyk-2012
    :db/valueType       :db.type/string
    :db/cardinality     :db.cardinality/one
    :db/unique          :db.unique/identity
    :db/doc             "SSYK-2012 type"
   }
])


;; TODO add Transaction Function that checks that the term is connected to a concept

(def term-schema
  [
   {
    :db/ident           :term/base-form
    :db/valueType       :db.type/string
    :db/cardinality     :db.cardinality/one
    :db/unique          :db.unique/identity
    :db/doc             "Term value, the actual text string that is refering to concepts"
    }]
  )




(def concept-relation-schema
  [
   {
    :db/ident           :relation/concept-1
    :db/valueType       :db.type/ref
    :db/cardinality     :db.cardinality/one
    :db/doc             "the first concept in a relation"
    }

   {
    :db/ident           :relation/concept-2
    :db/valueType       :db.type/ref
    :db/cardinality     :db.cardinality/one
    :db/doc             "the second concept in a relation"
    }

   {
    :db/ident           :relation/type
    :db/valueType       :db.type/keyword
    :db/cardinality     :db.cardinality/one
    :db/doc             "the type of relationship"
    }
   ]
  )



;;  (d/transact conn {:tx-data term-schema})
;;  (d/transact conn {:tx-data concept-schema})
;;  (d/transact conn {:tx-data concept-schema-extras})
;; (d/transact conn {:tx-data concept-relation-schema})


(def some-terms
  [{:term/base-form "Kontaktmannaskap"}])

(def some-concepts
  [
   {
    :concept/id "MZ6wMoAfyP"
    :concept/description "Kontaktmannaskap är ett sätt att organisera arbetet för kontinuitet. Kontaktmannen har till uppgift att kommunicera, informera och bidra till sociala aktiviteter samt fungera som en kontakt och länk till anhöriga. Inom omsorg är kontinuitet och kunskap oerhört viktigt för att skapa trygghet."
    :concept/preferred-term [:term/base-form "KontaktmannaskapX" ]
    :concept/alternative-terms #{[:term/base-form "Kontaktmannaskap"]}
    }
   ]
)


 ;; (d/transact conn {:tx-data some-terms})
 ;; (d/transact conn {:tx-data some-concepts})

(def find-concept-by-preferred-term-query '[:find (pull ?c
                                                        [
                                                         :concept/id
                                                         :concept/description
                                                         {:concept/preferred-term [:term/base-form]}
                                                         {:concept/referring-terms [:term/base-form]}])
                                            :in $ ?term
                                            :where [?t :term/base-form ?term]
                                            [?c :concept/preferred-term ?t]

                                            ])

;; (d/q find-concept-by-preferred-term-query (get-db)  "Kontaktmannaskap")

(defn fake-id "" [id] (format "%010d" id))


(defn legacy-converter
  ""
  []
  (map (fn [skill-main-headline]
         (let [term-mainhl (get skill-main-headline :term)]
           (d/transact conn {:tx-data [{:term/base-form term-mainhl}]})
           (let [skill-main-headline-id (get skill-main-headline :skillMainHeadlineID)
                concept-mainhl [ {:concept/id (fake-id skill-main-headline-id)
                                  :concept/description "hrpf"
                                  :concept/preferred-term [:term/base-form term-mainhl ]
                                  :concept/alternative-terms #{[:term/base-form term-mainhl]}
                                  }]]
            (list :term-mainhl term-mainhl :term-hl
                  (map (fn [skill-headline]
                         (let [term-hl (get skill-headline :term)]
                           (d/transact conn {:tx-data [{:term/base-form term-hl}]})
                           (let [skill-headline-id (get skill-headline :skillHeadlineID)
                                 concept-hl [ {:concept/id (fake-id skill-headline-id)
                                               :concept/description "zkrpkt"
                                               :concept/preferred-term [:term/base-form term-hl ]
                                               :concept/alternative-terms #{[:term/base-form term-hl]}
                                               }]]

                             (let [relation [ {:relation/concept-1 [:concept/id (fake-id skill-main-headline-id) ]
                                               :relation/concept-2 [:concept/id (fake-id skill-headline-id) ]
                                               :relation/type :hyponym
                                               }]]

                               (d/transact conn {:tx-data [
                                                           [ {:term/base-form term-mainhl} ]
                                                           concept-mainhl
                                                           [{:term/base-form term-hl}]
                                                           relation
                                                           concept-hl
                                                           ]})

                             term-hl)))
                       (get-skillheadlines skill-main-headline-id))))))
       (get-skillmainheadlines)))




;; (legacy-converter)
;; (d/q '[:find ?x :where [_ :term/base-form ?x]] (get-db))
;; (d/q '[:find ?x :where [_ :concept/id ?x]] (get-db))
;; (d/q '[:find ?x :where [_ :relation/concept-1 ?x]] (get-db))
