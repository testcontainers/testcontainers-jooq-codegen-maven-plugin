# testcontainers-jooq-codegen-maven-plugin

The `testcontainers-jooq-codegen-maven-plugin` simplifies the jOOQ code generation 
by using [Testcontainers](https://www.testcontainers.org/) and applying database migrations.

[![Build](https://github.com/testcontainers/testcontainers-jooq-codegen-maven-plugin/actions/workflows/build.yml/badge.svg)](https://github.com/testcontainers/testcontainers-jooq-codegen-maven-plugin/actions/workflows/build.yml)

## Supported databases:
* Postgres
* MySQL
* MariaDB
* 
## Supported migration tools:
* Flyway - [supported properties](https://flywaydb.org/documentation/configuration/parameters/ )
* Liquibase - [supported properties](src/main/java/org/testcontainers/jooq/codegen/migration/runner/LiquibaseRunner.java)

## How to use?

1. **With PostgreSQL and Flyway/Liquibase migrations**

```xml

<project>
    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <testcontainers.version>1.18.3</testcontainers.version>
        <testcontainers-jooq-codegen-maven-plugin.version>0.0.2</testcontainers-jooq-codegen-maven-plugin.version>
        <jooq.version>3.18.3</jooq.version>
        <postgresql.version>42.6.0</postgresql.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.jooq</groupId>
            <artifactId>jooq</artifactId>
            <version>${jooq.version}</version>
        </dependency>
    </dependencies>
    <build>
        <plugins>
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
                                <!--
                                "type" can be: POSTGRES, MYSQL, MARIADB
                                -->
                                <type>POSTGRES</type>
                                <!--
                                "containerImage" is optional.
                                The defaults are 
                                    POSTGRES: postgres:15.2-alpine
                                    MYSQL: mysql:8.0.33
                                    MARIADB: mariadb:10.11
                                -->
                                <containerImage>postgres:15.2-alpine</containerImage>
                                <username>test</username> <!-- optional -->
                                <password>test</password> <!-- optional -->
                                <databaseName>test</databaseName> <!-- optional -->
                            </database>
                            <flyway>
                                <!--
                                You can configure any supporting flyway config here.
                                see https://flywaydb.org/documentation/configuration/parameters/ 
                                -->
                                <defaultSchema>bank</defaultSchema> <!-- optional -->
                                <createSchemas>true</createSchemas> <!-- optional -->
                                <locations> <!-- optional -->
                                    filesystem:src/main/resources/db/migration/postgres,
                                    filesystem:src/main/resources/db/migration/postgresql
                                </locations>
                            </flyway>
                            <!-- or
                            Now supports only most useful properties which you can find in LiquibaseRunner.java
                            https://docs.liquibase.com/concepts/connections/creating-config-properties.html
                            <liquibase>
                               <changeLogPath>db.changelog-root.xml</changeLogPath>
                               <changeLogDirectory>src/main/resources/db/changelog</changeLogPath>
                            </liquibase> -->
                            <!-- 
                                You can configure any supporting jooq config here. 
                                see https://www.jooq.org/doc/latest/manual/code-generation/codegen-configuration/
                            -->
                            <jooq>
                                <skip>false</skip>  <!-- optional -->
                                <generator>
                                    <database>
                                        <includes>.*</includes>
                                        <excludes>flyway_schema_history</excludes>
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
        </plugins>
    </build>
</project>
```

Try with example application

```shell
$ cd examples/postgres-flyway-example
$ mvn clean package
```

The JOOQ code should be generated under example/target/generated-sources/jooq folder.

## CREDITS:
This plugin is heavily based on official https://github.com/jOOQ/jOOQ/tree/main/jOOQ-codegen-maven.
