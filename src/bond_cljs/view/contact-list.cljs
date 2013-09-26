(ns bond-cljs.view.contact-list
  (:require-macros [clang.angular :refer [def.controller defn.scope def.filter fnj]])
  (:require [clojure.string :as cs]
            [cljs.nodejs :as node]
            clobber.core
            clang.directive.clangRepeat)
  (:use [clang.util :only [? module]]))

(def m (module "bond.contactList" ["clang"]))
(def scope (atom nil))

(defn apply-scope [fun]
  (.$apply @scope fun))

(def status-order {:offline 2
                   :away 1
                   :online 0})

(defn contact-sort [{xname :name xjid :jid xstatus :status} {yname :name yjid :jid ystatus :status}]  
  (if (not= xstatus ystatus)
    (compare (get status-order xstatus) (get status-order ystatus))
    (compare (str (cs/lower-case xname) xjid) (str (cs/lower-case yname) yjid))))

(defn contact-sorted-set []
  (sorted-set-by contact-sort))

(defn make-set [contacts]
  (into (contact-sorted-set) contacts))

(defn replace-contacts [contacts-set]
  (apply-scope #(assoc! @scope :contacts contacts-set)))

(def.controller m ContactListCtrl [$scope]
  (reset! scope $scope)
  (assoc! $scope :contacts (contact-sorted-set))
  
  (defn.scope contact-status-css [{status :status}]
    (condp = status
      :online "color: green;"
      :away "color: orange;"
      "")))

(defn maybe-update-status [contact jid status]
  (if (= jid (:jid contact))
    (conj contact [:status status])
    contact))

(defn update-contact-status [contacts jid status]
  (if (not-any? #(= jid (:jid %)) contacts)
    (conj contacts {:jid jid :name jid :status status})
    (map #(maybe-update-status % jid status) contacts)))

(defn handle-status-event [{:keys [from from-resource to status]}]
  (let [contacts (:contacts @scope)
        updated-contacts (update-contact-status contacts from status)]
    (replace-contacts (make-set updated-contacts))))

(defn react-to-status-stream [status-stream]
  (.onValue status-stream handle-status-event))

(defn maybe-update-name [contact jid name]
  (if (= jid (:jid contact))
    (conj contact [:name name])
    contact))

(defn update-contact-from-roster-entry [contacts {:keys [jid name]}]
  (let [disp-name (or name jid)]
    (if (not-any? #(= jid (:jid %)) contacts)
      (conj contacts {:jid jid :name disp-name :status :offline})
      (map #(maybe-update-name % jid disp-name) contacts))))

(defn handle-roster-event [{:keys [to to-resource roster]}]
  (println "starting update")
  (let [contacts (:contacts @scope)
        updated-contacts (reduce update-contact-from-roster-entry contacts roster)]
    (println "updated data structure")
    (replace-contacts (make-set updated-contacts))
    (println "updated UI")))

(defn react-to-roster-stream [roster-stream]
  (.onValue roster-stream handle-roster-event))