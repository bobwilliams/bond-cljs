(ns bond-cljs.util
  (:require [clojure.string :as str]))

(def status-order {:offline 2
                   :away 1
                   :online 0})

(defn contact-sort [{xname :name xjid :jid xstatus :status} {yname :name yjid :jid ystatus :status}]  
  (if (not= xstatus ystatus)
    (compare (get status-order xstatus) (get status-order ystatus))
    (compare (str (str/lower-case xname) xjid) (str (str/lower-case yname) yjid))))

(defn contact-sorted-set []
  (sorted-set-by contact-sort))