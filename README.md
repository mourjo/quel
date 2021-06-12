# quel

A Clojure library designed to generate SQL strings for different dialects.

## Usage

## Notes

1. This program follows the specification in the resources directory (downloaded from
   [here](https://gist.github.com/salsakran/73eabd4943eccc397a2af618789a197a) on 12 June
   2021.
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
   ["=", <x>, <y>, <z>]
   And x, y, z can be <field> | <number> | <string> | nil
   So the result is (note that y and z are fields and not scalars):
   x IN (y, z)

   Similarly, the specification allows for is-empty to be applied to scalars
   // arg ::= <field> | <number> | <string> | nil
   ["is-empty", <arg>] // field IS NULL
5. NOT follows [this](https://www.w3schools.com/sql/sql_and_or.asp) specification.
6.
