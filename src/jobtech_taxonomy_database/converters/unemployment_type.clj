(ns jobtech-taxonomy-database.converters.unemployment-type
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as lm]
            [jobtech-taxonomy-database.converters.converter-util :as u]
            [jobtech-taxonomy-database.types :as t]))

(defn convert-unemployment-type
  "Convert a Swedish unemployment type"
  [{:keys [sökandekategoriid beteckning]}]
  {:pre [sökandekategoriid beteckning]}
  (u/create-concept
   t/unemployment-type beteckning beteckning sökandekategoriid))

(defn convert
  "Query db for unemployment types (SKAT codes)"
  []
  (concat (map convert-unemployment-type
               (lm/fetch-data lm/get-skat-code))))
