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


;; "jobtech-taxonomy-henrik-dev" ;; "jobtech-taxonomy-development"  ;; "jobtech-taxonomy-production" ;; "jobtech-taxonomy-production-2019-09-12-4"

(comment

  ;; production

  ;; export DATOMIC_SYSTEM=datomic-prod-v2
  ;; export DATOMIC_REGION=eu-central-1
  ;; export DATOMIC_SOCKS_PORT=8182

;;  curl -x socks5h://localhost:$DATOMIC_SOCKS_PORT http://entry.$DATOMIC_SYSTEM.$DATOMIC_REGION.datomic.net:8182/
  )

(comment

  ;; test

  ;; export DATOMIC_SYSTEM="prod-jobtech-taxonomy-db"
  ;; export DATOMIC_REGION="eu-west-1"
  ;; export DATOMIC_SOCKS_PORT=8182

  ;;  curl -x socks5h://localhost:$DATOMIC_SOCKS_PORT http://entry.$DATOMIC_SYSTEM.$DATOMIC_REGION.datomic.net:8182/


  )



#_(def datomic-config
  {:datomic-name   "jobtech-taxonomy-production-2019-12-03-1";; "jobtech-taxonomy-production-2019-11-29-2"
      :datomic-cfg {:server-type :ion
                    :region "eu-central-1"
                    :system "datomic-prod-v2"
                    :endpoint "http://datom-loadb-ucpudqb1175l-7e29b7ad396c5feb.elb.eu-central-1.amazonaws.com:8182"
                    :proxy-port 8182
                    :timeout 6000000000}})

(def datomic-config
  {:datomic-name  "henrik-dev-5"  ;; "jobtech-taxonomy-frontend-2019-11-22-1"
   :datomic-cfg {:server-type :ion
                 :region "eu-west-1" ;; e.g. us-east-1
                 :system "prod-jobtech-taxonomy-db"
                 ;;:creds-profile "<your_aws_profile_if_not_using_the_default>"
                 :endpoint "http://entry.prod-jobtech-taxonomy-db.eu-west-1.datomic.net:8182/"
                 :proxy-port 8182
                 :timeout 6000000000}})

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
