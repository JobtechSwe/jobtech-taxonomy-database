(ns jobtech-taxonomy-database.converters.skills-converter
  (:gen-class)
  (:require [datomic.client.api :as d]
            [jobtech-taxonomy-database.schema :refer :all :as schema]
            [jobtech-taxonomy-database.legacy-migration :refer :all]
            [jobtech-taxonomy-database.config :refer :all]
            [jobtech-taxonomy-database.datomic-connection :refer :all :as conn]
            [jobtech-taxonomy-database.converters.nano-id-assigner :refer :all]
            ))

(defn ^:private make-tempid
  "Turn a string into a temporary ID that will not be used by other converters.
  Accepts a string and returns a string."
  [str]
  (format "skills-converter-%s" str))

(defn ^:private make-tempid-term
  ""
  [term lang]
  (make-tempid (format "term-%s-%s" term lang)))

(defn ^:private make-tempid-concept
  ""
  [type id]
  (make-tempid (format "skill-concept-%s-%d" type id)))

(defn convert-term
  "Convert single term."
  [term]
  (let [conv-term {:db/id  (make-tempid-term (get term :term) (get term :lang))
                   :term/base-form (get term :term)}]
    conv-term))

(defn convert-skill
  "Convert a single skill"
  [headline skill-id]
  (let* [pref-term (convert-term (first (fetch-data get-prefered-skill-term { :id skill-id })))
         alt-terms (map #(convert-term %)
                        (fetch-data get-referenced-skill-terms { :id skill-id }))
         description-67 (get pref-term :term/base-form)
         nano-id (get-nano :skill skill-id description-67)]
    (concat alt-terms
            pref-term
            (list {:relation/concept-1     (make-tempid-concept "headline" (get headline :head_id))
                   :relation/concept-2     (make-tempid-concept "skill" skill-id)
                   :relation/type          :headline-to-skill}
                  {:db/id                  (make-tempid-concept "skill" skill-id)
                   :concept/id             nano-id
                   :concept/description    (get pref-term :db/id)
                   :concept/preferred-term (get pref-term :db/id)
                   :concept/alternative-terms (map #(get % :db/id) alt-terms)
                   })
            )))


(defn convert-head "" [main-headline headline]
  (let [terms-conv (mapcat (fn [id]
                             (convert-skill headline (get id :skill_id)))
                           (fetch-data get-skills-for-headline { :id (get headline :head_id) }))
        id-67 (keyword (str (get headline :head_id)))           ;ska matcha legacyAmsTaxonomyId i json
        description-67 (get headline :head_term)                ;ska matcha preferredTerm i json
        nano-id (get-nano :skill id-67 description-67)]
    (concat terms-conv
            (list {:relation/concept-1     (make-tempid-concept "main-headline" (get main-headline :main_id))
                   :relation/concept-2     (make-tempid-concept "headline" (get headline :head_id))
                   :relation/type          :main-headline-to-headline}
                  {:db/id                  (make-tempid-concept "headline" (get headline :head_id))
                   :concept/id             nano-id
                   :concept/description    (get headline :head_term)
                   :concept/preferred-term (make-tempid-term (get headline :head_term) (get headline :lang))}
                  {:db/id  (make-tempid-term (get headline :head_term) (get headline :lang))
                   :term/base-form (get headline :head_term)})
            ))) ;; FIXME: visst saknar headlines alternativa termer?


(defn convert-mainhead "" [main-headline]
  (let [headlines-conv (mapcat (fn [headline]
                                 (convert-head main-headline headline))
                               (fetch-data get-skill-headlines {:id (get main-headline :main_id)})
                               )]
    (concat headlines-conv
            (let* [id-67 (keyword (str (get main-headline :main_id)))           ;ska matcha legacyAmsTaxonomyId i json
                   description-67 (get main-headline :main_term)                ;ska matcha preferredTerm i json
                   nano-id (get-nano :skill id-67 description-67)]
              (list {:db/id                     (make-tempid-concept "main-headline" (get main-headline :main_id))
                     :concept/id                nano-id
                     :concept/description       (get main-headline :main_term)
                     :concept/preferred-term    (make-tempid-term (get main-headline :main_term) (get main-headline :lang))}
                    {:db/id                     (make-tempid-term (get main-headline :main_term) (get main-headline :lang))
                     :term/base-form            (get main-headline :main_term)})))))

(defn convert "" []
  (distinct
   (mapcat (fn [main-headline]
             (convert-mainhead main-headline))
           (fetch-data get-skill-mainheadlines))))
