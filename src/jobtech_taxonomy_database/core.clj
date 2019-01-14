(ns jobtech-taxonomy-database.core
  (:gen-class)
  (:require [datomic.client.api :as d]
            [clojure.pprint :as pp]
            [jobtech-taxonomy-database.schema :refer :all :as schema]
            [jobtech-taxonomy-database.legacy-migration :refer :all]
            [jobtech-taxonomy-database.datomic-connection :refer :all :as conn]
            [jobtech-taxonomy-database.converters.skills-converter :refer :all]))

(def converters
  "Each logic section of the old taxonomy can be handled by a converter
  set consisting of a reader, converter, writer and their
  namespace. By making the converter immutable it becomes easier to
  test. Add new converter sets here."
  '({:reader skill-retriever :converter skill-converter :writer skill-writer :namespace jobtech-taxonomy-database.converters.skills-converter}))

(defn -main
  []

  (init-new-db)

  (println "**** read from old taxonomy db, convert, and write to datomic...")
  (run! (fn [x]
          (let [input-data ((ns-resolve (get x :namespace) (get x :reader)))
                converted-data ((ns-resolve (get x :namespace) (get x :converter)) input-data)]
            ((ns-resolve (get x :namespace) (get x :writer)) converted-data)))
        converters)

  (println "**** basic queries to datomic to see some results...")
  (clojure.pprint/pprint (d/q '[:find ?x :where [_ :term/base-form ?x]] (get-db)))
  (clojure.pprint/pprint (d/q '[:find ?x :where [_ :concept/id ?x]] (get-db)))
  (clojure.pprint/pprint (d/q '[:find ?x :where [_ :relation/concept-1 ?x]] (get-db))))
