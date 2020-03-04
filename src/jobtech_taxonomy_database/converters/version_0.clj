(ns jobtech-taxonomy-database.converters.version-0)

(defn convert []
  [{:taxonomy-version/id 0
    :taxonomy-version/tx "datomic.tx"}])
