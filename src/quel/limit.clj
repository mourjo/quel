(ns quel.limit)


(defn- dispatch-limit
  [ctx limit-n]
  (if (number? limit-n)
    (:dialect ctx)
    :default))


(defmulti gen-limit
  #'dispatch-limit)


(defmethod gen-limit :postgres
  [ctx limit-n]
  (str "LIMIT " limit-n))


(defmethod gen-limit :mysql
  [ctx limit-n]
  (str "LIMIT " limit-n))


(defmethod gen-limit :sqlserver
  [ctx limit-n]
  (str "TOP " limit-n))


(defmethod gen-limit :default
  [ctx limit-n]
  "")
