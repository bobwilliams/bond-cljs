(ns bond-cljs.accounts
  (:require [bond-cljs.global :as g]
            [bond-cljs.chat :as chat]))

(defn add-account-change-watch [key watch-fun]
  (add-watch g/accounts key (fn [_ _ _ new] (watch-fun new))))

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
        stream-with-account (.map stream #(assoc % :account acc-with-id))
        unplug-fn (plug-account-stream stream-with-account)
        full-account (assoc acc-with-id :client client
                                        :stream stream-with-account
                                        :unplug-fn unplug-fn)]
    (swap! g/accounts conj full-account)))

(defn maybe-update-account [account updated-account]
  (if (= (:id account) (:id updated-account))
     updated-account
     account))

;; TODO: Clean up UI and XMPP after removing account
(defn delete-account! [account-id]
  (swap! g/accounts #(filter (fn [a] (not= (:id a) account-id)) %)))

(defn update-account! [updated-account]
  (delete-account! (:id updated-account))
  (add-new-account! updated-account))