(ns jobtech-taxonomy-database.datomic-connection
  (:gen-class)
  (:require [datomic.client.api :as d]
            [jobtech-taxonomy-database.config :refer :all]
            [jobtech-taxonomy-database.schema :refer :all :as schema]))

(def find-concept-by-preferred-term-query
  '[:find (pull ?c
                [
                 :concept/id
                 :concept/description
                 {:concept/preferred-term [:term/base-form]}
                 {:concept/referring-terms [:term/base-form]}])
    :in $ ?term
    :where [?t :term/base-form ?term]
    [?c :concept/preferred-term ?t]

    ])
;; (d/q find-concept-by-preferred-term-query (get-db) "Kontaktmannaskap")

;;;; Private ;;;;
(declare db-conn)
(declare db-handler)

(defn ^:private get-conn-with-config
  [config]
  ;; create connection unless already created
  (if (not (bound? #'db-conn))
    (let [client (d/client (get config :datomic-cfg))]
      (def db-conn (d/connect client {:db-name (get config :datomic-name)}))))
  db-conn)

(defn ^:private get-db-with-config
  [config]
  ;; create connection unless already created
  (if (not (bound? #'db-handler))
    (def db-handler (d/db (get-conn-with-config config))))
  db-handler)

(defn ^:private init-new-db-with-conn [conn]
  (d/transact conn {:tx-data (vec (concat schema/term-schema
                                          schema/concept-schema
                                          schema/concept-schema-extras
                                          schema/concept-relation-schema ))}))

;;;; Public ;;;;

;; TODO fix a smoother handling of a default config perhaps with
;; some type of optional argument if clojure has that? TODO2: learn clojure...

(defn get-db
  ([]       (get-db-with-config (get-datomic-config)))
  ([config] (get-db-with-config config)))

(defn get-conn
  ([]       (get-conn-with-config (get-datomic-config)))
  ([config] (get-conn-with-config config)))

(defn init-new-db
  ([]       (init-new-db-with-conn (get-conn)))
  ([conn]   (init-new-db-with-conn conn)))
