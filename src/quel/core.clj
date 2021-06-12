(ns quel.core
  (:require [clojure.walk :as cw]))



(defn gen-where
  [ctx clause])


(defn gen-limit
  [ctx clause])


(defn gen-query
  [ctx query]
  (let [limit-clause (gen-limit ctx (:limit query))
        where-clause (gen-where ctx (:where query))]
    (case (:dialect ctx)
      :postgres (format "SELECT * FROM data WHERE %s %s" where-clause limit-clause)
      :mysql (format "SELECT * FROM data WHERE %s %s" where-clause limit-clause)
      :sqlserver (format "SELECT %s * FROM data WHERE %s" limit-clause where-clause))))



(defn generate-sql
  [dialect fields query]
  (let [ctx {:dialect (keyword dialect)
             :fields fields}]
    (gen-query ctx (cw/keywordize-keys query))))
