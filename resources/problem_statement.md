### The problem

Your challenge is to write & test a basic transpiler which outputs a SQL string given a structured expression. You should make it possible for others to extend your code and add support for new filter clause types, new literal types, or new dialects without having to modify the original code.

Please use whatever programming language you are most comfortable in.

You should provide a working and tested function called `generateSql` with the following signature:

```
generateSql( dialect, fields, query)
```

*  `dialect` is the keyword name of the SQL dialect to generate SQL for. For the purposes of this exercise, it will be either `sqlserver`, `postgres`, or `mysql`.
*  `fields` is a map of integer IDs to a Field name. e.g.
    ```
    {1: "name", 2: "location}
    ```
*  `query` is a map containing information about the limits and filters to include in the query you generate.

### Query DSL

For the purposes of this exercise you will be taking the `query` map and generating a SQL query like

```sql
SELECT * FROM data
```

and adding `WHERE` and [`LIMIT`](https://www.postgresql.org/docs/11/queries-limit.html)/[`TOP`](https://docs.microsoft.com/en-us/sql/t-sql/queries/top-transact-sql?view=sql-server-2017) clauses as appropriate. For example, you might generate one of the following queries:

```sql
SELECT * FROM data WHERE name = 'cam';          -- Postgres/MySQL w/ WHERE
SELECT * FROM data WHERE name = 'cam' LIMIT 10; -- MySQL w/ WHERE & LIMIT
SELECT * FROM data LIMIT 20;                    -- Postgres w/ LIMIT
SELECT TOP 20 * FROM data;                      -- SQL Server w/ LIMIT
```

##### `query`

`query` is a map with the following schema. `limit` and `where` are both optional keys.

```
{"limit": <unsigned-int>,
 "where": <where-clause>}
```

##### `where` clause

`where`, when present, is a vector defining what should go in the `WHERE` clause of the SQL you generate. It has the form:

```
[<operator> <args>+]

operator ::= "and" | "or" | "not" | "<" | ">" | "=" | "!=" | "is-empty" | "not-empty"
```

##### Operators

###### `:and` and `or`

`:and` and `or` are used purely as conjunctions and should support 1 or more arguments representing subclauses that contain other operators. These clauses can also be nested with other compound clauses.

```
["and", <where-clause>, <where-clause>]                       // logical conjunction e.g. SQL `AND` operator
["or", <where-clause>, <where-clause>]                       // logical disjunction (SQL `OR`)
["and", ["or", <where-clause>, <where-clause>], <where-clause>] // nested compound clauses

// this is considered legal -- treat it as just `<where-clause>`
["and", <where-clause>]
```

###### `not`

`not` is similar to `and` or `or` but for obvious reasons always wraps a single clause.

```
["not", <where-clause>]
```

There are different ways to negate `WHERE` clauses in SQL, so how you do it is up to you.

###### `<` and `>`

`<` and `>` both take exactly two args. Args are either references to Fields, discussed more below, or number literals.

```
// arg ::= <field> | <number>
["<", <arg>, <arg] // x < y
```

###### `=` and `!=`

`=` and `!=` operate almost the same way, but the value is not necessarily a number, and they can accept more than 2 args. In the 2-arg form, they work the same way as `<` and `>`. With more that 2 args, they correspond to SQL `IN` and `NOT IN` operators, respectively.

```
// arg ::= <field> | <number> | <string> | nil
["=", <x>, <y>]      // x = y
["!=", <x>, <y>]     // x != y
["=", <x>, <y>, <z>]  // x IN (y, z)
["!=", <x>, <y>, <z>] // x NOT IN (y, z)
```

Note that values might be `nil`, and all the implications that has.

###### `is-empty` and `not-empty`
`is-empty` and `not-empty` take a single argument and translate into the appropriate natural SQL syntax for `IS NULL` and `IS NOT NULL`. The argument can be anything accepted by `=` or `!=`.

```
// arg ::= <field> | <number> | <string> | nil
["is-empty", <arg>] // field IS NULL
```

##### `field` clause

Fields always have the form

```
["field", <unsigned-int>]
```
The integer in question corresponds to one of the keys in the `fields` map; you should replace it with the appropriate identifier in the generated SQL.

#### Examples

```
generateSql( "postgres", {1: "id", 2: "name"}, {"where": ["=", ["field", 2] "cam"]})
// -> "SELECT * FROM data WHERE name = 'cam';"

generateSql("mysql", {1: "id", 2: "name"}, {"where": ["=", ["field", 2], "cam"], "limit": 10})
// -> "SELECT * FROM data WHERE name = 'cam' LIMIT 10;"

generateSql("postgres", {1: "id", 2: "name"}, {"limit": 20})
// -> "SELECT * FROM data LIMIT 20;"

generateSql("sqlserver", {1: "id", 2: "name"}, {"limit": 20})
// -> "SELECT TOP 20 * FROM data;"
```

###### Considerations

*  Make sure that when the field is a literal value you include any proper handling of that value in your generated SQL. e.g. single quoting of string values like `column = 'string'`
*  Some field identifiers need to be quoted, for example if they contain hyphens. In Postgres and SQL Server you should quote identifiers with double quotes and with MySQL you should use backticks. It does not hurt to quote identifiers that do not need to be quoted.
*   Beware of magic NULLs in SQL ([SQL:2003 defines all Null markers as being unequal to one another](https://en.wikipedia.org/wiki/Null_(SQL)#When_two_nulls_are_equal:_grouping,_sorting,_and_some_set_operations)), and remember that they are valid arguments to `=` and `!=`

### Getting Started

#### `fields`

Use this as the first argument in your function when writing the code.  Remember that when your code is evaluated we will swap this out for other definitions, so ensure this can be dynamic.

```
{1: "id",
 2: "name",
 3: "date_joined",
 4: "age"}
```

#### Requirements

Provide working code w/ tests showing that the following queries will work using the above `fields` definition as input. Output may differ slightly depending on how you choose to tackle certain problems, but the query should be valid and have equivalent logic.

```
generateSql("postgres", fields, {"where": ["=", ["field", 3], nil]})
// -> "SELECT * FROM data WHERE date_joined IS NULL"

generateSql( "postgres", fields, {"where": [">", ["field", 4], 35]})
// -> "SELECT * FROM data WHERE age > 35"

generateSql( "postgres", fields, {"where": ["and", ["<", ["field", 1], 5], ["=", ["field", 2], "joe"]]})
// -> "SELECT * FROM data WHERE id < 5 AND name='joe'"

generateSql( "postgres", fields, {"where": ["or", ["!=", ["field", 3], "2015-11-01"] ["=", ["field", 1], 456]]})
// -> "SELECT * FROM data WHERE date_joined <> '2015-11-01' OR id = 456"

generateSql( "postgres", fields, {"where": ["and" ["!=", ["field", 3], nil], ["or", [">", ["field", 4], 25] ["=", ["field", 2] "Jerry"]]]})
// -> "SELECT * FROM data WHERE date_joined IS NOT NULL AND (age > 25 OR name = 'Jerry')"

generateSql( "postgres", fields, {"where": ["=", ["field", 4], 25, 26, 27]})
// -> "SELECT * FROM data WHERE date_joined IN (25, 26, 27)"

generateSql( "postgres", fields, {"where": ["=", ["field", 2], "cam"]})
// -> "SELECT * FROM data WHERE name = 'cam';"

generateSql( "mysql", fields, {"where": ["=", ["field", 2], "cam"], {"limit": 10})
// -> "SELECT * FROM data WHERE name = 'cam' LIMIT 10;"

generateSql( "postgres", fields, {"limit": 20})
// -> "SELECT * FROM data LIMIT 20;"

generateSql( "sqlserver", fields, {"limit": 20})
// -> "SELECT TOP 20 * FROM data;"
```

### Bonus Points (if time permits):

##### Macro support

Extend the above DSL to support the usage of macros, which contain a predefined part of the expression which gets expanded and inserted at runtime. Assume that macros would be an additional argument to the function call containing the macro definitions such as

```
{"is_joe": ["=", ["field", 2], "joe"]]}
```

then extend the DSL handling to allow for a new macro clause of the form `["macro", <macro-id>]` which when encountered would insert the appropriate macro definition.

For example:

```
["and", ["<", ["field", 1], 5], ["macro", "is_joe"]]
// -> "SELECT * from data where id < 5 AND name = 'joe'"
```

##### Nested Macro support

Extend the above DSL to support the usage of nested macros, where you can use one macro inside of another macro. For example  with macro definitions

```
{"is_joe": ["=", ["field", 2], "joe"]]
 "is_adult": [">", ["field", 4], 18]]
 “Is_old_joe: ["and", ["macro", "is_joe"], ["macro", "is_old"]]}
```

then

```
["and", ["<", ["field", 1], 5], ["macro", "is__old_joe"]]
// -> "SELECT * from data where id < 5 AND age > 18 AND name = 'joe'"
```

Prior to transforming a query dictionary, check that there are no circular macros and throw an error if there is a circular dependency, e.g. macro definitions of

```
{"is_good": ["and", ["macro", "is_decent"], [">", ["field", 4], 18]]
 "is_decent": ["and", ["macro", "is_good"], ["<", ["field", 5], 5]]}
```

should throw an error.

##### Query Optimization

Optimize out comparisons that are always logically true or logically false, for example

```
generateSql( "sqlserver", {}, {"where": ["is-empty", nil], {"limit": 10})
// [optimize out WHERE NULL IS NULL]
// -> "SELECT TOP 10 * FROM data"
```

Flatten or remove compound filters where possible, such as rewriting

```
["and", ["and", x, y], z] // -> ["and", x, y, z]
```

and

```
["not", ["not", x]] // -> x
```
