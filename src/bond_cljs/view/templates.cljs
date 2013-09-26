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

(deftemplate contacts-list []
  [:div#side-menu {:ng-app "bond.contactList" :ng-controller "ContactListCtrl"}
    [:h3 "Contacts"]
    [:a {:href "#" :ng-repeat "contact in contacts"}
      [:i.glyphicon.glyphicon-user {:style "{{contactStatusCss(contact)}}"}]
      [:span "{{contact.name}}"]]])

(deftemplate settings-menu []
  [:div#settings])

(deftemplate chat-area []
  [:div.container
    [:ul#chat-tabs.nav.nav-pills]])

(deftemplate user-chat [username]
  [:li {:id (make-chat-id username)} 
      [:a username]])

(deftemplate main-page [username]
  [:div#contacts-page
    (nav-bar username)
    (contacts-list)
    (settings-menu)
    (chat-area)])