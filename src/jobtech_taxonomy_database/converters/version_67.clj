(ns jobtech-taxonomy-database.converters.version-67
  (:require [datomic.client.api :as d]
            [jobtech-taxonomy-database.legacy-migration :as legacy-migration]
            [jobtech-taxonomy-database.converters.nano-id-assigner :as nano]
            [jobtech-taxonomy-database.datomic-connection :refer :all :as conn]
            [camel-snake-kebab.core :as csk]
            [clojure.spec.alpha :as s]
            [jobtech-taxonomy-database.types :as t]))

(def get-version
  '[:find ?e
    :in $
    :where [?e :taxonomy-version/id 66]])

(defn get-verion-66-entity []
  (ffirst (d/q get-version (get-db))))

#_(defn convert []
    (let [version-db-id (get-verion-66-entity)]
      [{:db/id version-db-id
        :taxonomy-version/id 67}]))

(defn convert []
  [{:taxonomy-version/id 67
    :taxonomy-version/tx "datomic.tx"}])
