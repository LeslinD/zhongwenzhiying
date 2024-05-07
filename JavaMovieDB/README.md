# Movie DB with insert, delete, update, select built by Java Web.

## Prerequisites

- **Java**: jdk-17
- **IDE**: JetBrains IntelliJ IDEA 2022.3.2(ultimate)
- **MySQL**: v8.0.33
- **Tomcat**: v9.0.83
- **JARs**:
  - mysql-connector-j-8.0.33.jar: for connecting the MySQL database;
  - javax.servlet.*.jar: for building servlet interfaces; 
  - jackson-annotations/core/databind-*.jar: for converting Objects/Json to Json/Objects;
  - more detail can be checked at [the lib directory](lib).

The configuration details can be checked [here](CONFIGURATION.md).

## Features

- **Succinct functionalities for movies and cast/crew query/update**.
  - Apply the MVC scheme; details can be checked at [Entity](src/entity/README.md), [Dao](src/dao/README.md), [Servlet](src/servlet/README.md).
- **Simply templates for SQLs generation**.
  - Separate the constant data(like table name, column name etc.); details can be checked at [SQLTemplate](src/SQLTemplate/README.md).
- **Statistics analysis by year or by movie**.
  - Generating 2-dimension statistics tables for specific needs; details can be checked at [Statistics](src/statistics/README.md)
- **Modern frontend framework**.
  - Leverage the Svelte framework to build succinct and nice node.js app; details can be checked at [MovieDB app]().
