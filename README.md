# quel [![Clojure CI](https://github.com/mourjo/quel/actions/workflows/clojure.yml/badge.svg?branch=main)](https://github.com/mourjo/quel/actions/workflows/clojure.yml)

A Clojure library designed to generate SQL strings for different dialects.

## Usage

This uses [Leiningen](https://leiningen.org/#install) which needs to be installed to build
this project.

This can be run by starting a repl and calling the `generate-sql` function:

```shell
quel.core=> (def fields
       #_=>   {1 "id"
       #_=>    2 "name"
       #_=>    3 "date_joined"
       #_=>    4 "age"})
#'quel.core/fields
quel.core=>

quel.core=> (generate-sql "postgres" fields {"where" ["=" ["field" 3] nil]})
"SELECT * FROM data WHERE \"date_joined\" IS NULL ;"
```

An utility has been provided to run this via shell script -- ensure fields have string
encoded integer keys and all keys are syntax quoted:

```shell
lein run "sqlserver" "{\"1\": \"id\", \"2\": \"name\"}" "{\"where\": [\"=\", [\"field\", 2], \"cam\"], \"limit\": 10}"
SELECT TOP 10 * FROM data WHERE `name` = 'cam' ;
```

Tests written [here](test/quel/core_test.clj) has more examples calling the top level
function.

## Implementation Notes

1. This program follows the specification in the [resources
   directory](resources/problem_statement.md) (downloaded from
   [here](https://gist.github.com/salsakran/73eabd4943eccc397a2af618789a197a) on 12 June
   2021).
2. Validation is not part of this library, it assumes the input is valid. No guarantee on
   how the program will behave if the grammar mentioned in the specification is not
   followed. There may be errors, there may be incorrect SQL statements, it is outside the
   purview of this program, mostly due to time constraints of 3h.
3. Only the information contained in the problem statement was considered, an extensive
   research of the SQL standard was not done. That is, statements like "all the
   implications that has" has not been extensively tested as this is only a toy
   example. Care has been taken that the solution however is extensible to add all such
   exceptional scalar case handlings in one place.
4. Strictly follows the document, for example, the rule for = says :
   ```
   ["=", <x>, <y>, <z>]
   And x, y, z can be <field> | <number> | <string> | nil
   ```
   So the result is (note that y and z are fields and not scalars):
   ```
   x IN (y, z)
   ```
   Similarly, the specification allows for is-empty to be applied to scalars
   ```
   // arg ::= <field> | <number> | <string> | nil
   ["is-empty", <arg>] // field IS NULL
   ```
5. NOT follows [this](https://www.w3schools.com/sql/sql_and_or.asp) specification.
6. Mistake in example (should be age):
   ```
   generateSql( "postgres", fields, {"where": ["=", ["field", 4], 25, 26, 27]})
   // -> "SELECT * FROM data WHERE date_joined IN (25, 26, 27)"
   ```
7. Mistake in example (missing commas in IN clause)
   ```
   generateSql( "postgres", fields, {"where": ["=", ["field", 4], 25, 26, 27]})
   // -> "SELECT * FROM data WHERE date_joined IN (25, 26, 27)"
   ```


## Test coverage
```shell
|------------+---------+---------|
|  Namespace | % Forms | % Lines |
|------------+---------+---------|
|  quel.core |   91.43 |   90.91 |
| quel.field |  100.00 |  100.00 |
| quel.limit |  100.00 |  100.00 |
| quel.query |   96.45 |  100.00 |
| quel.value |   94.12 |  100.00 |
| quel.where |   80.31 |   80.70 |
|------------+---------+---------|
|  ALL FILES |   89.34 |   91.30 |
|------------+---------+---------|
```

## Versions and compatibility
This has been tested and developed on, so other versions may not work:

```
REPL-y 0.4.4, nREPL 0.8.3
Clojure 1.10.1
OpenJDK 64-Bit Server VM 15.0.2+7
```
