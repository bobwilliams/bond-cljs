(ns bond-cljs.view.settings
  (:require-macros [bond-cljs.angular.macros :refer [def.controller defn.scope def.directive fnj]])
  (:require [cljs.nodejs :as node]
            [bond-cljs.view.templates :as templates]
            [bond-cljs.view.functions :as vfun]
            [bond-cljs.global :as g]
            [dommy.core :refer [html]])
  (:use [bond-cljs.angular.util :only [module]]))

(def m (module "bond.settings" []))
(def scope (atom nil))

(defn find-provider [id]
  (->> @g/providers (filter #(= id (:id %))) (first)))

(defn compile-template [scope compiler template]
  (as-> template t
       (html t)
       (compiler t)
       (t scope)))

(def.controller m SettingsCtrl [$scope $compile]
  (reset! scope $scope)
  
  (def settings-pages {:home (compile-template $scope $compile (templates/settings-home))
                       :choose-provider (compile-template $scope $compile (templates/settings-choose-provider))
                       :edit-account (compile-template $scope $compile (templates/settings-edit-account))
                       :confirm-delete-account (compile-template $scope $compile (templates/settings-confirm-delete))})
      
  (doto $scope
    (aset "accounts" (clj->js @g/accounts))
    (aset "providers" (clj->js @g/providers))
    (aset "currentAccount" nil)
    (aset "currentProvider" nil)
    (aset "isNewAccount" false))
  
  (defn.scope goToScreen [screen]
    (let [screen-template (get settings-pages 
                               (keyword screen)
                               (:home settings-pages))]
      (.html (js/$ "#settings-content") screen-template)))
  
  (defn.scope editAccount [account]
    (println "SettingsCtrl " (JSON/stringify account))
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
  
  (defn.scope saveAccount []
    (.goToScreen $scope "home")) ;; TODO: Save Account
  
  (defn.scope deleteAccount []
    (.goToScreen $scope "home"))) ;; TODO: Delete Account