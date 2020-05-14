(ns jobtech-taxonomy-database.converters.sun-2000-deprecator
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as lm]
            [jobtech-taxonomy-database.converters.nano-id-assigner :as nano-id-assigner]
            [jobtech-taxonomy-database.converters.converter-util :as u]
            [jobtech-taxonomy-database.types :as t]
            [clojure.spec.alpha :as s]
            [datomic.client.api :as d]
            [jobtech-taxonomy-database.datomic-connection :as conn]
            )
  )


(defn deprecate-sun [rows type]
  (map (fn [{:keys [id term code parent-id]}]
         (u/deprecate-concept type id)
         )
       rows
       )
  )


(defn convert
  "deprecate old sun 2000 levels and fields"
  []
  (concat
   (deprecate-sun (lm/fetch-data lm/get-sun-field-1) t/sun-education-field-1)
   (deprecate-sun (lm/fetch-data lm/get-sun-field-2) t/sun-education-field-2)
   (deprecate-sun (lm/fetch-data lm/get-sun-field-3) t/sun-education-field-3)
   (deprecate-sun (lm/fetch-data lm/get-sun-level-1) t/sun-education-level-1)
   (deprecate-sun (lm/fetch-data lm/get-sun-level-2) t/sun-education-level-2)
   (deprecate-sun (lm/fetch-data lm/get-sun-level-3) t/sun-education-level-3)
   ))
