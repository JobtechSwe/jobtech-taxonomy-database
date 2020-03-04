(ns jobtech-taxonomy-database.converters.version-1)

(defn convert []
  [{:taxonomy-version/id 1
    :taxonomy-version/tx "datomic.tx"}])
