(ns bond-cljs.view.contacts-bar
  (:require-macros [bond-cljs.angular.macros :refer [def.controller defn.scope fnj]])
  (:require [clojure.string :as cs]
            [cljs.nodejs :as node]
            [bond-cljs.global :as g]
            [bond-cljs.util :as u])
  (:use [bond-cljs.angular.util :only [module]]))

(def m (module "bond.contactList" []))
(def scope (atom nil))

(defn make-group-set [groups]
  (into (u/group-sorted-set) groups))

(defn make-contact-set [contacts]
  (into (u/contact-sorted-set) contacts))

(defn apply-scope [fun]
  (.$apply @scope fun))

(defn time-m []
  (.getTime (js/Date.)))

(defn replace-contacts! [contacts-set]
  (let [js-contacts (clj->js contacts-set)]
    (reset! g/contact-list contacts-set)
    (apply-scope #(aset @scope "contactsList" js-contacts))))

(def.controller m ContactsBarCtrl [$scope]
  (reset! scope $scope)
  (aset $scope "contactsList" (clj->js @g/contact-list)))

(defn maybe-update-status [contact jid status]
  (if (= jid (:jid contact))
    (conj contact [:status status])
    contact))

;; TODO: Add if not exists
(defn update-contact-status [contacts {:keys [from from-resource to status]}]
  (map #(maybe-update-status % from status) contacts))

(defn update-contact-groups-status [groups event]
  (map #(assoc % :contacts (update-contact-status (:contacts %) event)) groups))

(defn convert-group-contact-set [group]
  (assoc group :contacts (make-contact-set (:contacts group))))

(defn handle-status-events [events]
  (let [updated-contacts (.reduce events #(update-contact-groups-status %1 %2) @g/contact-list)]
    (replace-contacts! (make-group-set (map convert-group-contact-set updated-contacts)))))

(defn react-to-status-stream [status-stream]
  (let [buffered-stream (.bufferWithTime status-stream 100)]
    (.onValue buffered-stream handle-status-events)))

(defn maybe-update-name [contact jid name account-id]
  (if (= jid (:jid contact))
    (assoc contact :name name :accounts (conj (:accounts contact) account-id))
    contact))

(defn update-contact-from-roster-entry [account-id contacts jid name]
  (let [disp-name (or name jid)]
    (if (not-any? #(= jid (:jid %)) contacts)
      (conj contacts {:jid jid :name disp-name :status :offline :accounts #{account-id}})
      (map #(maybe-update-name % jid disp-name account-id) contacts))))

(defn update-group-contact-from-roster [account-id group jid name group-name]
  (if (= group-name (:name group))
    (assoc group :contacts (update-contact-from-roster-entry account-id (:contacts group) jid name))
    group))

(defn update-group-from-roster-entry [account-id groups {:keys [jid name group]}]
  (if (not-any? #(= group (:name %)) groups)
    (conj groups {:name group :contacts (make-contact-set [{:jid jid :name (or name jid) :status :offline :accounts #{account-id}}])})
    (map #(update-group-contact-from-roster account-id % jid name group) groups)))

(defn handle-roster-event [{:keys [to to-resource roster account-id]}]
  (let [updated-contacts (reduce #(update-group-from-roster-entry account-id %1 %2) @g/contact-list roster)]
    (replace-contacts! (make-group-set updated-contacts))))

(defn react-to-roster-stream [roster-stream]
  (.onValue roster-stream handle-roster-event))

(defn remove-account [account-id contact]
  (assoc contact :accounts (disj (set (:accounts contact)) account-id)))

(defn clear-contacts-for-account! [account-id]
  (let [list-removed-account (map #(remove-account account-id %) @g/contact-list)
        updated-contacts (remove #(= (:accounts %) #{}) list-removed-account)]
    (replace-contacts! updated-contacts)))