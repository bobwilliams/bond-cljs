(ns bond-cljs.core
	(:require [bond-cljs.views :as views]
            [bond-cljs.chat :as chat]))

;; TODO: From Config File
(def config {:display-name "Skeezy McFly"
             :accounts [{:user "mcskeezy@gmail.com"
                         :pass "sUp3rS3Cr3t"
                         :host "talk.google.com"
                         :port 5222
                         :reconnect true}]})

;; Render Initial Page
(views/render-page (views/chat-page (:display-name config) []))

;; Bind Page Events
(views/bind-events)

;; Connect Accounts
(def chat-connections (map chat/connect-account (:accounts config)))

;; Create Chat Event Observable
(def chat-streams (map chat/event-stream chat-connections))

;; Bind Event Streams to UI
(doseq [s chat-streams] (views/bind-event-stream s))