# testcontainers-jooq-codegen-maven-plugin

The `testcontainers-jooq-codegen-maven-plugin` simplifies the jOOQ code generation
by using [Testcontainers](https://www.testcontainers.org/) and applying database migrations. <br/>

[![Build](https://github.com/testcontainers/testcontainers-jooq-codegen-maven-plugin/actions/workflows/build.yml/badge.svg)](https://github.com/testcontainers/testcontainers-jooq-codegen-maven-plugin/actions/workflows/build.yml)

## Summary

You can skip code generation and migration using `skip` property <br/>
If you need to reuse existing database connection - take a look at [Jooq section](#Jooq)

## Databases:

* Postgres
* MySQL
* MariaDB

#### In order to configure Database use `database` block to your plugin `configuration`

| Parameter      | Required | Default value                                                              | Description                                                    |
|----------------|----------|----------------------------------------------------------------------------|----------------------------------------------------------------|
| type           | yes      |                                                                            | Database implementation one of: `POSTGRES`  `MYSQL`  `MARIADB` |
| containerImage |          | Provided from database type,usually latest version from official container | Image of used container if not default picked                  |
| username       |          | Provided from database container if not specified                          | Database username for container                                |
| password       |          | Provided from database container if not specified                          | Database password for container                                |
| databaseName   |          | Provided from database container if not specified                          | Database name for container                                    |

Example

```xml

<database>
    <type>POSTGRES</type>
    <containerImage>postgres:15-alpine</containerImage>
    <username>test</username>
    <password>test</password>
    <databaseName>test</databaseName>
</database>
```

## Migration tools:

### Flyway

#### Configuration

At runtime default configuration files will be autoloaded as it documented -
https://flywaydb.org/documentation/configuration/configfile <br/>
Currently, the plugin supports all properties existing in Flyway <br/>
You can find them by original link <br/>
https://flywaydb.org/documentation/configuration/parameters/ <br/>
<b>Now [config files parameter](https://flywaydb.org/documentation/configuration/parameters/configFiles) is not
implemented yet</b> <br/>

#### In order to use Flyway add `flyway` block to your plugin `configuration`

- Zero configuration with defaults

```xml 

<flyway/>
```

- Adding properties

```xml

<flyway>
    <defaultSchema>bank</defaultSchema>
    <createSchemas>true</createSchemas>
    <table>my_custom_history_table</table>
    <locations>
        filesystem:src/main/resources/db/migration/postgres,
        filesystem:src/main/resources/db/migration/postgresql
    </locations>
</flyway>
```

### Liquibase

Now supported only the most useful properties which you can find in
[LiquibaseRunner.java](src/main/java/org/testcontainers/jooq/codegen/migration/runner/LiquibaseRunner.java) <br/>
Reference to Liquibase properties - https://docs.liquibase.com/concepts/connections/creating-config-properties.html

#### In order to use Liquibase add `liquibase` block to your plugin `configuration`

```xml

<liquibase>
    <changeLogPath>db.changelog-root.xml</changeLogPath>
    <changeLogDirectory>src/main/resources/db/changelog</changeLogPath>
</liquibase> 
```

Before run - Make sure your plugin has dependency on a chosen jdbc database driver implementation

```xml

<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>${postgresql.version}</version>
</dependency>
```

### Jooq

#### Properties

`generator` - property to configure jooq generation plugin itself, original
reference - https://www.jooq.org/doc/latest/manual/code-generation/codegen-configuration/ <br/>
`configurationFiles` / `configurationFile` - are not implemented yet <br/>
`jdbc` - if contains all required jdbc parameters (url,name,password) -
existing database will be used, no container won't be spin up <br/>
`baseDir` - directory relative to which generated sources will be generated , `{project.basedir}` - default

#### In order to configure JOOQ add `jooq` block to your plugin `configuration`

```xml

<jooq>
    <generator>
        <database>
            ...
        </database>
    </generator>
    <jdbc>
        ....
    </jdbc>
</jooq>
```

## Examples

[MariaDB + Flyway](examples/mariadb-flyway-example ) <br/>
[MySQL + Flyway](examples/mysql-flyway-example ) <br/>
[Postgres + Flyway](examples/postgres-flyway-example ) <br/>
[Postgres + Liquibase](examples/postgres-liquibase-example ) <br/>

### Try with example application

```shell
$ cd examples/postgres-flyway-example
$ mvn clean package
```

The JOOQ code should be generated under example/target/generated-sources/jooq folder.

## CREDITS:

This plugin is heavily based on official https://github.com/jOOQ/jOOQ/tree/main/jOOQ-codegen-maven.
