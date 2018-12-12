(defproject jobtech-taxonomy-database "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [

[org.clojure/clojure "1.9.0"]
[org.clojure/java.jdbc "0.7.8"]
                 [com.microsoft.sqlserver/mssql-jdbc "7.0.0.jre10"]
                 [korma "0.4.3"]
                 [log4j "1.2.15" :exclusions [javax.mail/mail
                                              javax.jms/jms
                                              com.sun.jdmk/jmxtools
                                              com.sun.jmx/jmxri]]
    	         [com.datomic/client-pro "0.8.28"]
]
  :main ^:skip-aot jobtech-taxonomy-database.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
