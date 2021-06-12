(defproject quel "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/tools.logging "1.1.0"]
                 [org.clojure/core.async "1.3.618"]
                 [log4j/log4j "1.2.17"]
                 [org.slf4j/slf4j-log4j12 "1.7.30"]
                 [com.stuartsierra/component "1.0.0"]
                 [org.clojure/core.match "1.0.0"]
                 [cheshire "5.10.0"]]
  :plugins [[lein-cloverage "1.2.2"]]
  :main quel.core
  :repl-options {:init-ns quel.core})
