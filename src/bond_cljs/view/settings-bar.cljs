(ns bond-cljs.view.settings-bar
  (:require-macros [bond-cljs.angular.macros :refer [def.controller defn.scope def.directive fnj]])
  (:require [cljs.nodejs :as node]
            [bond-cljs.view.templates :as templates]
            [bond-cljs.view.functions :as vfun]
            [bond-cljs.accounts :as accounts]
            [bond-cljs.global :as g]
            [dommy.core :refer [html]])
  (:use [bond-cljs.angular.util :only [module]]))

(def m (module "bond.settings" []))
(def scope (atom nil))

(defn apply-scope [fun]
  (.$apply @scope fun))

(defn find-provider [id]
  (->> @g/providers (filter #(= id (:id %))) (first)))

(defn compile-template [scope compiler template]
  (as-> template t
       (html t)
       (compiler t)
       (t scope)))

(def settings-pages {:home (templates/settings-home)
                     :choose-provider (templates/settings-choose-provider)
                     :edit-account (templates/settings-edit-account)
                     :confirm-delete-account (templates/settings-confirm-delete)})

(def.controller m SettingsBarCtrl [$scope $compile]
  (reset! scope $scope)
      
  (doto $scope
    (aset "accounts" (clj->js @g/accounts))
    (aset "providers" (clj->js @g/providers))
    (aset "currentAccount" nil)
    (aset "currentProvider" nil)
    (aset "isNewAccount" false))
  
  (defn.scope goToScreen [screen]
    (let [screen-template (get settings-pages 
                               (keyword screen)
                               (:home settings-pages))
          compiled-template (compile-template $scope $compile screen-template)]
      (.html (js/$ "#settings-content") compiled-template)))
  
  (defn.scope editAccount [account]
    (doto $scope
      (aset "currentAccount" account)
      (aset "currentProvider" (clj->js (find-provider (.-provider account))))
      (aset "isNewAccount" false)
      (.goToScreen "edit-account")))
  
  (defn.scope newAccount [provider]
    (doto $scope
      (aset "currentProvider" provider)
      (aset "currentAccount" (js-obj))
      (aset "isNewAccount" true)
      (.goToScreen "edit-account")))
  
  ;; TODO: Replace manual account update
  (defn.scope saveAccount [account]
    (let [clj-account (assoc (js->clj account :keywordize-keys true) :provider (.-id (.-currentProvider $scope)))]
      (if (.-isNewAccount $scope)
        (accounts/add-new-account! clj-account)
        (accounts/update-account! clj-account)))
    (aset $scope "accounts" (clj->js @g/accounts))
    (.goToScreen $scope "home"))
  
  (defn.scope deleteAccount [account-id]
    (accounts/delete-account! account-id)
    (aset $scope "accounts" (clj->js @g/accounts))
    (.goToScreen $scope "home")))