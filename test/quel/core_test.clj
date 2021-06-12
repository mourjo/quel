(ns quel.core-test
  (:require [clojure.test :refer :all]
            [quel.core :as sut]))

(def fields
  {1 "id"
   2 "name"
   3 "date_joined"
   4 "age"})

(deftest test-examples-1
  (is (= (sut/generate-sql "postgres" fields {"where" ["=" ["field" 3] nil]})
         "SELECT * FROM data WHERE \"date_joined\" IS NULL ;"))
  ;; ->  "SELECT * FROM data WHERE date_joined IS NULL"


  (is (= (sut/generate-sql  "postgres" fields {"where" [">" ["field" 4] 35]})
         "SELECT * FROM data WHERE \"age\" > 35 ;"))
  ;; ->  "SELECT * FROM data WHERE age > 35"

  (is (= (sut/generate-sql  "postgres" fields {"where" ["and" ["<" ["field" 1] 5] ["=" ["field" 2] "joe"]]})
         "SELECT * FROM data WHERE (\"id\" < 5) AND (\"name\" = 'joe') ;"))
  ;; -> "SELECT * FROM data WHERE id < 5 AND name='joe'"

  (is (= (sut/generate-sql  "postgres" fields {"where" ["or" ["!=" ["field" 3] "2015-11-01"] ["=" ["field" 1] 456]]})
         "SELECT * FROM data WHERE (\"date_joined\" <> '2015-11-01') OR (\"id\" = 456) ;"))
  ;; ->   "SELECT * FROM data WHERE date_joined <> '2015-11-01' OR id = 456"

  (is (= (sut/generate-sql  "postgres" fields {"where" ["and" ["!=" ["field" 3] nil] ["or" [">" ["field" 4] 25] ["=" ["field" 2] "Jerry"]]]})
         "SELECT * FROM data WHERE (\"date_joined\" IS NOT NULL) AND ((\"age\" > 25) OR (\"name\" = 'Jerry')) ;"))
  ;; ->  "SELECT * FROM data WHERE date_joined IS NOT NULL AND (age > 25 OR name = 'Jerry')"

  (is (= (sut/generate-sql  "postgres" fields {"where" ["=" ["field" 4] 25 26 27]})
         "SELECT * FROM data WHERE \"age\" IN (25,26,27) ;"))
  ;; ->  "SELECT * FROM data WHERE date_joined IN (25 26 27)" <--- this is a mistake in the sample, the field should be age

  (is (= (sut/generate-sql  "postgres" fields {"where" ["=" ["field" 2] "cam"]})
         "SELECT * FROM data WHERE \"name\" = 'cam' ;"))
  ;; ->  "SELECT * FROM data WHERE name = 'cam';"

  (is (= (sut/generate-sql  "mysql" fields {"where" ["=" ["field" 2] "cam"]
                                            "limit" 10})
         "SELECT * FROM data WHERE \"name\" = 'cam' LIMIT 10 ;"))
  ;; ->  "SELECT * FROM data WHERE name = 'cam' LIMIT 10;"

  (is (= (sut/generate-sql  "postgres" fields {"limit" 20})
         "SELECT * FROM data LIMIT 20 ;"))
  ;; ->  "SELECT * FROM data LIMIT 20;"

  (is (= (sut/generate-sql  "sqlserver" fields {"limit" 20})
         "SELECT TOP 20 * FROM data ;"))
  ;; -> "SELECT TOP 20 * FROM data;"
  )


(deftest test-examples-2
  (is (= (sut/generate-sql "postgres" fields {"where" ["=" ["field" 3] nil]})
         "SELECT * FROM data WHERE \"date_joined\" IS NULL ;"))
  ;; ->  "SELECT * FROM data WHERE date_joined IS NULL"

  (is (= (sut/generate-sql  "postgres" fields {"where" [">" ["field" 4] 35]})
         "SELECT * FROM data WHERE \"age\" > 35 ;"))
  ;; ->  "SELECT * FROM data WHERE age > 35"

  (is (= (sut/generate-sql  "postgres" fields {"where" ["and" ["<" ["field" 1] 5] ["=" ["field" 2] "joe"]]})
         "SELECT * FROM data WHERE (\"id\" < 5) AND (\"name\" = 'joe') ;"))
  ;; ->  "SELECT * FROM data WHERE id < 5 AND name='joe'"

  (is (= (sut/generate-sql  "postgres" fields {"where" ["or" ["!=" ["field" 3] "2015-11-01"] ["=" ["field" 1] 456]]})
         "SELECT * FROM data WHERE (\"date_joined\" <> '2015-11-01') OR (\"id\" = 456) ;"))
  ;; ->  "SELECT * FROM data WHERE date_joined <> '2015-11-01' OR id = 456"

  (is (= (sut/generate-sql  "postgres"
                            fields
                            {"where" ["and"
                                      ["!=" ["field" 3] nil]
                                      ["or"
                                       [">" ["field" 4] 25]
                                       ["=" ["field" 2] "Jerry"]]]})
         "SELECT * FROM data WHERE (\"date_joined\" IS NOT NULL) AND ((\"age\" > 25) OR (\"name\" = 'Jerry')) ;"))
  ;; ->  "SELECT * FROM data WHERE date_joined IS NOT NULL AND (age > 25 OR name = 'Jerry')"

  (is (= (sut/generate-sql  "postgres" fields {"where" ["=" ["field" 4] 25 26 27]})
         "SELECT * FROM data WHERE \"age\" IN (25,26,27) ;"))
  ;; ->  "SELECT * FROM data WHERE date_joined IN (25 26 27)" <-- missing commas in the example

  (is (= (sut/generate-sql  "postgres" fields {"where" ["=" ["field" 2] "cam"]})
         "SELECT * FROM data WHERE \"name\" = 'cam' ;"))
  ;; ->  "SELECT * FROM data WHERE name = 'cam';"

  (is (= (sut/generate-sql  "mysql" fields {"where" ["=" ["field" 2] "cam"] "limit" 10})
         "SELECT * FROM data WHERE \"name\" = 'cam' LIMIT 10 ;"))
  ;; ->  "SELECT * FROM data WHERE name = 'cam' LIMIT 10;"

  (is (= (sut/generate-sql  "postgres" fields {"limit" 20})
         "SELECT * FROM data LIMIT 20 ;"))
  ;; ->  "SELECT * FROM data LIMIT 20;"

  (is (= (sut/generate-sql  "sqlserver" fields {"limit" 20})
         "SELECT TOP 20 * FROM data ;"))
  ;; ->  "SELECT TOP 20 * FROM data;"
  )
