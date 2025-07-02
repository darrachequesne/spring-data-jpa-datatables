# History

| Version                  | Release date  | Spring Boot compatibility |
|--------------------------|---------------|---------------------------|
| [7.1.0](#710-2025-07-02) | July 2025     | "                         |
| [7.0.1](#701-2025-02-18) | February 2025 | "                         |
| [7.0.0](#700-2025-02-13) | February 2025 | `>= 3.4.0`                |
| [6.0.4](#604-2024-04-03) | April 2024    | "                         |
| [6.0.3](#603-2024-03-24) | March 2024    | "                         |
| [6.0.2](#602-2024-03-03) | March 2024    | "                         |
| [6.0.1](#601-2023-02-12) | February 2023 | "                         |
| [6.0.0](#600-2023-01-02) | January 2023  | `>= 3.O.0 && < 3.4.0`     |
| [5.2.0](#520-2022-05-19) | May 2022      | "                         |
| [5.1.0](#510-2021-03-17) | March 2021    | "                         |
| [5.0.0](#500-2018-03-01) | March 2018    | `>= 2.O.0 && < 3.0.0`     |
| [4.3](#43-2017-12-24)    | December 2017 | "                         |
| [4.2](#42-2017-12-24)    | December 2017 | "                         |
| [4.1](#41-2017-04-05)    | April 2017    | "                         |
| [4.0](#40-2017-03-06)    | March 2017    | "                         |
| [3.1](#31-2016-12-16)    | December 2016 | "                         |
| [3.0](#30-2016-11-19)    | November 2016 | `>= 1.O.0 && < 2.0.0`     |


# Release notes

## [7.1.0](https://github.com/darrachequesne/spring-data-jpa-datatables/compare/v7.0.1...v7.1.0) (2025-07-02)

`spring-boot-dependencies` was updated from version `3.4.0` (Nov 2024) to `3.5.3` (Jun 2025).

Note: version ranges in `<dependencyManagement>` are supported starting with Maven 4 (https://issues.apache.org/jira/browse/MNG-4463).



## [7.0.1](https://github.com/darrachequesne/spring-data-jpa-datatables/compare/v7.0.0...v7.0.1) (2025-02-18)


### Bug Fixes

* properly compute search panes with related entities ([cc4b439](https://github.com/darrachequesne/spring-data-jpa-datatables/commit/cc4b439f2a7b30070c9fa1f4bcb9dab2ceb0bfc0))



## [7.0.0](https://github.com/darrachequesne/spring-data-jpa-datatables/compare/v6.0.4...v7.0.0) (2025-02-13)

### Features

* upgrade to Spring Boot 3.4.0 ([fd5c9f7](https://github.com/darrachequesne/spring-data-jpa-datatables/commit/fd5c9f7aac04babf7d73370d39d19b33c2310571))

⚠ BREAKING CHANGE ⚠

`hibernate-core` is upgraded from `6.4.x` to `6.6.x`, which contains an important breaking change regarding type casts:

> `Expression.as()` doesn’t do anymore a real type conversions, it’s just an unsafe typecast on the Expression object itself.

Reference: https://docs.jboss.org/hibernate/orm/6.6/migration-guide/migration-guide.html#criteria-query

Note: this change is not compatible with older versions of Spring Boot, as `JpaExpression.cast()` was added in `hibernate-core@6.6`.



## [6.0.4](https://github.com/darrachequesne/spring-data-jpa-datatables/compare/v6.0.3...v6.0.4) (2024-04-03)


### Bug Fixes

* properly compute search panes with related entities ([495cfbc](https://github.com/darrachequesne/spring-data-jpa-datatables/commit/495cfbc4cf6e110bf7b6dcb47d7bfd8587056169))



## [6.0.3](https://github.com/darrachequesne/spring-data-jpa-datatables/compare/v6.0.2...v6.0.3) (2024-03-24)


### Bug Fixes

* **sqlserver:** prevent cast from NVARCHAR to VARCHAR ([f1e0ecd](https://github.com/darrachequesne/spring-data-jpa-datatables/commit/f1e0ecdcc73c3983683d4ddbcfe62fdc7862a70b))



## [6.0.2](https://github.com/darrachequesne/spring-data-jpa-datatables/compare/v6.0.1...v6.0.2) (2024-03-03)


### Bug Fixes

* allow order array to be empty ([a214d5b](https://github.com/darrachequesne/spring-data-jpa-datatables/commit/a214d5bb199fff4ccd578c3bbb71ee64f3a0f198))
* apply any prefiltering specification to the search panes ([e83b4d5](https://github.com/darrachequesne/spring-data-jpa-datatables/commit/e83b4d580c7cc021059c46322e99155051400214))



## [6.0.1](https://github.com/darrachequesne/spring-data-jpa-datatables/compare/v6.0.0...v6.0.1) (2023-02-12)


### Bug Fixes

* fix integration with Spring Boot 3 ([a6a8a0d](https://github.com/darrachequesne/spring-data-jpa-datatables/commit/a6a8a0d9d97919e8321927ac4f35078844cdfa26))



## [6.0.0](https://github.com/darrachequesne/spring-data-jpa-datatables/compare/v5.2.0...v6.0.0) (2023-01-02)


### Features

* upgrade to Spring Boot 3.0.0 ([d4c810e](https://github.com/darrachequesne/spring-data-jpa-datatables/commit/d4c810e0444556906b8639dead0861adea27ee69))



## [5.2.0](https://github.com/darrachequesne/spring-data-jpa-datatables/compare/v5.1.0...v5.2.0) (2022-05-19)


### Features

* log errors ([#144](https://github.com/darrachequesne/spring-data-jpa-datatables/issues/144)) ([d102cfa](https://github.com/darrachequesne/spring-data-jpa-datatables/commit/d102cfabc3a67b3dd1768e373e21f0855f94a43a))



## [5.1.0](https://github.com/darrachequesne/spring-data-jpa-datatables/compare/v5.0.0...v5.1.0) (2021-03-17)

### Features

* add support for the SearchPanes extension ([16803f9](https://github.com/darrachequesne/spring-data-jpa-datatables/commit/16803f9d1e4f8c8c7b128a55b0be96d8cec36382))



## [5.0.0](https://github.com/darrachequesne/spring-data-jpa-datatables/compare/v4.3...v5.0.0) (2018-03-01)


### BREAKING CHANGES

  * Update to spring boot 2.0.0 ([#73](https://github.com/darrachequesne/spring-data-jpa-datatables/pull/73))



## [4.3](https://github.com/darrachequesne/spring-data-jpa-datatables/compare/v4.2...v4.3) (2017-12-24)


### Bug Fixes

  *  Remove JOIN FETCH when using COUNT query ([#68](https://github.com/darrachequesne/spring-data-jpa-datatables/pull/68))



## [4.2](https://github.com/darrachequesne/spring-data-jpa-datatables/compare/v4.1...v4.2) (2017-12-24)


### Bug Fixes

  * Add proper JOIN FETCH clause ([#67](https://github.com/darrachequesne/spring-data-jpa-datatables/pull/67))
  * Remove column duplicates when using JOIN FETCH ([#64](https://github.com/darrachequesne/spring-data-jpa-datatables/pull/64))



## [4.1](https://github.com/darrachequesne/spring-data-jpa-datatables/compare/v4.0...v4.1) (2017-04-05)


### Bug Fixes

  * Fix searching with the separator "+" ([#55](https://github.com/darrachequesne/spring-data-jpa-datatables/pull/55))



## [4.0](https://github.com/darrachequesne/spring-data-jpa-datatables/compare/v3.1...v4.0) (2017-03-06)


### BREAKING CHANGES

  * Update bom to Brussels-RELEASE version ([#51](https://github.com/darrachequesne/spring-data-jpa-datatables/pull/51))



## [3.1](https://github.com/darrachequesne/spring-data-jpa-datatables/compare/v3.0...v3.1) (2016-12-16)


### Features

  * Add the ability to filter on NULL values ([#44](https://github.com/darrachequesne/spring-data-jpa-datatables/pull/44))



## [3.0](https://github.com/darrachequesne/spring-data-jpa-datatables/compare/v2.6...v3.0) (2016-11-19)


### Bug Fixes

  * Restrict eager loading to @OneToOne and @OneToMany relationships ([#39](https://github.com/darrachequesne/spring-data-jpa-datatables/pull/39))


2.6 / 2016-10-13
==================

  * Add tests for MySQL and PostgreSQL (#32)
  * Add tests for querydsl implementation (#33)
  * Update travis status badge to point towards master (#28)

2.5 / 2016-08-18
==================

  * Update the paging calculation (#24)

2.4 / 2016-08-14
==================

  * Add support for additional converter (#21)

2.3 / 2016-06-12
==================

  * Ensure related entities are eagerly loaded (#16)
  * Add some helpers and refactor tests (#15)
  * Add support for nested @ManyToOne relationships (#14)

2.2 / 2016-05-13
==================

  * Set an empty list as default value for output data
  * Fix for using @Embedded class (by @wimdeblauwe)

2.1 / 2016-04-09
==================

  * Add toString methods to mappings
  * Prevent unnecessary query when no results are found by the count query
  * Add an optional pre-filtering specification
  * Update code style
  * Fix string cast for QueryDSL predicates (fix #6)

2.0 / 2016-03-04
==================

  * Add support for QueryDSL

1.5 / 2016-03-01
==================

  * Add helper to get a map of the columns, indexed by name
  * Add escape character for LIKE clauses
  * Fix direction regexp

1.4 / 2016-02-02
==================

  * Fixed factory always generating dataTablesRepositories
  * Add JDK6 test back
