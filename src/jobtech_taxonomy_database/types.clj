(ns jobtech-taxonomy-database.types
  (:refer-clojure :exclude [keyword])
  (:gen-class)
  (:require
   [clojure.spec.alpha :as s]))

;;concept-types


(def continent "continent")
(def country "country")
(def driving-licence "driving-licence")
;; (def driving-licence-combination "driving_licence_combination") ;; Not used anymore
(def employment-duration "employment-duration")
(def employment-type "employment-type")
(def isco-level-4 "isco_level-4")
(def isco-level-1 "isco_level-1")
(def keyword "keyword")
(def language "language")
(def language-level "language-level")
(def municipality "municipality")
(def occupation-collection "occupation-collection")
(def occupation-field "occupation-field")
(def occupation-name "occupation-name")
(def region "region")
(def skill "skill")
(def skill-headline "skill-headline")
(def sni-level-1 "sni-level-1")
(def sni-level-2 "sni-level-2")
(def ssyk-level-1 "ssyk-level-1")
(def ssyk-level-2 "ssyk-level-2")
(def ssyk-level-3 "ssyk-level-3")
(def ssyk-level-4 "ssyk-level-4")
(def sun-education-field-1 "sun-education_field-1")
(def sun-education-field-2 "sun-education_field-2")
(def sun-education-field-3 "sun-education_field-3")
(def sun-education-field-4 "sun-education_field-4")
(def sun-education-level-1 "sun-education_level-1")
(def sun-education-level-2 "sun-education_level-2")
(def sun-education-level-3 "sun-education_level-3")
(def wage-type "wage-type")
(def worktime-extent "worktime-extent")

(s/def ::concept-types #{continent
                         country
                         driving-licence
                         ;; driving-licence-combination ;; Not used anymore
                         employment-duration
                         employment-type
                         language
                         language-level
                         municipality
                         region
                         skill
                         skill-headline
                         sni-level-1
                         sni-level-2
                         wage-type
                         worktime-extent
                         isco-level-4           ;previously OccupationGroup
                         isco-level-1           ;previously OccupationField
                         keyword                ;previously PopularSynonyms
                         occupation-collection  ;previously OccupationCollection
                         occupation-field       ;previously LocaleField
                         occupation-name        ;previously OccupationName
                         ssyk-level-1           ;previously LocaleLevel1
                         ssyk-level-2           ;previously LocaleLevel2
                         ssyk-level-3           ;previously LocaleLevel3
                         ssyk-level-4           ;previously LocaleGroup

                         sun-education-field-1
                         sun-education-field-2
                         sun-education-field-3
                         sun-education-field-4
                         })

;; relation-types
(def broader "broader")
(def narrower "narrower")
(def related "related")
(def occupation-name-affinity "affinity")

(s/def ::relation-types #{broader narrower occupation-name-affinity related})

(comment
  "This doesn't work anymore!"
  "I made this to create the code above from the response from jobtech taxonomy api get all types"
  "YOu have to manually change keyword to keyword-type"

  (def dump [["continent"],
             ["country"],
             ["driving-license"], ;; should be licence
             ["employment-duration"],
             ["employment-type"],
             ["isco-level-4"],
             ["keyword"],
             ["language"],
             ["language-level"],
             ["municipality"],
             ["occupation-collection"],
             ["occupation-field"],
             ["occupation-group"],
             ["occupation-name"],
             ["region"],
             ["skill"],
             ["skill-headline"],
             ["skill-main-headline"],
             ["ssyk-level-1"],
             ["ssyk-level-2"],
             ["ssyk-level-3"],
             ["sun-education-field-1"],
             ["sun-education-field-2"],
             ["sun-education-field-3"],
             ["sun-education-level-1"],
             ["sun-education-level-2"],
             ["sun-education-level-3"],
             ["wage-type"],
             ["worktime-extent"]])

  (defn generate-def [type]
    (println (str "(def "  (first type)  " \""  (csk/->snake_case_string (first type))   "\""      ")"))))
