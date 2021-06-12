(ns quel.core
  (:require [clojure.walk :as cw]
            [cheshire.core :as cc]
            [quel.query :as qq])
  (:gen-class))

(defn generate-sql
  [dialect fields query]
  (let [ctx {:dialect (keyword dialect)
             :fields fields}]
    (qq/gen-query ctx (cw/keywordize-keys query))))


(defn -main
  [dialect-string fields-json query-json]
  (try
    (println (generate-sql dialect-string
                           (cc/parse-string fields-json #(Integer/parseInt %))
                           (cc/parse-string query-json)))
    (catch Exception e
      (println "Invalid input, ensure fields have string encoded integer keys and all keys are syntax quoted."))))
