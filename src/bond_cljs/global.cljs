(ns bond-cljs.global
  (:require [bond-cljs.util :as u]
            [cljs.nodejs :as node]))

(def bacon (.-Bacon (node/require "baconjs")))

(def config (atom {}))
(def contact-list (atom (u/group-sorted-set)))

(def accounts (atom []))
(def in-stream (bond-cljs.global.bacon.Bus.))
(def out-stream (bond-cljs.global.bacon.Bus.))

(def providers (atom [{:id "gtalk"
                       :name "GTalk"
                       :options [{:label "Label"
                                  :key "label"
                                  :type "text"}
                                 {:label "Username"
                                  :key "user"
                                  :type "text"}
                                 {:label "Password"
                                  :key "pass"
                                  :type "password"}
                                 {:label "Host"
                                  :key "host"
                                  :type "text"}
                                 {:label "Port"
                                  :key "port"
                                  :type "text"}
                                 {:label "Auto Reconnect"
                                  :key "reconnect"
                                  :type "bool"}]}]))