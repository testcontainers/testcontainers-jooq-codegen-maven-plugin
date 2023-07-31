# testcontainers-jooq-codegen-maven-plugin

The `testcontainers-jooq-codegen-maven-plugin` simplifies the jOOQ code generation
by using [Testcontainers](https://www.testcontainers.org/) and applying database migrations.

[![Build](https://github.com/testcontainers/testcontainers-jooq-codegen-maven-plugin/actions/workflows/build.yml/badge.svg)](https://github.com/testcontainers/testcontainers-jooq-codegen-maven-plugin/actions/workflows/build.yml)
![Maven Central](https://img.shields.io/maven-central/v/org.testcontainers/testcontainers-jooq-codegen-maven-plugin?label=latest-version)

## Summary

- Plugin migration and code generation might be skipped using `skip` property
- If you need to reuse existing database connection - take a look at [Jooq section](#Jooq)

## Database Configuration

To configure a target database, you need to specify at least database `type` property.

#### Properties

| Parameter      | Required | Default value                                                              | Description                                                    |
|----------------|----------|----------------------------------------------------------------------------|----------------------------------------------------------------|
| type           | yes      |                                                                            | Database implementation one of: `POSTGRES`  `MYSQL`  `MARIADB` |
| containerImage |          | Provided from database type,usually latest version from official container | Image of used container if not default picked                  |
| username       |          | Provided from database container if not specified                          | Database username for container                                |
| password       |          | Provided from database container if not specified                          | Database password for container                                |
| databaseName   |          | Provided from database container if not specified                          | Database name for container                                    |

#### `database` block configuration

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

Flyway works the same way as the original plugin  
Please find original documentation by link https://flywaydb.org/documentation/usage/maven/

#### Configuration

At runtime default configuration files will be autoloaded as it documented -
https://flywaydb.org/documentation/configuration/configfile   
Currently, the plugin supports all properties existing in Flyway   
You can find them by original link   
https://flywaydb.org/documentation/configuration/parameters/   
<b>Now [config files parameter](https://flywaydb.org/documentation/configuration/parameters/configFiles) is not
implemented yet</b>, but you can use config file at default location ${baseDir}/flyway.conf

#### `flyway` block configuration

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

Liquibase's configuration works the same way as the original maven plugin, with some limitations   
Please find documentation by
link https://docs.liquibase.com/tools-integrations/maven/using-liquibase-and-maven-pom-file.html

#### Properties

Now supports only the most useful properties

| Property                       | type   | default                                                                                                                      |
|--------------------------------|--------|------------------------------------------------------------------------------------------------------------------------------|
| changeLogPath                  | String | if changeLogDirectory is provided - db.changelog-root.xml, otherwise - src/main/resources/db/changelog/db.changelog-root.xml |
| changeLogDirectory             | String | projectBaseDir                                                                                                               |
| parameters                     | Map    |                                                                                                                              |
| defaultSchemaName              | String |                                                                                                                              |
| liquibaseSchemaName            | String |                                                                                                                              |
| databaseChangeLogTableName     | String |                                                                                                                              |
| databaseChangeLogLockTableName | String |                                                                                                                              |

Reference to Liquibase properties - https://docs.liquibase.com/concepts/connections/creating-config-properties.html

#### `liquibase` block configuration

- Zero configuration with defaults

```xml

<liquibase/>
```

- Adding properties

```xml

<liquibase>
    <changeLogPath>db.changelog-root.yml</changeLogPath>
    <changeLogDirectory>src/main/resources/db/postgres/changelog</changeLogPath>
    <defaultSchemaName>custom</defaultSchemaName>
</liquibase> 
```

### JOOQ

#### Properties

`generator` - property to configure JOOQ code generation settings.
See https://www.jooq.org/doc/latest/manual/code-generation/codegen-configuration for all the supporting configuration
properties.  
`configurationFiles` / `configurationFile` - are not implemented yet   
`jdbc` - if contains all required jdbc parameters (url,name,password) -
existing database will be used, no container won't be spin up   
`baseDir` - directory relative to which generated sources will be generated , `{project.basedir}` - default

#### `jooq` block configuration

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

#### Plugin dependencies configuration

```xml

<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>${postgresql.version}</version>
</dependency>
```

## Examples

### Complete example

Example with `PostgreSQL` and minimal configuration with `Flyway` and `JOOQ`

```xml

<plugin>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers-jooq-codegen-maven-plugin</artifactId>
    <version>${testcontainers-jooq-codegen-maven-plugin.version}</version>
    <dependencies>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>postgresql</artifactId>
            <version>${testcontainers.version}</version>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>${postgresql.version}</version>
        </dependency>
    </dependencies>
    <executions>
        <execution>
            <id>generate-jooq-sources</id>
            <goals>
                <goal>generate</goal>
            </goals>
            <phase>generate-sources</phase>
            <configuration>
                <database>
                    <type>POSTGRES</type>
                </database>
                <flyway/>
                <jooq>
                    <generator>
                        <database>
                            <includes>.*</includes>
                            <inputSchema>public</inputSchema>
                        </database>
                        <target>
                            <packageName>org.jooq.codegen.maven.example</packageName>
                            <directory>target/generated-sources/jooq</directory>
                        </target>
                    </generator>
                </jooq>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### More examples

[MariaDB + Flyway](examples/mariadb-flyway-example )   
[MySQL + Flyway](examples/mysql-flyway-example )   
[Postgres + Flyway](examples/postgres-flyway-example )   
[Postgres + Liquibase](examples/postgres-liquibase-example )

### Try with example application

```shell
$ cd examples/postgres-flyway-example
$ mvn clean package
```

The JOOQ code should be generated under example/target/generated-sources/jooq folder.

## CREDITS:

This plugin is heavily based on official https://github.com/jOOQ/jOOQ/tree/main/jOOQ-codegen-maven.
