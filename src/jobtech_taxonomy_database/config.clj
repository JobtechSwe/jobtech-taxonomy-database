(ns jobtech-taxonomy-database.config
    (:gen-class))

;; TODO: load from separate, profile specific, properties files (dev/prod)
;;  ... (delay (load-file (.getFile (resource "config.clj"))))

(def ^:private datomic-config
  {:datomic-name "taxonomy_v13"
   :datomic-cfg {:server-type :peer-server
                 :access-key  "myaccesskey"
                 :secret      "mysecret"
                 :endpoint    "localhost:8998"}})

(def ^:private legacydb-config
  {:subprotocol "mssql"
   :subname "//localhost:1433"
   :user "SA"
   :password "Taxonomy123!"
   :sslmode "require"})

(defn get-datomic-config []
  "Get active config. This seems overly complicated now, but I hope to pave the way to using lazy loading of property files."
  datomic-config)

(defn get-legacydb-config []
  legacydb-config)
