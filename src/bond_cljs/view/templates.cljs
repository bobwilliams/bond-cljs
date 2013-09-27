(ns bond-cljs.view.templates
  (:use-macros [dommy.macros :only [node deftemplate sel sel1]])
  (:require [clojure.string :as str]
            [dommy.utils :as utils]
            [dommy.core :as dommy]))

(deftemplate nav-bar [username]
  [:div#header
    [:a#contacts-button.glyphicon.glyphicon-circle-arrow-right {:href "#"}]
    [:a#settings-button.glyphicon.glyphicon-cog {:href "#"}]
    [:h1 username]])

(deftemplate contacts-list []
  [:div#contacts-bar.side-bar {:ng-controller "ContactListCtrl"}
    [:h3 "Contacts"]
    [:a {:href "#" :ng-repeat "contact in contacts"}
      [:i.glyphicon.glyphicon-user {:style "{{contactStatusCss(contact)}}"}]
      [:span "{{contact.name}}"]]])

(deftemplate settings-home []
  [:div.panel.panel-default
    [:div.panel-heading
      [:h3.panel-title "Accounts"]]
    [:div.list-group
      [:a.list-group-item {:ng-repeat "account in accounts" :ng-click "editAccount(account)" :href ""} "{{account.label}}"]]
    [:div.panel-footer
      [:a.btn.btn-sm.btn-primary {:href "" :ng-click "goToScreen('choose-provider')"} "Add Account"]]])

(deftemplate settings-choose-provider []
  [:div.panel.panel-default
    [:div.panel-heading
      [:h3.panel-title "Choose Provider"]]
    [:div.list-group
      [:a.list-group-item {:ng-repeat "provider in providers" :ng-click "newAccount(provider)" :href ""} "{{provider.name}}"]]
    [:div.panel-footer
      [:a.btn.btn-sm.btn-danger.pull-right {:type "submit" :ng-click "goToScreen('home')"} "Cancel"]]])

(deftemplate settings-edit-account []
    [:form#account-form.form-horizontal {:name "accountForm" :bootstrap-form-classes true}
      [:div.panel.panel-default
        [:div.panel-heading
          [:h3.panel-title "{{isNewAccount ? 'Create Account' : 'Edit Account'}}"]
          [:a.delete-account {:href "" :ng-if "!isNewAccount" :ng-click "deleteAccount()"}]]
        [:div.panel-body
          [:div
            [:div.form-group
              [:label {:for "provider"} "Chat Provider:"]
              [:div.bond-field
                [:input#provider {:type "text" :value "{{currentProvider.name}}" :disabled "true"}]]]]
          [:div {:ng-repeat "option in currentProvider.options" :ng-switch "option.type"}
            [:div.form-group {:ng-switch-when "text"}
              [:label {:for "{{option.key}}"} "{{option.label}}:"]
              [:div.bond-field
                [:input {:type "text" :id "{{option.key}}" :name "{{option.key}}" :ng-model "currentAccount[option.key]" :required true}]
                [:span.validation-message "Required"]]]
            [:div.form-group {:ng-switch-when "password"}
              [:label {:for "{{option.key}}"} "{{option.label}}:"]
              [:div.bond-field
                [:input {:type "password" :id "{{option.key}}" :name "{{option.key}}" :ng-model "currentAccount[option.key]" :required true}]
                [:span.validation-message "Required"]]]
            [:div.checkbox {:ng-switch-when "bool"}
              [:label
                [:input {:type "checkbox" :name "{{option.key}}" :ng-model "currentAccount[option.key]"} "{{option.label}}"]]]]]
        [:div.panel-footer
          [:button.btn.btn-sm.btn-primary.pull-left {:type "submit" :ng-disabled "accountForm.$invalid" :ng-click "saveAccount()"} "Save"
            [:span.glyphicon.glyphicon-ok]]
          [:a.btn.btn-sm.btn-danger.pull-right {:type "submit" :ng-click "goToScreen('home')"} "Cancel"]]]]) 

(deftemplate settings-confirm-delete []
  [:div#confirm-delete-account.panel.panel-default
    [:div.panel-heading
      [:h3.panel-title "Delete Account"]]
    [:div.panel-body
      [:p "Are you sure you want to delete this account ({{ currentAccount.label }})?"]]
    [:div.panel-footer
      [:a.btn.btn-sm.btn-danger.pull-left {:href "" :ng-click "deleteAccount(true)"} "Yes, Delete Account"]
      [:a.btn.btn-sm.btn-default.pull-right {:href "" :ng-click "editAccount(currentAccount)"} "Cancel"]]])

(deftemplate settings-bar []
  [:div#settings-bar.side-bar {:ng-controller "SettingsCtrl"}
    [:h3 "Settings"]
    [:div#settings-content
      (settings-home)]])

(deftemplate chat-area []
  [:div#chat-area.container])

(deftemplate main-page [username]
  [:div#main-page
    (nav-bar username)
    (contacts-list)
    (settings-bar)
    (chat-area)])