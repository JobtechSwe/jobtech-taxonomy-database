(ns jobtech-taxonomy-database.schema)

(def concept-schema
  [{:db/ident       :concept/id
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/unique      :db.unique/identity
    :db/doc         "Unique identifier for concepts"}

   {:db/ident       :concept/description
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         "Text describing the concept, is used for disambiguation."}

   {:db/ident       :concept/preferred-term
    :db/valueType   :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/doc         "What we prefer to call the concept"}

   {:db/ident       :concept/alternative-terms
    :db/cardinality :db.cardinality/many
    :db/valueType   :db.type/ref
    :db/doc         "All terms referring to this concept"}

   {:db/ident       :concept/type
    :db/valueType   :db.type/keyword
    :db/cardinality :db.cardinality/one
    :db/doc         "JobTech types" ;TODO Explain more. What is this? /Sara
    }])

;; Example:
;;  (def some-concepts
;;    [
;;     {
;;      :concept/id                "MZ6wMoAfyP"
;;      :concept/description       "Kontaktmannaskap är ett sätt att organisera arbetet för kontinuitet. Kontaktmannen har till uppgift att kommunicera, informera och bidra till sociala aktiviteter samt fungera som en kontakt och länk till anhöriga. Inom omsorg är kontinuitet och kunskap oerhört viktigt för att skapa trygghet."
;;      :concept/preferred-term    [:term/base-form "KontaktmannaskapX"]
;;      :concept/alternative-terms #{[:term/base-form "Kontaktmannaskap"]}
;;      }
;;     ]
;;    )
;;  (d/transact conn {:tx-data some-concepts})


(def concept-schema-extras
  [{:db/ident       :concept.external-standard/ssyk-2012
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/unique      :db.unique/identity
    :db/doc         "SSYK-2012 type"}])

;; TODO add Transaction Function that checks that the term is connected to a concept


(def term-schema
  [{:db/ident       :term/base-form
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/unique      :db.unique/identity
    :db/doc         "Term value, the actual text string that is referring to concepts"}])

;; Example:
;;  (def some-terms
;;    [{:term/base-form "Kontaktmannaskap"}])
;;  (d/transact conn {:tx-data some-terms})


(def concept-relation-schema
  [{:db/ident       :relation/concept-1
    :db/valueType   :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/doc         "the first concept in a relation"}

   {:db/ident       :relation/concept-2
    :db/valueType   :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/doc         "the second concept in a relation"}

   {:db/ident       :relation/type
    :db/valueType   :db.type/keyword
    :db/cardinality :db.cardinality/one
    :db/doc         "the type of relationship"}

   {:db/ident       :relation/description
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         "Text describing the relation."}])
