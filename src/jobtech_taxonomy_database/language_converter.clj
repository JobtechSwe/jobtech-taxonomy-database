(ns jobtech-taxonomy-database.language-converter
  (:gen-class)
  (:require [datomic.client.api :as d]
            [jobtech-taxonomy-database.schema :refer :all :as schema]
            [jobtech-taxonomy-database.legacy-migration :refer :all]
            [jobtech-taxonomy-database.config :refer :all]
            [jobtech-taxonomy-database.datomic-connection :refer :all :as conn]))

; TODO continue with this module/library/file/whatever.
; I don't know what I'm doing and it's not finished.

(defn ^:private fake-id
  "Temporary function until new id function with nano-ids is imported into this project."
  [id]
  (format "%010d" id))

(defn language-converter
  "Immutable language converter."
  [language]
  [{:concept/id                (:languageid_2 language)
   :concept/description       (:term language)
   :concept/preferred-term    [:term/base-form (:term language)]
   :concept/alternative-terms #{[:term/base-form (:term language)]}}
  {:term/base-form (:term language)}]
  )

#_
(defn language-converter
  "Immutable language converter."
  [languages]

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
                                   concept-hl {:concept/id                (fake-id language-id)
                                               :concept/description       "zkrpkt"
                                               :concept/preferred-term    [:term/base-form term-hl]
                                               :concept/alternative-terms #{[:term/base-form term-hl]}}
                                   relation {:relation/concept-1 [:concept/id (fake-id skill-main-headline-id)]
                                             :relation/concept-2 [:concept/id (fake-id skill-headline-id)]
                                             :relation/type      :hyponym}]
                                  (list {:term/base-form term-mainhl}
                                        {:term/base-form term-hl}
                                        concept-hl
                                        relation)))
                          headlines)
                       output (list (list {:concept/id                (fake-id skill-main-headline-id)
                                           :concept/description       "hrpf"
                                           :concept/preferred-term    [:term/base-form term-mainhl]
                                           :concept/alternative-terms #{[:term/base-form term-mainhl]}}))]
                      (concat output headline-output)))
                skills)))))

(defn language-retriever
  "Get all languages from the old database."
  []
  (fetch-data get-language)
  )

#_
(defn language-retriever
  "Get all skills from the old database."
  []
  (map (fn [skill-main-headline]
         (let [skill-main-headline-id (get skill-main-headline :skillmainheadlineid)]
           (hash-map :main-headline skill-main-headline
                     :headlines (list (get-language {:id skill-main-headline-id})))))
       (get-language)))

(defn language-writer
  [data]
  (run! (fn [t]
          (d/transact (get-conn) {:tx-data (vec (list t))}))
        data))