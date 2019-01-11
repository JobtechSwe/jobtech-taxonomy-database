(ns jobtech-taxonomy-database.converters.skills-converter
  (:gen-class)
  (:require [datomic.client.api :as d]
            [jobtech-taxonomy-database.schema :refer :all :as schema]
            [jobtech-taxonomy-database.legacy-migration :refer :all]
            [jobtech-taxonomy-database.config :refer :all]
            [jobtech-taxonomy-database.datomic-connection :refer :all :as conn]))

(defn ^:private fake-id
  "Temporary function until new id function with nano-ids is imported into this project."
  [id]
  (format "%010d" id))


;;(defn keep-unique-db-id
;;  "Remove duplicate concepts"
;;  [[head & tail] list
;;   mem]
;;  (let 
;;  (if head
;;    


(defn skill-converter
  "Immutable skill converter."
  [skills]

  (vec
   (apply concat
          (apply concat
                 (map (fn [skill]
                        (let* [skill-main-headline (get skill :main-headline)
                               headlines (get skill :headlines)
                               term-mainhl (get skill-main-headline :term)
                               skill-main-headline-id (get skill-main-headline :skillmainheadlineid)
                               headline-output (map (fn [skill-headline]
                                                      (let* [term-hl (get skill-headline :term)
                                                             skill-headline-id (get skill-headline :skillheadlineid)
                                                             concept-hl {:db/id                     (fake-id skill-headline-id)
                                                                         :concept/id                (fake-id skill-headline-id)
                                                                         :concept/description       "zkrpkt"
                                                                         :concept/preferred-term    term-hl
                                                                         :concept/alternative-terms term-hl}
                                                             relation {:relation/concept-1 (fake-id skill-main-headline-id)
                                                                       :relation/concept-2 (fake-id skill-headline-id)
                                                                       :relation/type      :hyponym}]
                                                            (list {:db/id  term-mainhl
                                                                   :term/base-form term-mainhl}
                                                                  {:db/id  term-hl
                                                                   :term/base-form term-hl}
                                                                  concept-hl
                                                                  relation)))
                                                    headlines)
                               output (list (list {:db/id                     (fake-id skill-main-headline-id)
                                                   :concept/id                (fake-id skill-main-headline-id)
                                                   :concept/description       "hrpf"
                                                   :concept/preferred-term    term-mainhl
                                                   :concept/alternative-terms term-mainhl}))]
                              (concat output headline-output)))
                      skills)))))

(defn skill-retriever
  "Get all skills from the old database."
  []
  (map (fn [skill-main-headline]
         (let [skill-main-headline-id (get skill-main-headline :skillmainheadlineid)]
           (hash-map :main-headline skill-main-headline
                     :headlines (list (get-skillheadlines {:id skill-main-headline-id})))))
       (get-skillmainheadlines)))

(defn skill-writer
  [data]
  (d/transact (get-conn) {:tx-data data}))
