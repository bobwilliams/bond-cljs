(ns bond-cljs.core
	(:require [bond-cljs.views :as views]))

(views/render-page (views/chat-page ["Josh Skidmore","Jeremy Martin","Bryan Gilbert","Bob Williams"]))
(views/bind-events)

;;(defn init []
;;  (views/render-page (views/chat-page ["Josh Skidmore","Jeremy Martin","Bryan Gilbert","Bob Williams"]))
;;  (views/bind-events))
