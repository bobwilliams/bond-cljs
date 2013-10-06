(ns bond-cljs.watchers
  (:require [bond-cljs.global :as g]
            [bond-cljs.accounts :as accounts]
            [bond-cljs.config :as config]))

(defn add-watches! []
  (add-watch g/config :conf-write (fn [_ _ old new] (config/handle-config-change old new)))
  (add-watch g/accounts :conf-accounts (fn [_ _ old new] (config/handle-accounts-update old new))))