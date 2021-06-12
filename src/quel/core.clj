(ns quel.core
  (:require [clojure.walk :as cw]
            [quel.query :as qq]))

(defn generate-sql
  [dialect fields query]
  (let [ctx {:dialect (keyword dialect)
             :fields fields}]
    (qq/gen-query ctx (cw/keywordize-keys query))))
