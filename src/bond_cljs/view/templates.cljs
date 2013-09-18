(ns bond-cljs.view.templates
  (:use-macros [dommy.macros :only [node deftemplate sel sel1]])
  (:require [clojure.string :as str]
            [dommy.utils :as utils]
            [dommy.core :as dommy]))

(defn make-id [username]
  (str/replace username #"[^\w]" "-"))

(defn make-chat-id [username]
  (str (make-id username) "-chat"))

(deftemplate nav-bar [username]
  [:div#header
    [:a#nav-toggle.glyphicon.glyphicon-circle-arrow-right {:href "#"}]
    [:a#settings-button.glyphicon.glyphicon-cog {:href "#"}]
    [:h1 username]])

(deftemplate contact-item [username]
  [:a {:href "" :id (make-id username)}
    [:i.glyphicon.glyphicon-user]
    [:span username]])

(deftemplate contacts-list [users]
  [:div#side-menu
    [:h3 "Contacts"]
    (map contact-item users)])

(deftemplate settings-menu []
  [:div#settings])

(deftemplate chat-area []
  [:div.container
    [:ul#chat-tabs.nav.nav-pills]])

(deftemplate user-chat [username]
  [:li {:id (make-chat-id username)} 
      [:a username]])

(deftemplate main-page [username users]
  [:div#contacts-page
    (nav-bar username)
    (contacts-list users)
    (settings-menu)
    (chat-area)])