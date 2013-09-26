(ns bond-cljs.view.contact-list
  (:require-macros [clang.angular :refer [def.controller defn.scope def.filter fnj]])
  (:require [clojure.string :as cs]
            [cljs.nodejs :as node])
  (:use [clang.util :only [? module]]))

(def status-order {:offline 2
                   :away 1
                   :online 0})

(defn contact-sort [{xname :name xjid :jid xstatus :status} {yname :name yjid :jid ystatus :status}]  
  (if (not= xstatus ystatus)
    (compare (get status-order xstatus) (get status-order ystatus))
    (compare (str (cs/lower-case xname) xjid) (str (cs/lower-case yname) yjid))))

(defn contact-sorted-set []
  (sorted-set-by contact-sort))

(def m (module "bond.contactList" []))
(def scope (atom nil))
(def contact-list (atom (contact-sorted-set)))

(defn apply-scope [fun]
  (.$apply @scope fun))

(defn make-set [contacts]
  (into (contact-sorted-set) contacts))

(defn time-m []
  (.getTime (js/Date.)))

(defn replace-contacts [contacts-set]
  (let [js-contacts (clj->js contacts-set)]
    (reset! contact-list contacts-set)
    (apply-scope #(assoc! @scope :contacts js-contacts))))

(def.controller m ContactListCtrl [$scope]
  (reset! scope $scope)
  (assoc! $scope :contacts (clj->js @contact-list))
  
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
  (let [contacts @contact-list
        updated-contacts (.reduce events #(update-contact-status %1 %2) contacts)]
    (replace-contacts (make-set updated-contacts))))

(defn react-to-status-stream [status-stream]
  (let [buffered-stream (.bufferWithTime status-stream 100)]
    (.onValue buffered-stream handle-status-events)))

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
  (let [contacts @contact-list
        updated-contacts (reduce update-contact-from-roster-entry contacts roster)]
    (replace-contacts (make-set updated-contacts))))

(defn react-to-roster-stream [roster-stream]
  (.onValue roster-stream handle-roster-event))