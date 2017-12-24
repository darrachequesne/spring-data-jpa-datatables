
<a name="4.3"></a>
# [4.3](https://github.com/darrachequesne/spring-data-jpa-datatables/compare/v4.2...v4.3) / 2017-12-24


### Bug Fixes

  *  Remove JOIN FETCH when using COUNT query (#68)


<a name="4.2"></a>
# [4.2](https://github.com/darrachequesne/spring-data-jpa-datatables/compare/v4.1...v4.2) / 2017-12-24


### Bug Fixes

  * Add proper JOIN FETCH clause (#67)
  * Remove column duplicates when using JOIN FETCH (#64)


<a name="4.1"></a>
# [4.1](https://github.com/darrachequesne/spring-data-jpa-datatables/compare/v4.0...v4.1) / 2017-04-05


### Bug Fixes

  * Fix searching with the separator "+" (#55)


<a name="4.0"></a>
# [4.0](https://github.com/darrachequesne/spring-data-jpa-datatables/compare/v3.1...v4.0) / 2017-03-06


### BREAKING CHANGES

  * Update bom to Brussels-RELEASE version (#51)


<a name="3.1"></a>
# [3.1](https://github.com/darrachequesne/spring-data-jpa-datatables/compare/v3.0...v3.1) / 2016-12-16


### Features

  * Add the ability to filter on NULL values (#44)


<a name="3.0"></a>
# [3.0](https://github.com/darrachequesne/spring-data-jpa-datatables/compare/v2.6...v3.0) / 2016-11-19


### Bug Fixes

  * Restrict eager loading to @OneToOne and @OneToMany relationships (#39)


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
