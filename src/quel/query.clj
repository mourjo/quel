(ns quel.query
  (:require [clojure.walk :as cw]
            [quel.where :as qw]
            [clojure.string :as cs]
            [quel.limit :as ql]))


(defn- dispatch-query
  [ctx query]
  (:dialect ctx))


(defmulti gen-query
  #'dispatch-query)


(defmethod gen-query :postgres
  [ctx query]
  (let [limit-clause (cs/trim (ql/gen-limit ctx (:limit query)))
        where-clause (cs/trim (qw/gen-where ctx (:where query)))]
    (cond
      (and (seq limit-clause)
           (seq where-clause))
      (format "SELECT * FROM data WHERE %s %s ;" where-clause limit-clause)

      (seq limit-clause)
      (format "SELECT * FROM data %s ;" limit-clause)

      (seq where-clause)
      (format "SELECT * FROM data WHERE %s ;" where-clause)

      :else
      "SELECT * FROM data ;")))


(defmethod gen-query :mysql
  [ctx query]
  (let [limit-clause (cs/trim (ql/gen-limit ctx (:limit query)))
        where-clause (cs/trim (qw/gen-where ctx (:where query)))]
    (cond
      (and (seq limit-clause)
           (seq where-clause))
      (format "SELECT * FROM data WHERE %s %s ;" where-clause limit-clause)

      (seq limit-clause)
      (format "SELECT * FROM data %s ;" limit-clause)

      (seq where-clause)
      (format "SELECT * FROM data WHERE %s ;" where-clause)

      :else
      "SELECT * FROM data ;")))


(defmethod gen-query :sqlserver
  [ctx query]
  (let [limit-clause (cs/trim (ql/gen-limit ctx (:limit query)))
        where-clause (cs/trim (qw/gen-where ctx (:where query)))]
    (cond
      (and (seq limit-clause)
           (seq where-clause))
      (format "SELECT %s * FROM data WHERE %s ;" limit-clause where-clause)

      (seq limit-clause)
      (format "SELECT %s * FROM data ;" limit-clause)

      (seq where-clause)
      (format "SELECT * FROM data WHERE %s ;" where-clause)

      :else
      "SELECT * FROM data ;")))
