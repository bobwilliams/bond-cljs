(ns bond-cljs.accounts
  (:require-macros [bond-cljs.macros :refer [if-let* when-let*]])
  (:require [bond-cljs.global :as g]
            [bond-cljs.view.contacts-bar :as contacts-bar]
            [bond-cljs.chat :as chat]))

;; TODO: Key off of special id, not user to create/modify/delete accounts
(defn ensure-id [account]
  (if (nil? (:id account))
    (assoc account :id (:user account))
    account))

(defn plug-account-stream [stream]
  (.plug g/in-stream stream))

(defn add-new-account! [new-account]
  (let [acc-with-id (ensure-id new-account)
        client (chat/connect-account acc-with-id)
        stream (chat/event-stream client)
        stream-with-account (.map stream #(assoc % :account-id (:id acc-with-id))) ;; TODO: In Chat?
        unplug-fn (plug-account-stream stream-with-account)
        full-account (assoc acc-with-id :client client
                                        :stream stream-with-account
                                        :unplug-fn unplug-fn)]
    (swap! g/accounts conj full-account)))

(defn maybe-update-account [account updated-account]
  (if (= (:id account) (:id updated-account))
     updated-account
     account))

(defn unplug-account! [account]
  (when-let [unplug-fn (:unplug-fn account)]
    (unplug-fn)))

(defn disconnect-account! [account]
  (when-let [xmpp-client (:xmpp-client account)]
    (chat/disconnect xmpp-client)))

(defn delete-account! [account-id]
  (when-let [account (->> @g/accounts (filter #(= (:id %) account-id)) first)]
    (unplug-account! account)
    (disconnect-account! account)
    (contacts-bar/clear-contacts-for-account! account-id)
    (swap! g/accounts #(remove (fn [a] (= (:id a) account-id)) %))))

(defn update-account! [updated-account]
  (delete-account! (:id updated-account))
  (add-new-account! updated-account))