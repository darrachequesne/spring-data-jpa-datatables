# History

- [6.0.2](#602-2024-03-03) (Mar 2024)
- [6.0.1](#601-2023-02-12) (Feb 2023)
- [6.0.0](#600-2023-01-02) (Jan 2023)
- [5.2.0](#520-2022-05-19) (May 2022)
- [5.1.0](#510-2021-03-17) (Mar 2021)
- [5.0.0](#500-2018-03-01) (Mar 2018)
- [4.3](#43-2017-12-24) (Dec 2017)
- [4.2](#42-2017-12-24) (Dec 2017)
- [4.1](#41-2017-04-05) (Apr 2017)
- [4.0](#40-2017-03-06) (Mar 2017)
- [3.1](#31-2016-12-16) (Dec 2016)
- [3.0](#30-2016-11-19) (Nov 2016)



# Release notes

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
