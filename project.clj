(defproject bond-cljs "0.1.0-SNAPSHOT"
  :description "Bond: Platinum Edition Chat Client"
  :min-lein-version "2.0.0"
  :lesscss-paths ["less/main"]
  :lesscss-output-path "public/css"
  :dependencies [[node-webkit-cljs "0.1.4"]
                 [org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-1878"]
                 [prismatic/dommy "0.1.1"]]
  :plugins [[lein-cljsbuild "0.3.2"]
            [lein-lesscss "1.3-SNAPSHOT"]]
  :cljsbuild {:builds [{:source-paths ["src"]
                        :compiler {:output-to     "public/js/bond-cljs.js"
                                   :optimizations :simple  ;; :advanced,:simple,:whitespace
                                   :pretty-print  true}}]})
