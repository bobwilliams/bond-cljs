(ns bond-cljs.chat
  (:require [cljs.nodejs :as node]
            [clojure.string :as str]))

(def xmpp (node/require "node-xmpp"))
(def bacon (.-Bacon (node/require "baconjs")))

(defn- split [u]
  (str/split u #"/"))

(defn- users-and-resources [attrs]
  {:to (-> attrs :to split first)
   :to-resource (-> attrs :to split second)
   :from (-> attrs :from split first)
   :from-resource (-> attrs :from split second)})

(defn- get-text [obj]
  (when obj (.getText obj)))

(defn- get-state [stanza]
  (when-let [show-child (.getChild stanza "show")]
    (get-text show-child)))

(defn- get-status [stanza attrs]
  (let [state (get-state stanza)]
    (cond
      (-> attrs :type (= "unavailable")) :offline
      (nil? state) :online
      (= state "away") :away
      :else :online)))

(defn- get-activity [stanza]
  (let [activity (-> stanza (.children) (aget 0) (.name))]
    (cond
      (= activity "cha:composing") :typing
      (= activity "cha:paused") :paused
      :else :none)))

(defn- convert-attrs [attrs]
  {:type (.-type attrs)
   :from (.-from attrs)
   :to (.-to attrs)
   :id (.-id attrs)})

(defn- presence-element []
  (bond-cljs.chat.xmpp.Element. "presence"))

;; TODO: Verify
(defn- disconnect-presence-element []
  (bond-cljs.chat.xmpp.Element. "presence" (js-obj "type" "unavailable")))

(defn- roster-element []
  (-> (bond-cljs.chat.xmpp.Element. "iq" (js-obj "type" "get"))
      (.c "query" (js-obj "xmlns" "jabber:iq:roster"))))

(defn- is-roster? [stanza]
  (-> stanza (.getChild "query") (.is "query" "jabber:iq:roster")))

(defn- is-presence? [stanza]
  (.is stanza "presence"))

(defn- is-message? [stanza attrs]
  (and
    (.is stanza "message")
    (= (:type attrs) "chat")))

(defn- is-activity? [stanza]
  (.is stanza "message"))

(defn- convert-presence [stanza attrs]
  (merge (users-and-resources attrs)
         {:type :status-update
          :status (get-status stanza attrs)}))

(defn roster-entry-group [entry]
  (if-let [group-tag (.getChild entry "group")]
    (-> group-tag (.-children) (aget 0))
    "Default"))

(defn- convert-roster-entry [entry]
  (let [attrs (.-attrs entry)]
    {:jid (-> attrs (.-jid))
     :name (-> attrs (.-name))
     :group (roster-entry-group entry)}))

(defn- convert-roster [stanza attrs]
  {:type :roster
   :roster (-> stanza (.getChild "query") (.getChildren "item") js->clj (.map convert-roster-entry) js->clj)})

(defn- convert-message [stanza attrs]
  (merge (users-and-resources attrs)
         {:type :message
          :message (-> stanza (.getChild "body") get-text)}))

(defn- convert-activity [stanza attrs]
  (merge (users-and-resources attrs)
         {:type :activity
          :activity (get-activity stanza)}))

(defn- convert-stanza-event [stanza]
  (let [attrs (convert-attrs (.-attrs stanza))]
    (cond
      (is-presence? stanza) (convert-presence stanza attrs)
      (is-roster? stanza) (convert-roster stanza attrs)
      (is-message? stanza attrs) (convert-message stanza attrs)
      (is-activity? stanza) (convert-activity stanza attrs)
      :else {:type "unknown" :attrs attrs})))

(defn- connect [{:keys [user pass host port reconnect] :as account}]
  (bond-cljs.chat.xmpp.Client.
    (js-obj "jid" user
            "password" pass
            "host" host
            "port" port
            "reconnect" reconnect)))

(defn connect-account [account]
  (let [client (connect account)]
    (doto client
      (.on "online" (fn []
                      (println "Connected!!")
                      (.send client (presence-element))
                      (.send client (roster-element))))
      (.on "error" (fn [err]
                     (println "Error!!!: " err))))
    client))

(defn event-stream [xmpp-client]
  (let [stream (.fromEventTarget bacon xmpp-client "stanza")]
    (-> stream
        (.map convert-stanza-event))))

(defn disconnect [xmpp-client]
  (.send xmpp-client (disconnect-presence-element)))