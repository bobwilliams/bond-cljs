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

(def.controller m ContactListCtrl [$scope]
  (reset! scope $scope)
  (assoc! $scope :contacts (sorted-set)))

(defn remove-user [user]
  (let [contacts (:contacts @scope)
        updated-contacts (into (sorted-set) (filter #(not= user %) contacts))]
    (apply-scope #(assoc! @scope :contacts updated-contacts))))

(defn add-user [user]
  (let [contacts (:contacts @scope)]
    (println (type contacts))
    (when (not-any? #(= user %) contacts)
      (apply-scope #(assoc! @scope :contacts (conj contacts user))))))

(defn handle-status-event [{:keys [from from-resource to status]}]
  (cond
    (= status :offline) (remove-user from)
    (= status :online) (add-user from)))

(defn react-to-status-stream [status-stream]
  (.onValue status-stream handle-status-event))