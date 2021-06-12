# quel

A Clojure library designed to generate SQL strings for different dialects.

## Usage

## Notes

1. Validation is not part of this library, it assumes the input is valid.

2. Only the information contained in the problem statement was considered, an extensive
   research of the SQL standard was not done. That is, statements like "all the
   implications that has" has not been extensively tested as this is only a toy
   example. Care has been taken that the solution however is extensible to add all such
   exceptional scalar case handlings in one place.
