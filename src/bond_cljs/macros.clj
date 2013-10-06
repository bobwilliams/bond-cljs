(ns bond-cljs.util)

(defn- recursive-when-let [[[form tst] & othrs] body]
  `(let [temp# ~tst]
     (when temp#
       (let [~form temp#]
         ~@(if (nil? othrs)
           body
           `(~(recursive-when-let othrs body)))))))

(defmacro when-let* [bindings & body]
   (recursive-when-let (partition 2 bindings) body))

(defn- recursive-if-let [[[form tst] & othrs] if-body else-body]
  `(let [temp# ~tst]
    (if temp#
      (let [~form temp#]
        ~(if (nil? othrs)
          if-body
          (recursive-if-let othrs if-body else-body)))
      ~else-body)))

(defmacro if-let* [bindings if-body else-body]
   (recursive-if-let (partition 2 bindings) if-body else-body))