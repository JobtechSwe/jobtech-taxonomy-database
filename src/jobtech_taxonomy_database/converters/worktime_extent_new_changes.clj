(ns jobtech-taxonomy-database.converters.worktime-extent-new-changes
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as lm]
            [jobtech-taxonomy-database.converters.converter-util :as u]
            [jobtech-taxonomy-database.types :as t]))

(defn convert-deprecated
  [{:keys [id-67]}]
  {:pre [id-67]}
  [(u/deprecate-concept t/worktime-extent id-67)]) ;list since only one value is deprecated

(defn convert []
  "Run this function after the database has been loaded"
  (remove nil? (concat
                (mapcat convert-deprecated (lm/fetch-data lm/get-deprecated-worktime-extent)))))
