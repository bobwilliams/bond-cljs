(ns bond-cljs.global
  (:require [bond-cljs.util :as u]))

;; TODO: Add watchers to update angular scopes
(def contact-list (atom (u/contact-sorted-set)))
(def accounts (atom []))
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
                                  :type "text"}]}]))
