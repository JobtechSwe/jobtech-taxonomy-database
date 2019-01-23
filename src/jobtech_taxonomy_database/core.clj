(ns jobtech-taxonomy-database.core
  (:gen-class)
  (:require [datomic.client.api :as d]
            [clojure.pprint :as pp]
            [jobtech-taxonomy-database.schema :refer :all :as schema]
            [jobtech-taxonomy-database.legacy-migration :refer :all]
            [jobtech-taxonomy-database.datomic-connection :refer :all]
            [jobtech-taxonomy-database.converters.skills-converter]
            [jobtech-taxonomy-database.converters.continent-converter]
            ;;[jobtech-taxonomy-database.converters.country-converter]
            ;;[jobtech-taxonomy-database.converters.drivers-license-converter]
            ;;[jobtech-taxonomy-database.converters.employment-duration-converter]
            ;;[jobtech-taxonomy-database.converters.language-converter]
            ;;[jobtech-taxonomy-database.converters.language-level-converter]
            ;;[jobtech-taxonomy-database.converters.skills-converter]
            ;;[jobtech-taxonomy-database.converters.worktime-extent-converter]
            ))

(def converters
  "Each logic section of the old taxonomy can be handled by a converter
  set consisting of a reader, converter, writer and their
  namespace. By making the converter immutable it becomes easier to
  test. Add new converter sets here."
  '({:namespace jobtech-taxonomy-database.converters.skills-converter}
    {:namespace jobtech-taxonomy-database.converters.continent-converter}
    ;;{:namespace jobtech-taxonomy-database.converters.country-converter}
    ;;{:namespace jobtech-taxonomy-database.converters.drivers-license-converter}
    ;;{:namespace jobtech-taxonomy-database.converters.employment-duration-converter}
    ;;{:namespace jobtech-taxonomy-database.converters.language-converter}
    ;;{:namespace jobtech-taxonomy-database.converters.language-level-converter}
    ;;{:namespace jobtech-taxonomy-database.converters.skills-converter}
    ;;{:namespace jobtech-taxonomy-database.converters.worktime-extent-converter}
    ))

(defn -main
  []

  (init-new-db)

  (println "**** read from old taxonomy db, convert, and write to datomic...")
  (run! (fn [x]
          (println "**** Calling " (get x :namespace))
          (let [converted-data ((ns-resolve (get x :namespace) 'convert))]
            (d/transact (get-conn) {:tx-data converted-data})))
        converters)

  (println "**** basic queries to datomic to see some results...")
  (clojure.pprint/pprint (d/q '[:find ?x :where [_ :term/base-form ?x]] (get-db)))
  (clojure.pprint/pprint (d/q '[:find ?x :where [_ :concept/id ?x]] (get-db)))
  (clojure.pprint/pprint (d/q '[:find ?x :where [_ :relation/concept-1 ?x]] (get-db))))
;Should we add a source attribute to the transaction? See https://docs.datomic.com/on-prem/best-practices.html#add-facts-about-transaction-entity
