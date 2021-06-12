(ns quel.value)

(defn gen-val
  [ctx val]
  (cond (string? val) (format "'%s'" val)
        (nil? val) "NULL"
        :else val))
