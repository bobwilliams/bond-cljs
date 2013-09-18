(ns bond-cljs.corse
	(:require [bond-cljs.view.templates :as templates]
            [bond-cljs.view.functions :as vfun]
            [bond-cljs.chat :as chat]))

;; TODO: From Config File
(def config {:display-name "gilbertw1"
             :accounts [{:user "gilbertw1@gmail.com"
                         :pass "mejoqecyqujcfqqp"
                         :host "talk.google.com"
                         :port 5222
                         :reconnect true}]})

;; Render Initial Page
(vfun/render-page (templates/main-page (:display-name config) []))

;; Bind Page Events
(vfun/bind-events)

;; Connect Accounts
(def chat-connections (map chat/connect-account (:accounts config)))

;; Create Chat Event Observable
(def chat-streams (map chat/event-stream chat-connections))

;; Bind Event Streams to UI
(doseq [s chat-streams] (vfun/bind-event-stream s))