(ns bond-cljs.angular.util)

(def modules (atom {}))

(defn module
  ([n] (module n []))
  ([n deps]
   (let [n (name n)]
     (if-let [m (@modules (name n))]
       m
       (let [m (.module (.-angular js/window)
                  n
                  (into-array (map name deps)))]
         (swap! modules assoc n m)
         m)))))