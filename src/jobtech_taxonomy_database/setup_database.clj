(ns jobtech-taxonomy-database.setup-database
  (:require [datomic.client.api :as d]
            [jobtech-taxonomy-database.config :as c]
            [jobtech-taxonomy-database.datomic-connection :as dc]
            )
  (:gen-class)
  )

(defn -main [& args]
  (when-not (:datomic-name (c/get-datomic-config))
    (binding [*out* *err*])
      (println "Usage: lein run -m jobtech-taxonomy-database.setup-database YOUR_DATABASE_NAME")
    (System/exit 1))
  (dc/init-new-db)
  )
