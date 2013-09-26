(ns bond-cljs.angular.macros)

(defmacro fnj [args & body]
  (if (empty? args)
    `(fn [] ~@body)
    `(clojure.core/array ~@(map name args) (fn ~args ~@body))))

(defmacro def.controller [module n args & body]
  `(.controller ~module ~(name n) (fnj ~args ~@body)))

(defmacro defn.scope [n args & body]
  `(aset ~'$scope ~(name n) (fn ~args ~@body)))