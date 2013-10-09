(ns bond-cljs.view.contacts-bar
  (:require-macros [bond-cljs.angular.macros :refer [def.controller defn.scope fnj]])
  (:require [clojure.string :as cs]
            [cljs.nodejs :as node]
            [bond-cljs.global :as g]
            [bond-cljs.util :as u])
  (:use [bond-cljs.angular.util :only [module]]))

(def m (module "bond.contactList" []))
(def scope (atom nil))

(defn apply-scope [fun]
  (.$apply @scope fun))

(defn make-set [contacts]
  (into (u/contact-sorted-set) contacts))

(defn time-m []
  (.getTime (js/Date.)))

(defn replace-contacts! [contacts-set]
  (let [js-contacts (clj->js contacts-set)]
    (reset! g/contact-list contacts-set)
    (apply-scope #(aset @scope "contacts" js-contacts))))

(def.controller m ContactsBarCtrl [$scope]
  (reset! scope $scope)
  (aset $scope "contacts" (clj->js @g/contact-list))
  
  (defn.scope contactStatusCss [contact]
    (condp = (.-status contact)
      "online" "color: green;"
      "away" "color: orange;"
      "")))

(defn maybe-update-status [contact jid status]
  (if (= jid (:jid contact))
    (conj contact [:status status])
    contact))

(defn update-contact-status [contacts {:keys [from from-resource to status]}]
  (if (not-any? #(= from (:jid %)) contacts)
    (conj contacts {:jid from :name from :status status})
    (map #(maybe-update-status % from status) contacts)))

(defn handle-status-events [events]
  (let [contacts @g/contact-list
        updated-contacts (.reduce events #(update-contact-status %1 %2) contacts)]
    (replace-contacts! (make-set updated-contacts))))

(defn react-to-status-stream [status-stream]
  (let [buffered-stream (.bufferWithTime status-stream 100)]
    (.onValue buffered-stream handle-status-events)))

(defn maybe-update-name [contact jid name account-id]
  (if (= jid (:jid contact))
    (assoc contact :name name :accounts (conj (:accounts contact) account-id))
    contact))

(defn update-contact-from-roster-entry [account-id contacts {:keys [jid name]}]
  (let [disp-name (or name jid)]
    (if (not-any? #(= jid (:jid %)) contacts)
      (conj contacts {:jid jid :name disp-name :status :offline :accounts #{account-id}})
      (map #(maybe-update-name % jid disp-name account-id) contacts))))

(defn handle-roster-event [{:keys [to to-resource roster account-id]}]
  (let [contacts @g/contact-list
        updated-contacts (reduce #(update-contact-from-roster-entry account-id %1 %2) contacts roster)]
    (replace-contacts! (make-set updated-contacts))))

(defn react-to-roster-stream [roster-stream]
  (.onValue roster-stream handle-roster-event))

(defn remove-account [account-id contact]
  (assoc contact :accounts (disj (set (:accounts contact)) account-id)))

(defn clear-contacts-for-account! [account-id]
  (let [list-removed-account (map #(remove-account account-id %) @g/contact-list)
        updated-contacts (remove #(= (:accounts %) #{}) list-removed-account)]
    (replace-contacts! updated-contacts)))