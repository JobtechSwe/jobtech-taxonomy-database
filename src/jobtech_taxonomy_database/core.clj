(ns jobtech-taxonomy-database.core
  (:gen-class)
  (:require [clojure.pprint :as pp]
            [jobtech-taxonomy-database.datomic-connection :as conn]
            [jobtech-taxonomy-database.converters.converter-util :as u]
            [clojure.walk :as walk]
            [clojure.string :as str]))

(def converters
  "Each logic section of the old taxonomy can be handled by a converter
  set consisting of a reader, converter, writer and their
  namespace. By making the converter immutable it becomes easier to
  test. Add new converter sets here."
  '[jobtech-taxonomy-database.converters.version-0
    jobtech-taxonomy-database.converters.driving-licence-converter
    jobtech-taxonomy-database.converters.employment-duration-converter
    jobtech-taxonomy-database.converters.employment-type-converter
    jobtech-taxonomy-database.converters.geographic-places-converter
    jobtech-taxonomy-database.converters.language-converter
    jobtech-taxonomy-database.converters.language-level-converter
    jobtech-taxonomy-database.converters.occupation-converter
    jobtech-taxonomy-database.converters.skills-converter
    jobtech-taxonomy-database.converters.occupation-experience-year-converter
    jobtech-taxonomy-database.converters.occupation-skill-relation-converter
    jobtech-taxonomy-database.converters.SNI-level-converter
    jobtech-taxonomy-database.converters.wage-type-converter
    jobtech-taxonomy-database.converters.worktime-extent-converter
    jobtech-taxonomy-database.converters.version-1

    jobtech-taxonomy-database.converters.employment-type-new-changes
    jobtech-taxonomy-database.converters.employment-duration-new-changes
    jobtech-taxonomy-database.converters.occupation-new-changes-converter
    jobtech-taxonomy-database.converters.skills-converter-new-changes
    jobtech-taxonomy-database.converters.occupation-skill-relation-new-changes
    jobtech-taxonomy-database.converters.geographic-places-new-changes
    jobtech-taxonomy-database.converters.worktime-extent-new-changes
    jobtech-taxonomy-database.converters.SUN-education-field-converter
    jobtech-taxonomy-database.converters.version-2])

(defn- ->converter-var [ns-sym]
  (requiring-resolve (symbol (str ns-sym) "convert")))

(defn- ->txs [ns-sym]
  (let [converter-id (last (str/split (name ns-sym) #"\."))]
    (conj ((->converter-var ns-sym))
          {:db/id "datomic.tx"
           :daynote/comment (str "Imported by " converter-id)})))

(defn find-dupes []
  (run! #(u/find-duplicate-ids ((->converter-var %))) converters))

(defn -main [& args]
  (conn/create-database)
  (conn/init-new-db)
  (println "**** Read from old taxonomy db, convert, and write to datomic...")
  (set! *print-length* 100000)
  (with-open [w (clojure.java.io/writer "/tmp/latest-convert-for-debug.edn")]
    (run! (fn [ns-sym]
            (println "**** Calling" ns-sym)
            (binding [*out* w]
              (println "**** Calling" ns-sym))
            (let [converted-data (->txs ns-sym)]
              (binding [*out* w]
                (pp/write converted-data))
              (->> converted-data
                   (walk/postwalk #(cond-> %
                                     (seq? %) vec
                                     (int? %) long))
                   (conn/transact-data))))
          converters)))

(-main)

(clojure.walk/postwalk
 #(cond-> %
    (seq? %) vec
    (int? %) long)
  txs)