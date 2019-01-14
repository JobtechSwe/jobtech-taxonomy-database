(ns jobtech-taxonomy-database.converters.skills-converter
  (:gen-class)
  (:require [datomic.client.api :as d]
            [jobtech-taxonomy-database.schema :refer :all :as schema]
            [jobtech-taxonomy-database.legacy-migration :refer :all]
            [jobtech-taxonomy-database.config :refer :all]
            [jobtech-taxonomy-database.datomic-connection :refer :all :as conn]))

(defn ^:private fake-id
  "Temporary function until new id function with nano-ids is imported into this project.
  Accepts an integer and returns a string."
  [id]
  (format "%010d" id))


(defn ^:private make-tempid
  "Turn a string into a temporary ID that will not be used by other converters.
  Accepts a string and returns a string."
  [str]
  (format "skills-converter.%s" str))


(defn converter
  "Immutable skill converter."
  [skill]

  (let* [skill-main-headline (get skill :main-headline)
         headlines (get skill :headlines)
         term-mainhl (get skill-main-headline :term)
         skill-main-headline-id (get skill-main-headline :skillmainheadlineid)
         headline-output (map (fn [skill-headline]
                                (let* [term-hl (get skill-headline :term)
                                       skill-headline-id (get skill-headline :skillheadlineid)
                                       concept-hl {:db/id                     (fake-id skill-headline-id)
                                                   :concept/id                (fake-id skill-headline-id)
                                                   :concept/description       term-hl
                                                   :concept/preferred-term    (make-tempid term-hl)
                                                   :concept/alternative-terms (make-tempid term-hl)}
                                       relation {:relation/concept-1 (fake-id skill-main-headline-id)
                                                 :relation/concept-2 (fake-id skill-headline-id)
                                                 :relation/type      :main-headline-to-headline}]
                                  (list {:db/id  (make-tempid term-mainhl)
                                         :term/base-form term-mainhl}
                                        {:db/id  (make-tempid term-hl)
                                         :term/base-form term-hl}
                                        concept-hl
                                        relation)))
                              headlines)
         output (list (list {:db/id                     (fake-id skill-main-headline-id)
                             :concept/id                (fake-id skill-main-headline-id)
                             :concept/description       term-mainhl
                             :concept/preferred-term    (make-tempid term-mainhl)
                             :concept/alternative-terms (make-tempid term-mainhl)}))]
    (concat output headline-output)))


(defn convert
  ""
  []
  (apply concat
         (mapcat converter
                 (map (fn [skill-main-headline]
                        (let [skill-main-headline-id (get skill-main-headline :skillmainheadlineid)]
                          (hash-map :main-headline skill-main-headline
                                    :headlines (list (get-skillheadlines {:id skill-main-headline-id})))))
                      (get-skillmainheadlines)))))
