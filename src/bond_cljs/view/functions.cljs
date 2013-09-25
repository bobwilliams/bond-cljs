(ns bond-cljs.view.functions
  (:use-macros [dommy.macros :only [node deftemplate sel sel1]])
  (:require [clojure.string :as str]
            [dommy.utils :as utils]
            [dommy.core :as dommy]
            [bond-cljs.view.templates :as templates :refer [make-id make-chat-id]]
            [bond-cljs.view.contact-list :as contacts]))

(defn render-page [page]
  (dommy/append! (sel1 :body) page))

(defn sel-id [id]
  (sel1 (str "#" id)))

(defn toggle-side-menu []
  (dommy/toggle-class! (sel1 :body) "side-menu-expanded"))

(defn show-dev-tools []
  (-> (js/require "nw.gui") (.-Window) (.get) (.showDevTools)))

(defn bind-events []
  (Mousetrap/bind "ctrl+p" #(toggle-side-menu))
  (Mousetrap/bind "ctrl+u" #(show-dev-tools))
  (dommy/listen! (sel1 :#nav-toggle) :click toggle-side-menu))

(defn add-chat [user]
  (dommy/append! (sel1 :#chat-tabs) (templates/user-chat user)))

(defn unfocus-current-chat []
  (map #(dommy/remove-class! % :active) (sel :#chat-tabs>li)))

(defn focus-chat [user]
  (unfocus-current-chat)
  (dommy/add-class! (sel-id (make-chat-id user)) :active))

(defn add-user [user]
  (dommy/append! (sel1 :#side-menu) (templates/contact-item user))
  (dommy/listen! (sel-id (make-id user)) :click #(.preventDefault %))) ;; TODO: Chat user

(defn ensure-user-removed [user]
  (map dommy/remove! (sel-id (make-id user))))

(defn ensure-user-exists [user]
  (let [existing-user (sel-id (make-id user))]
    (when (nil? existing-user)
      (add-user user))))

(defn handle-status-event [{:keys [from from-resource to status]}]
  (cond
    (= status :offline) (ensure-user-removed from)
    (= status :online) (ensure-user-exists from)))

(defn bind-event-stream [event-stream]
  (let [chat-stream (.filter event-stream #(= (:type %) :message))
        status-stream (.filter event-stream #(= (:type %) :status-update))
        activity-stream (.filter event-stream #(= (:type %) :activity))]
    ;(.onValue chat-stream #(.log js/console (str "CHAT: " %)))
    ;(.onValue activity-stream #(.log js/console (str "ACTIVITY: " %)))
    (contacts/react-to-status-stream status-stream)))