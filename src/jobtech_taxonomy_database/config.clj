(ns jobtech-taxonomy-database.config
  (:require [cprop.core :as c])
  (:gen-class))

;; TODO: load from separate, profile specific, properties files (dev/prod)
;;  ... (delay (load-file (.getFile (resource "config.clj"))))

(def config
  (if (= "PROD" (System/getenv "jobtech-taxonomy-database-env"))
    (c/load-config :file "config/prod/config.edn")
    (c/load-config :file "config/dev/config.edn")
    ))


(def datomic-config
  {:datomic-name (first *command-line-args*)                ;; "jobtech-taxonomy-frontend-2019-11-22-1"
   :datomic-cfg (:datomic-cfg config)})

(def ^:private legacydb-config
  (:legacydb config)
  )

(defn get-datomic-config []
  "Get active config. This seems overly complicated now, but I hope to pave the way to using lazy loading of property files."
  datomic-config)

(defn get-legacydb-config []
  legacydb-config)
