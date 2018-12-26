(ns jobtech-taxonomy-database.util)


;; TODO: load from separate, profile specific, properties files (dev/prod)
;;  ... (delay (load-file (.getFile (resource "config.clj"))))
(def ^:private config
  {:datomic-name "taxonomy_v13"
   :datomic-cfg {:server-type :peer-server
                 :access-key  "myaccesskey"
                 :secret      "mysecret"
                 :endpoint    "localhost:8998"}})


(defn get-config []
  "Get active config. This seems overly complicated now, but I hope to pave the way to using lazy loading of property files."
  config)
