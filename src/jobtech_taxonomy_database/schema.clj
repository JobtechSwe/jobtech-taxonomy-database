(ns jobtech-taxonomy-database.schema)

(def concept-schema
  [{:db/ident :concept/id
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/unique :db.unique/identity
    :db/doc "Unique identifier for concepts"}

   {:db/ident :concept/description
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "Text describing the concept, is used for disambiguation. Deprecated"}

   {:db/ident :concept/definition
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "Text defining the concept, is used for disambiguation."}

   {:db/ident :concept/preferred-label
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "What we prefer to call the concept."}

   {:db/ident :concept/alternative-terms
    :db/cardinality :db.cardinality/many
    :db/valueType :db.type/ref
    :db/doc "All terms referring to this concept"}

   {:db/ident :concept/type
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "The concepts main type"}

   {:db/ident :concept/deprecated
    :db/valueType :db.type/boolean
    :db/cardinality :db.cardinality/one
    :db/doc "If a concept is deprecated"}

   {:db/ident :concept/quality-level
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one
    :db/doc "Quality level"}

   {:db/ident :concept/replaced-by
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/many
    :db/doc "Refers to other concepts that is replacing this one"}

   {:db/ident :concept.relation/related
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/many
    :db/doc "related concepts"}])

;; Example:
;;  (def some-concepts
;;    [{
;;      :concept/id                "MZ6wMoAfyP"
;;      :concept/description       "Kontaktmannaskap är ett sätt att organisera arbetet för kontinuitet. Kontaktmannen har till uppgift att kommunicera, informera och bidra till sociala aktiviteter samt fungera som en kontakt och länk till anhöriga. Inom omsorg är kontinuitet och kunskap oerhört viktigt för att skapa trygghet."
;;      }}])
;;  (d/transact conn {:tx-data some-concepts})


(def concept-schema-extras
  [{:db/ident :concept.external-standard/ssyk-code-2012
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/unique :db.unique/identity
    :db/doc "SSYK-2012 type"}

   {:db/ident :concept/sort-order
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one
    :db/doc "Value for display sort order in category"}

   {:db/ident :concept.external-standard/eures-code-2014
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "EURES code"}

   {:db/ident :concept.external-standard/driving-licence-code-2013
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "Driving licence code"}

   {:db/ident :concept.external-standard/implicit-driving-licences
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/many
    :db/doc "List of 'lower' ranking driving licences included in the licence"}

   {:db/ident :concept.external-standard/nuts-level-3-code-2013
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "NUTS level 3 code"}

   {:db/ident :concept.external-standard/national-nuts-level-3-code-2019
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "Swedish Län code"}

   {:db/ident :concept.external-standard/lau-2-code-2015
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "Swedish Municipality code"}

   {:db/ident :concept.external-standard/iso-3166-1-alpha-2-2013
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "Country code 2 letter"}

   {:db/ident :concept.external-standard/iso-3166-1-alpha-3-2013
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "Country code 3 letter"}

   {:db/ident :concept.external-standard/iso-639-3-alpha-2-2007
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "2 letter language code"}

   {:db/ident :concept.external-standard/iso-639-3-alpha-3-2007
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "3 letter language code"}

   {:db/ident :concept.external-database.ams-taxonomy-67/id
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "ID from legacy Taxonomy version 67"}

   {:db/ident :concept.external-standard/isco-code-08
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "ISCO-08 level 4"}

   {:db/ident :concept.external-standard/sun-education-field-code-2020
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "SUN education field code, either 1, 2 or 3 digits and a letter"}

   {:db/ident :concept.external-standard/sun-education-level-code-2020
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "SUN education level code, either 1, 2 or 3 digits"}

   {:db/ident :concept.external-standard/sun-education-field-code-2000
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "Old SUN education field code, either 1, 2 or 3 digits and a letter"}

   {:db/ident :concept.external-standard/sun-education-level-code-2000
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "Old SUN education level code, either 1, 2 or 3 digits"}

   {:db/ident :concept.external-standard/sni-level-code-2007
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "SNI 2007 level code"}])



; Add see-instead-flag? See https://docs.datomic.com/on-prem/best-practices.html#annotate-schema /Sara

;; TODO add Transaction Function that checks that the term is connected to a concept

;; Example:
;;  (def some-terms
;;    [{:term/base-form "Kontaktmannaskap"}])
;;  (d/transact conn {:tx-data some-terms})


(def concept-relation-schema
  [{:db/ident :relation/concept-1
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/doc "The entity ID of the first concept in a relation"}

   {:db/ident :relation/concept-2
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/doc "The entity ID of the second concept in a relation"}

   {:db/ident :relation/type
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "the type of relationship"}

   {:db/ident :relation/description
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "Text describing the relation."}

   {:db/ident :relation/substitutability-percentage
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one
    :db/doc "The substitutability percentage, how well the demand for an occupation is satisfied by a similar occupation"}])

(def version-schema
  [{:db/ident :taxonomy-version/id
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one
    :db/unique :db.unique/identity
    :db/doc "The version identifier of the taxonomy, used almost like a tag in Git."}
   {:db/ident :taxonomy-version/tx
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/doc "Transaction id representing as-of time point of the taxonomy version"}])

(def user-schema
  [{:db/ident :taxonomy-user/id
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "The user id. Intended to be added to the transaction."}])

(def daynote-schema
  [{:db/ident :daynote/comment
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "Comment entered by the user who modifies a concept or relation. Intended to be added to the transaction."}
   {:db/ident :daynote/ref
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/doc "Optional reference to a concept or relation this tx adds a note about. Intended to be added to the transaction. Needed only when there are no changes to a concept/relation in this transaction"}])


;; (d/transact (get-conn) {:tx-data [{:db/id "a" :concept/id "a" :concept/preferred-label "clojure" :concept.relation/related "b" }  {:db/id "b" :concept/id "b"  :concept/preferred-label "java"} ]} )


;; (d/q '[:find (pull ?e [*])  :in $ :where [?e :concept/id] ] (get-db))

;; (d/transact (get-conn) {:tx-data [{:db/id "c"  :concept/id "c" :concept/preferred-label "scala"}]})


;; (d/transact (get-conn) {:tx-data [[:db/add 37541725018783840 :concept.relation/related 67822275247734882]]} )


;; (d/transact (get-conn) {:tx-data [[:db/retract 37541725018783840 :concept.relation/related 67822275247734882]]} )


(def scheme-schema
  [{:db/ident :scheme/name
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "The scheme name."}

   {:db/ident :scheme/member
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/many
    :db/doc "members of the scheme"}])
