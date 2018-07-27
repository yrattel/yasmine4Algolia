**This is a test**

* This code will not work without src/main/resources/ folder containing **hn_logs.tsv**

This is a Java Spring Boot application exposing the following endpoints through a REST API:
 * `GET /1/queries/count/<DATE_PREFIX>`: returns a JSON object specifying the number of distinct queries that have been done during a specific time range
 * `GET /1/queries/popular/<DATE_PREFIX>?size=<SIZE>`: returns a JSON object listing the top `<SIZE>` popular queries that have been done during a specific time range

This application is tested with Spring Boot test framework.