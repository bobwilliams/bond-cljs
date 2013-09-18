(ns bond-cljs.views
  (:use-macros [dommy.macros :only [node deftemplate sel sel1]])
  (:require [clojure.string :as str]
            [dommy.utils :as utils]
            [dommy.core :as dommy]))

(defn make-id [user]
  (str/replace user #"[^\w]" "-"))

(defn render-page [page]
  (dommy/append! (sel1 :body) page))

(defn contact-for [username]
  [:a {:href "" :id (make-id username)}
    [:i.glyphicon.glyphicon-user]
    [:span username]])

(defn toggle-side-menu []
  (dommy/toggle-class! (sel1 :body) "side-menu-expanded"))

(defn show-dev-tools []
  (-> (js/require "nw.gui") (.-Window) (.get) (.showDevTools)))

(defn bind-events []
  (Mousetrap/bind "ctrl+p" #(toggle-side-menu))
  (Mousetrap/bind "ctrl+u" #(show-dev-tools))
  (dommy/listen! (sel1 :#nav-toggle) :click toggle-side-menu))

(deftemplate chat-page [username users]
  [:div#contacts-page
    [:div#header
      [:a#nav-toggle.glyphicon.glyphicon-circle-arrow-right {:href "#"}]
      [:a#settings-button.glyphicon.glyphicon-cog {:href "#"}]
      [:h1 username]]
    [:div#side-menu
      [:h3 "Contacts"]
      (map contact-for users)]
    [:div.container
      [:h1 "Bond"]]])

(defn add-user [user]
  (dommy/append! (sel1 :#side-menu) (contact-for user)))

(defn ensure-user-removed [user]
  (map dommy/remove! (sel (str "#" (make-id user)))))

(defn ensure-user-exists [user]
  (let [existing-user (sel1 (str "#" (make-id user)))]
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
    (.onValue status-stream handle-status-event)))