(defproject jobtech-taxonomy-database "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/java.jdbc "0.7.8"]
                 ;; TODO Change back
                 ;; https://mvnrepository.com/artifact/com.microsoft.sqlserver/mssql-jdbc
                 ;;[com.microsoft.sqlserver/mssql-jdbc "7.1.3.jre11-preview"] ;; requires jre11
                 ;;[com.microsoft.sqlserver/mssql-jdbc "7.0.0.jre10"] ;; Ubuntu's openjdk-11-jdk only provides jre10
                 [com.microsoft.sqlserver/mssql-jdbc "7.0.0.jre8"]
                 [com.layerware/hugsql "0.4.9"]
                 [com.datomic/client-cloud "0.8.78"]
                 [cheshire "5.8.1"]
                 [nano-id "0.9.3"]
                 [camel-snake-kebab "0.4.0"]
                 [dk.ative/docjure "1.13.0"]
                 [javax.xml.bind/jaxb-api "2.3.1"]
                 ]
  :repositories [["snapshots" {:url "https://repo.clojars.org"
                               :username "batfish"
                               :password :env}]]
  :main ^:skip-aot jobtech-taxonomy-database.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :merge-ids {:main jobtech-taxonomy-database.utils.merge-new-nano-ids}}
  :aliases  {"main"   ["with-profile" "uberjar" "run"]
             "merge"  ["with-profile" "merge-ids" "run"]}
  :java-cmd "/usr/bin/java"
  :plugins [[lein-cljfmt "0.6.3"]]

  )
