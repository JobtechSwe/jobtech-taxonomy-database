(ns jobtech-taxonomy-database.converters.occupation-experience-year-converter
  (:require [jobtech-taxonomy-database.types :as t]
            [jobtech-taxonomy-database.converters.converter-util :as u]
            [jobtech-taxonomy-database.legacy-migration :as lm]))

(defn convert []
  (map #(u/create-concept t/occupation-experience-year
                          (:experienceyearcandidate %)
                          (:experienceyearcontact %)
                          (:occupationexperienceyearid %))
       (lm/fetch-data lm/get-occupation-experience-years)))
