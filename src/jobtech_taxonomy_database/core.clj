(ns jobtech-taxonomy-database.core
  (:gen-class)
  (:require [datomic.client.api :as d])
  )

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))


(def cfg {:server-type :peer-server
          :access-key "myaccesskey"
          :secret "mysecret"
          :endpoint "localhost:8998"})

(def client (d/client cfg))


(def conn (d/connect client {:db-name "hello"}))
