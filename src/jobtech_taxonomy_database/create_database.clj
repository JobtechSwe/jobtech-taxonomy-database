(ns jobtech-taxonomy-database.create-database
  (:require [datomic.client.api :as d]
            [jobtech-taxonomy-database.config :as c]
            [jobtech-taxonomy-database.datomic-connection :as dc]
            )
  (:gen-class)
  )




(defn create-database-with-name [name]
  (d/create-database (dc/get-client) {:db-name name})
  )

;; (println "Usage: lein run -m jobtech-taxonomy-database.create-database 'my-database-name'")

(defn -main [& args]
 (first *command-line-args*)
  )
