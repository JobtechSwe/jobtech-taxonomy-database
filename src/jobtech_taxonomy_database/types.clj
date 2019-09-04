(ns jobtech-taxonomy-database.types
  (:refer-clojure :exclude [keyword])
  (:gen-class)
  (:require
   ;[camel-snake-kebab.core :as csk]
   [clojure.spec.alpha :as s]))

;;concept-types


(def continent "continent")
(def country "country")
(def driving-licence "driving_licence")
(def driving-licence-combination "driving_licence_combination")
(def employment-duration "employment_duration")
(def employment-type "employment_type")
(def isco-level-4 "isco_level_4")
(def isco-level-1 "isco_level_1")
(def keyword "keyword")
(def language "language")
(def language-level "language_level")
(def municipality "municipality")
(def occupation-collection "occupation_collection")
(def occupation-field "occupation_field")
(def occupation-name "occupation_name")
(def region "region")
(def skill "skill")
(def skill-headline "skill_headline")
(def sni-level-1 "sni_level_1")
(def sni-level-2 "sni_level_2")
(def ssyk-level-1 "ssyk_level_1")
(def ssyk-level-2 "ssyk_level_2")
(def ssyk-level-3 "ssyk_level_3")
(def ssyk-level-4 "ssyk_level_4")
(def sun-education-field-1 "sun_education_field_1")
(def sun-education-field-2 "sun_education_field_2")
(def sun-education-field-3 "sun_education_field_3")
(def sun-education-field-4 "sun_education_field_4")
;; (def sun-education-level-1 "sun_education_level_1")
;; (def sun-education-level-2 "sun_education_level_2")
;; (def sun-education-level-3 "sun_education_level_3")
(def wage-type "wage_type")
(def worktime-extent "worktime_extent")

(s/def ::concept-types #{continent
                         country
                         driving-licence
                         driving-licence-combination
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
                         keyword           ;previously
                         occupation-collection  ;previously OccupationCollection
                         occupation-field       ;previously LocaleField
                         ;occupation-group       ;previously
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
(def occupation-name-affinity "occupation_name_affinity")

(s/def ::relation-types #{broader narrower occupation-name-affinity related})

(comment
  "This doesn't work anymore!"
  "I made this to create the code above from the response from jobtech taxonomy api get all types"
  "YOu have to manually change keyword to keyword-type"

  (def dump [["continent"],
             ["country"],
             ["driving-license"],
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
