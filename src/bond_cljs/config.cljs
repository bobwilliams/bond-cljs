(ns bond-cljs.config
  (:require [cljs.nodejs :as node]
            [bond-cljs.global :as g]
            [bond-cljs.accounts :as accounts]
            [bond-cljs.view.settings-bar :as settings-bar]))

(def fs (node/require "fs"))
(def user-home (or (.-HOME process/env) (.-HOMEPATH process/env) (.-USERPROFILE process/env)))
(def bond-dir (str user-home "/.bond-cljs"))
(def bond-conf (str bond-dir "/" "conf.json"))

(defn ensure-bond-dir-exists []
  (when-not (.existsSync fs bond-dir)
    (.mkdirSync fs bond-dir)))

(defn ensure-bond-conf-file-exists []
  (when-not (.existsSync fs bond-conf)
    (.writeFileSync fs bond-conf "{}")))

(defn read-config-file []
  (let [conf-json (.readFileSync fs bond-conf)]
    (-> conf-json JSON/parse (js->clj :keywordize-keys true))))

(defn write-config! [conf]
  (let [conf-json (-> conf (clj->js) (JSON/stringify))]
    (.writeFileSync fs bond-conf conf-json)))

(defn config-account [account]
  (dissoc account :client :unplug-fn :stream))

(defn add-watches! []
  (add-watch g/config :conf-write (fn [_ _ _ new] (write-config! new)))
  (add-watch g/accounts :conf-update (fn [_ _ _ new] (swap! g/config assoc :accounts (map config-account new)))))

;; TODO: Chicken or egg with accounts!
(defn initialize-configuration! []
  (ensure-bond-dir-exists)
  (ensure-bond-conf-file-exists)
  (let [config (read-config-file)]
    (reset! g/config config)
    (add-watches!)))