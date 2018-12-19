(defproject jobtech-taxonomy-database "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [

[org.clojure/clojure "1.9.0"]
[org.clojure/java.jdbc "0.7.8"]
                 [com.microsoft.sqlserver/mssql-jdbc "7.0.0.jre10"]
                 [com.layerware/hugsql "0.4.9"]
                 [com.datomic/client-pro "0.8.28"]
]
  :main ^:skip-aot jobtech-taxonomy-database.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
