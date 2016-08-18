
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
