(ns bond-cljs.views
  (:use-macros [dommy.macros :only [node deftemplate sel sel1]])
  (:require [dommy.utils :as utils]
            [dommy.core :as dommy]))

(defn render-page [page]
  (dommy/append! (sel1 :body) page))

(defn contact-for [username]
  [:a {:href ""}
    [:i.glyphicon.glyphicon-user]
    [:span username]])

(defn toggle-side-menu []
  (dommy/toggle-class! (sel1 :body) "side-menu-expanded"))

(defn bind-events []
  (dommy/listen! (sel1 :#nav-toggle) :click toggle-side-menu))

(deftemplate chat-page [users]
  [:div#contacts-page
    [:div#header
      [:a#nav-toggle.glyphicon.glyphicon-circle-arrow-right {:href "#"}]
      [:a#settings-button.glyphicon.glyphicon-cog {:href "#"}]
      [:h1 "gilbertw1"]]
    [:div#side-menu
      [:h3 "Contacts"]
        (map contact-for users)]
    [:div.container
      [:h1 "Bond"]]])