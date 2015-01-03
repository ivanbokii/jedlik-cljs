(defproject jedlikcljs "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2644"]
                 [mocha-latte "0.1.2"]
                 [chai-latte "0.2.0"]]

  :plugins [[lein-cljsbuild "1.0.4"]
            [lein-npm "0.4.0"]]

  :node-dependencies [[mocha "2.1.0"]
                      [chai "1.10.0"]]

  :source-paths ["src" "target/classes"]

  :cljsbuild
    {:builds [{:id "test"
               :source-paths ["test" "src"]
               :compiler {:output-to "target/testable.js"
                          :output-dir "target/test-js"
                          :pretty-print true
                          :optimizations :simple
                          :cache-analysis true}}
              {:id "dev"
               :source-paths ["src"]
               :compiler {:output-to "target/jedlik.js"
                          :output-dir "target/jedlik"
                          :pretty-print true
                          :optimizations :simple}}]})
