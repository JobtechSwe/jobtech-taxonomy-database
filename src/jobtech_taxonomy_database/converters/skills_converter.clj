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
  [type id]
  (format "fake-id-%s" (format "skill-concept-%s-%d" type id)))

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
  (let [terms-conv (map (fn [term]
                          (convert-term term))
                        (fetch-data get-skill-terms { :id skill-id }))]

    (into (list {:relation/concept-1     (make-tempid-concept "headline" (get headline :head_id))
                 :relation/concept-2     (make-tempid-concept "skill" skill-id)
                 :relation/type          :headline-to-skill}
                {:db/id                  (make-tempid-concept "skill" skill-id)
                 :concept/id             (fake-id "skill" skill-id)
                 :concept/description    (format "skill %s" (fake-id "skill" skill-id))
                 :concept/preferred-term (get (first terms-conv) :db/id) ;;FIXME
                 :concept/alternative-terms (get (first terms-conv) :db/id) ;;FIXME
                 })
          terms-conv)))


(defn convert-head "" [main-headline headline]
  (let [terms-conv (mapcat (fn [id]
                             (convert-skill headline (get id :skill_id)))
                           (fetch-data get-skills-for-headline { :id (get headline :head_id) }))]
    (conj terms-conv
                {:relation/concept-1     (make-tempid-concept "main-headline" (get main-headline :main_id))
                 :relation/concept-2     (make-tempid-concept "headline" (get headline :head_id))
                 :relation/type          :main-headline-to-headline}
                {:db/id                  (make-tempid-concept "headline" (get headline :head_id))
                 :concept/id             (fake-id "headline" (get headline :head_id))
                 :concept/description    (get headline :head_term)
                 :concept/preferred-term (make-tempid-term (get headline :head_term) (get headline :lang))}
                {:db/id  (make-tempid-term (get headline :head_term) (get headline :lang))
                 :term/base-form (get headline :head_term)})
          )) ;; FIXME: visst saknar headlines alternativa termer?


(defn convert-mainhead "" [main-headline]
  (let [headlines-conv (mapcat (fn [headline]
                                 (convert-head main-headline headline))
                               (fetch-data get-skill-headlines {:id (get main-headline :main_id)}))]
    (conj headlines-conv
           {:db/id                     (make-tempid-concept "main-headline" (get main-headline :main_id))
                 :concept/id                (fake-id "main-headline" (get main-headline :main_id))
                 :concept/description       (get main-headline :main_term)
                 :concept/preferred-term    (make-tempid-term (get main-headline :main_term) (get main-headline :lang))}
                {:db/id  (make-tempid-term (get main-headline :main_term) (get main-headline :lang))
                 :term/base-form (get main-headline :main_term)})))


(defn convert "" []
  (distinct
   (mapcat (fn [main-headline]
             (convert-mainhead main-headline))
           (fetch-data get-skill-mainheadlines))))

(defn save-legacy "" []
  (set! *print-length* -1)
  (with-open [w (clojure.java.io/writer "/tmp/data3.clj")]
    (binding [*out* w]
      (pr (convert)))))
;; sed -e 's|",|",\n|g' -e 's|} {|}\n{|g' -e 's|} (|}\n(|g' -e 's| #|\n#|g' < data.clj > data_indent.clj
;; (save-legacy)

;(defn load-legacy "" []
(defn convertX "" []
  (with-open [r (java.io.PushbackReader. (clojure.java.io/reader "/tmp/data3.clj"))]
    (binding [*read-eval* false]
      (read r))))

;; (convert)
;; (fetch-data get-skill-mainheadlines)
;; (fetch-data get-skill-headlines { :id 2 })
;; (fetch-data get-skill-terms { :id 1128 })
