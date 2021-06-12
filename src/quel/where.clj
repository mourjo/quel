(ns quel.where
  (:require [quel.field :as qf]
            [quel.value :as qv]
            [clojure.string :as cs]))

(defn- dispatch-where
  [ctx [op & _]]
  (keyword op))


(defmulti gen-where
  #'dispatch-where)


(defmethod gen-where :>
  [ctx [_ left right]]
  (format "%s > %s"
          (qf/gen-field left)
          (qf/gen-field right)))

(defmethod gen-where :<
  [ctx [_ left right]]
  (format "%s < %s"
          (qf/gen-field ctx left)
          (qf/gen-field ctx right)))

(defmethod gen-where :>=
  [ctx [_ left right]]
  (format "%s >= %s"
          (qf/gen-field ctx left)
          (qf/gen-field ctx right)))

(defmethod gen-where :<=
  [ctx [_ left right]]
  (format "%s <= %s"
          (qf/gen-field ctx left)
          (qf/gen-field ctx right)))


(defmethod gen-where :=
  [ctx [_ & args]]
  (if (<= (count args) 2)
    (format "%s = %s"
            (qf/gen-field ctx (first args))
            (qf/gen-field ctx (second args)))
    (let [fields (map (partial qf/gen-field ctx) args)]
      (format "%s IN (%s)"
              (first fields)
              (cs/join "," (rest fields))))))


(defmethod gen-where :!=
  [ctx [_ & args]]
  (if (<= (count args) 2)
    (format "%s != %s"
            (qf/gen-field ctx (first args))
            (qf/gen-field ctx (second args)))
    (let [fields (map (partial qf/gen-field ctx) args)]
      (format "%s NOT IN (%s)"
              (first fields)
              (cs/join "," (rest fields))))))


(defmethod gen-where :is-empty
  [ctx [_ field]]
  (format "%s IS NULL" (qf/gen-field ctx field)))


(defmethod gen-where :is-not-empty
  [ctx [_ field]]
  (format "%s IS NOT NULL" (qf/gen-field ctx field)))
