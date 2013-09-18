(ns bond-cljs.corse
	(:require [cljs.nodejs :as node]
            [bond-cljs.view.templates :as templates]
            [bond-cljs.view.functions :as vfun]
            [bond-cljs.chat :as chat]))

(def fs (node/require "fs"))
;(def path (node/require "path"))
(def user-home (or (.-HOME process/env) (.-HOMEPATH process/env) (.-USERPROFILE process/env)))
(def bond-dir (str user-home "/.bond-cljs"))
(def bond-conf (str bond-dir "/" "conf.json"))

;; Ensure Bond Directory Exists
(when-not (.existsSync fs bond-dir)
  (.mkdirSync fs bond-dir))

;; Ensure Bond Config Exists
(when-not (.existsSync fs bond-conf)
  (.writeFileSync fs bond-conf "{}"))

;; Read config file
(def config-json (.readFileSync fs bond-conf))
(def config (-> config-json JSON/parse (js->clj :keywordize-keys true)))

(println config)

;; Render Initial Page
(vfun/render-page (templates/main-page (or (:display-name config) "User") []))

;; Bind Page Events
(vfun/bind-events)

;; Connect Accounts
(def chat-connections (map chat/connect-account (:accounts config)))

;; Create Chat Event Observable
(def chat-streams (map chat/event-stream chat-connections))

;; Bind Event Streams to UI
(doseq [s chat-streams] (vfun/bind-event-stream s))