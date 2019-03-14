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

   {:db/ident       :concept/category
    :db/valueType   :db.type/keyword
    :db/cardinality :db.cardinality/one
    :db/doc         "JobTech categories" ;
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
    :db/doc         "SSYK-2012 type"}

   {:db/ident       :concept.category/sort-order
    :db/valueType   :db.type/long
    :db/cardinality :db.cardinality/one
    :db/doc         "Value for display sort order in category"}

   {:db/ident       :concept.external-standard/eures-code
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         "EURES code"}

   {:db/ident       :concept.external-standard/drivers-licence-code
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         "Driver's licence code"}

   {:db/ident       :concept.external-standard/nuts-level-3-code
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         "NUTS level 3 code"}

   {:db/ident       :concept.external-standard/country-code
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         "Country code"}

   {:db/ident       :concept.taxonomy-67-id
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         "ID from legacy Taxonomy version 67"}

   {:db/ident       :concept.external-standard/isco-08
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         "ISCO-08 level 4"}

    {:db/ident       :concept.external-standard/SUN-field-code
     :db/valueType   :db.type/string
     :db/cardinality :db.cardinality/one
     :db/doc         "SUN code, either 1, 2 or 3 digits"}

   ])

; Add see-instead-flag? See https://docs.datomic.com/on-prem/best-practices.html#annotate-schema /Sara

;; TODO add Transaction Function that checks that the term is connected to a concept


(def term-schema
  [{:db/ident       :term/base-form
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/unique      :db.unique/identity ; Should this really be unique/identity? Same term can be different concepts. /Sara
    :db/doc         "Term value, the actual text string that is referring to concepts"}

   {:db/ident       :term/special-usage
    :db/valueType   :db.type/keyword
    :db/cardinality :db.cardinality/one
    :db/doc         "A restricted term has term/special-usage :restricted, a historic term has term/special-usage :historic"}

   {:db/ident       :term/term-to-use-instead
    :db/valueType   :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/doc         "A historic term refers to a term to use instead."}])

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
    :db/doc         "Text describing the relation."}

   {:db/ident        :relation/affinity-percentage
    :db/valueType    :db.type/long
    :db/cardinality  :db.cardinality/one
    :db/doc          "The affinity percentage, how well the demand for an occupation is satisfied by a simillar occupation"
    }

   ])
