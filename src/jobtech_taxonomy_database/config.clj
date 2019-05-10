(ns jobtech-taxonomy-database.config
  (:gen-class))

;; TODO: load from separate, profile specific, properties files (dev/prod)
;;  ... (delay (load-file (.getFile (resource "config.clj"))))

#_(def ^:private datomic-config
  {:datomic-name "taxonomy_v13"
   :datomic-cfg {:server-type :peer-server
                 :access-key  "myaccesskey"
                 :secret      "mysecret"
                 :endpoint    "localhost:8998"}})


;; "jobtech-taxonomy-henrik-dev" ;; "jobtech-taxonomy-development"  ;; "jobtech-taxonomy-production"

(def datomic-config
  { :datomic-name "jobtech-taxonomy-henrik-dev"
   :datomic-cfg {
                 :server-type :ion
                 :region "eu-west-1" ;; e.g. us-east-1
                 :system "prod-jobtech-taxonomy-db"
                 ;;:creds-profile "<your_aws_profile_if_not_using_the_default>"
                 :endpoint "http://entry.prod-jobtech-taxonomy-db.eu-west-1.datomic.net:8182/"
                 :proxy-port 8182
                 :timeout 6000000
                 }
   }
  )


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
