(defproject domshot "0.2.0"
  :description "Take a snapshot of your dom, a domshot"
  :url "https://github.com/dillonforrest/domshot"
  :license {:name "MIT"
            :url "http://opensource.org/licenses/MIT"}

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2755"]]

  :node-dependencies [[source-map-support "0.2.8"]]

  :plugins [[lein-cljsbuild "1.0.4"]
            [lein-npm "0.4.0"]]

  :source-paths ["src" "target/classes"]

  :clean-targets ["out" "out-adv"]

  :cljsbuild {
    :builds [{:id "dev"
              :source-paths ["src"]
              :compiler {
                :main domshot.core
                :output-to "out/domshot.js"
                :output-dir "out"
                :optimizations :none
                :cache-analysis true
                :source-map true}}
             {:id "release"
              :source-paths ["src"]
              :compiler {
                :main domshot.core
                :output-to "out-adv/domshot.min.js"
                :output-dir "out-adv"
                :optimizations :advanced
                :pretty-print false}}]})
