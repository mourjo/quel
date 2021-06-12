(ns quel.field
  (:require [quel.value :as qv]))


(defn- extract-field-name
  [ctx [_ field-id]]
  (-> ctx
      :fields
      (get field-id)))


(defn- dispatch-field
  [ctx field]
  (if (sequential? field)
    (:dialect ctx)
    :default))


(defmulti gen-field
  #'dispatch-field)


(defmethod gen-field :postgres
  [ctx field]
  (format "\"%s\"" (extract-field-name ctx field)))


(defmethod gen-field :mysql
  [ctx field]
  (format "\"%s\"" (extract-field-name ctx field)))


(defmethod gen-field :sqlserver
  [ctx field]
  (format "`%s`" (extract-field-name ctx field)))


(defmethod gen-field :default
  [ctx field]
  (qv/gen-val ctx field))
