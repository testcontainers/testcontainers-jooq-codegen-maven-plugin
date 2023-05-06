# jooq-testcontainers-codegen-maven-plugin
jOOQ code generator using Testcontainers

## CREDITS:
This plugin is heavily based on official https://github.com/jOOQ/jOOQ/tree/main/jOOQ-codegen-maven.

## How to use?
The plugin is not yet published to maven central. So, you need to first install the plugin locally.

```shell
$ git clone https://github.com/sivalabs/jooq-testcontainers-codegen-maven-plugin.git
$ cd jooq-testcontainers-codegen-maven-plugin
$ ./mvnw clean install
```

Try with example application

```shell
$ cd example
$ ./mvnw clean package
```

The JOOQ code should be generated under example/target/generated-sources/jooq folder.
