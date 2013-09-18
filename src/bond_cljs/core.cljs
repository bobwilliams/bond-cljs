(ns bond-cljs.core
	(:require [bond-cljs.views :as views]
            [bond-cljs.chat :as chat]))

;; Render Initial Page
(views/render-page (views/chat-page "gilbertw1" []))

;; Bind Page Events
(views/bind-events)

;; Connect Gtalk Account
(def connection (chat/connect-account {:user "gilbertw1@gmail.com"
                                       :pass "ulmwstqtalehpqct"
                                       :host "talk.google.com"
                                       :port 5222
                                       :reconnect true}))


;; Create Chat Event Observable
(def chat-stream (chat/event-stream connection))

;; React to chat event stream
(views/bind-event-stream chat-stream)