(ns jobtech-taxonomy-database.create-database
  (:require [datomic.client.api :as d]
            [jobtech-taxonomy-database.config :as c]
            [jobtech-taxonomy-database.datomic-connection :as dc]
            )
  (:gen-class)
  )




(defn get-timestamp []
  (.format (java.text.SimpleDateFormat. "yyyy-MM-dd-mm-ss") (new java.util.Date))
  )

(defn create-database-name [type]
  (str "jobtech-taxonomy-" type  "-" (get-timestamp))
  )

(defn create-database-with-name [type]
  (let [name (create-database-name type)]
    (do
      (println "Creating Database")
      (println name)
      (d/create-database (dc/get-client) {:db-name name })))
  )


(defn -main [& args]
  (let [name (first *command-line-args*)]
    (when-not name
      (binding [*out* *err*]
        (println "Usage: lein run -m jobtech-taxonomy-database.create-database 'dev|frontend|prod'")
        )
      (System/exit 1))
    (create-database-with-name name)
    )
  )
