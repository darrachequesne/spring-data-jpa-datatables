[![Build Status](https://github.com/darrachequesne/spring-data-jpa-datatables/workflows/CI/badge.svg?branch=main)](https://github.com/darrachequesne/spring-data-jpa-datatables/actions)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.darrachequesne/spring-data-jpa-datatables/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.darrachequesne/spring-data-jpa-datatables)

# spring-data-jpa-datatables

This project is an extension of the [Spring Data JPA](https://github.com/spring-projects/spring-data-jpa) project to ease its use with jQuery plugin [DataTables](http://datatables.net/) with **server-side processing enabled**.

This will allow you to handle the Ajax requests sent by DataTables for each draw of the information on the page (i.e. when paging, ordering, searching, etc.) from Spring **@RestController**.

For a MongoDB counterpart, please see [spring-data-mongodb-datatables](https://github.com/darrachequesne/spring-data-mongodb-datatables).

**Example:**

```java
@RestController
public class UserRestController {

  @Autowired
  private UserRepository userRepository;

  @RequestMapping(value = "/data/users", method = RequestMethod.GET)
  public DataTablesOutput<User> getUsers(@Valid DataTablesInput input) {
    return userRepository.findAll(input);
  }
}
```

![Example](https://user-images.githubusercontent.com/13031701/43364754-92f8de16-9320-11e8-9ee2-cc072e1eef8c.gif)


## Contents

- [Maven dependency](#maven-dependency)
- [Getting started](#getting-started)
  - [Step 1 - Enable the use of the `DataTablesRepository` factory](#step-1---enable-the-use-of-the-datatablesrepository-factory)
  - [Step 2 - Create a new entity](#step-2---create-a-new-entity)
  - [Step 3 - Extend the DataTablesRepository interface](#step-3---extend-the-datatablesrepository-interface)
  - [Step 4 - Use the repository in your controllers](#step-4---use-the-repository-in-your-controllers)
  - [Step 5 - On the client-side, create a new DataTable object](#step-5---on-the-client-side-create-a-new-datatable-object)
  - [Step 6 - Fix the serialization / deserialization of the query parameters](#step-6---fix-the-serialization--deserialization-of-the-query-parameters)
- [API](#api)
- [How to](#how-to)
  - [Apply filters](#apply-filters)
  - [Manage non-searchable fields](#manage-non-searchable-fields)
  - [Limit the exposed attributes of the entities](#limit-the-exposed-attributes-of-the-entities)
  - [Search on a rendered column](#search-on-a-rendered-column)
  - [Use with the SearchPanes extension](#use-with-the-searchpanes-extension)
  - [Fetch lazy fields](#fetch-lazy-fields)
  - [Search for a specific value in a column](#search-for-a-specific-value-in-a-column)
- [Examples of additional specification](#examples-of-additional-specification)
  - [Specific date](#specific-date)
  - [Range of integers](#range-of-integers)
  - [Range of dates](#range-of-dates)
- [Troubleshooting](#troubleshooting)

## Maven dependency

```xml
<dependency>
  <groupId>com.github.darrachequesne</groupId>
  <artifactId>spring-data-jpa-datatables</artifactId>
  <version>6.0.2</version>
</dependency>
```

Compatibility with Spring Boot:

| Version       | Spring Boot version                                                     |
|---------------|-------------------------------------------------------------------------|
| 4.x and below | 1                                                                       |
| 5.x           | 2                                                                       |
| 6.x           | [3](https://www.springcloud.io/post/2022-11/springboot3-upgrade-guide/) |


Back to [top](#contents).


## Getting started

Please see the [sample project](https://github.com/darrachequesne/spring-data-jpa-datatables-sample) for a complete example.

### Step 1 - Enable the use of the `DataTablesRepository` factory

With either

```java
@Configuration
@EnableJpaRepositories(repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class)
public class DataTablesConfiguration {}
```

or its XML counterpart

```xml
<jpa:repositories factory-class="org.springframework.data.jpa.datatables.repository.DataTablesRepositoryFactoryBean" />
```

You can restrict the scope of the factory with `@EnableJpaRepositories(repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class, basePackages = "my.package.for.datatables.repositories")`. In that case, only the repositories in the given package will be instantiated as `DataTablesRepositoryImpl` on run.

```java
@Configuration
@EnableJpaRepositories(basePackages = "my.default.package")
public class DefaultJpaConfiguration {}

@Configuration
@EnableJpaRepositories(repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class, basePackages = "my.package.for.datatables.repositories")
public class DataTablesConfiguration {}
```

### Step 2 - Create a new entity

```java
@Entity
public class User {

  private Integer id;

  private String mail;

  @ManyToOne
  @JoinColumn(name = "id_address")
  private Address address;

}
```

### Step 3 - Extend the DataTablesRepository interface

```java
public interface UserRepository extends DataTablesRepository<User, Integer> {}
```

The `DataTablesRepository` interface extends both [PagingAndSortingRepository](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/PagingAndSortingRepository.html) and [JpaSpecificationExecutor](https://docs.spring.io/spring-data/jpa/docs/current/api/org/springframework/data/jpa/repository/JpaSpecificationExecutor.html).

### Step 4 - Use the repository in your controllers

```java
@RestController
@RequiredArgsConstructor
public class MyController {
  private final UserRepository userRepository;

  @RequestMapping(value = "/data/users", method = RequestMethod.GET)
  public DataTablesOutput<User> getUsers(@Valid DataTablesInput input) {
    return userRepository.findAll(input);
  }
}
```

### Step 5 - On the client-side, create a new DataTable object

```javascript
$(document).ready(function() {
  var table = $('table#sample').DataTable({
    ajax : '/data/users',
    serverSide : true,
    columns : [{
      data : 'id'
    }, {
      data : 'mail'
    }, {
      data : 'address.town',
      render: function (data, type, row) {
        return data || '';
      }
    }]
  });
}
```

### Step 6 - Fix the serialization / deserialization of the query parameters

By default, the [parameters](https://datatables.net/manual/server-side#Sent-parameters) sent by the plugin cannot be deserialized by Spring MVC and will throw the following exception: `InvalidPropertyException: Invalid property 'columns[0][data]' of bean class [org.springframework.data.jpa.datatables.mapping.DataTablesInput]`.

There are multiple solutions to this issue:

- [Solution n°1 - custom serialization](#solution-n1---custom-serialization)
- [Solution n°2 - POST requests](#solution-n2---post-requests)
- [Solution n°3 - manual serialization](#solution-n3---manual-serialization)

#### Solution n°1 - custom serialization

You need to include the [jquery.spring-friendly.js](jquery.spring-friendly.js) file found at the root of the repository.

```html
<script src="jquery.spring-friendly.js" />
```

It overrides the default serialization of the HTTP request parameters to allow Spring MVC to correctly map them, by changing `column[0][data]` into `column[0].data` in the request payload.

#### Solution n°2 - POST requests

Client-side:

```javascript
$('table#sample').DataTable({
  'ajax': {
    'contentType': 'application/json',
    'url': '/data/users',
    'type': 'POST',
    'data': function(d) {
      return JSON.stringify(d);
    }
  }
})
```

Server-side:

```java
@RestController
public class MyController {
  @RequestMapping(value = '/data/users', method = RequestMethod.POST)
  public DataTablesOutput<User> getUsers(@Valid @RequestBody DataTablesInput input) {
    return userRepository.findAll(input);
  }
}
```

#### Solution n°3 - manual serialization

```javascript

function flatten(params) {
  params.columns.forEach(function (column, index) {
    params['columns[' + index + '].data'] = column.data;
    params['columns[' + index + '].name'] = column.name;
    params['columns[' + index + '].searchable'] = column.searchable;
    params['columns[' + index + '].orderable'] = column.orderable;
    params['columns[' + index + '].search.regex'] = column.search.regex;
    params['columns[' + index + '].search.value'] = column.search.value;
  });
  delete params.columns;

  params.order.forEach(function (order, index) {
    params['order[' + index + '].column'] = order.column;
    params['order[' + index + '].dir'] = order.dir;
  });
  delete params.order;

  params['search.regex'] = params.search.regex;
  params['search.value'] = params.search.value;
  delete params.search;

  return params;
}

$('table#sample').DataTable({
  'ajax': {
    'url': '/data/users',
    'type': 'GET',
    'data': flatten
  }
})
```

Back to [top](#contents).


## API

The repositories now expose the following methods:

```java
public interface DataTablesRepository<T, ID extends Serializable> {

    DataTablesOutput<T> findAll(
        DataTablesInput input
    );

    DataTablesOutput<R> findAll(
        DataTablesInput input,
        Function<T, R> converter
    );

    DataTablesOutput<T> findAll(
        DataTablesInput input,
        Specification<T> additionalSpecification
    );

    DataTablesOutput<T> findAll(
        DataTablesInput input,
        Specification<T> additionalSpecification,
        Specification<T> preFilteringSpecification
    );

    DataTablesOutput<R> findAll(
        DataTablesInput input,
        Specification<T> additionalSpecification,
        Specification<T> preFilteringSpecification,
        Function<T, R> converter
    );
}
```

**Note**: since version 2.0, QueryDSL is also supported:
* replace `DataTablesRepositoryFactoryBean` with `QDataTablesRepositoryFactoryBean`
* replace `DataTablesRepository` with `QDataTablesRepository`

and your repositories will now expose:

```java
public interface QDataTablesRepository<T, ID extends Serializable> {

    DataTablesOutput<T> findAll(
        DataTablesInput input
    );

    DataTablesOutput<R> findAll(
        DataTablesInput input,
        Function<T, R> converter
    );

    DataTablesOutput<T> findAll(
        DataTablesInput input,
        Predicate additionalPredicate
    );

    DataTablesOutput<T> findAll(
        DataTablesInput input,
        Predicate additionalPredicate,
        Predicate preFilteringPredicate
    );

    DataTablesOutput<R> findAll(
        DataTablesInput input,
        Predicate additionalPredicate,
        Predicate preFilteringPredicate,
        Function<T, R> converter
    );
}
```

Your controllers should be able to handle the parameters sent by DataTables:

```java
@RestController
public class UserRestController {

  @Autowired
  private UserRepository userRepository;

  @RequestMapping(value = "/data/users", method = RequestMethod.GET)
  public DataTablesOutput<User> getUsers(@Valid DataTablesInput input) {
    return userRepository.findAll(input);
  }

  // or with some preprocessing
  @RequestMapping(value = "/data/users", method = RequestMethod.GET)
  public DataTablesOutput<User> getUsers(@Valid DataTablesInput input) {
    ColumnParameter parameter0 = input.getColumns().get(0);
    Specification additionalSpecification = getAdditionalSpecification(parameter0.getSearch().getValue());
    parameter0.getSearch().setValue("");
    return userRepository.findAll(input, additionalSpecification);
  }

  // or with an additional filter allowing to 'hide' data from the client (the filter will be applied on both the count and the data queries, and may impact the recordsTotal in the output)
  @RequestMapping(value = "/data/users", method = RequestMethod.GET)
  public DataTablesOutput<User> getUsers(@Valid DataTablesInput input) {
    return userRepository.findAll(input, null, removeHiddenEntitiesSpecification);
  }
}
```

The `DataTablesInput` class maps the fields sent by the client (listed [there](https://datatables.net/manual/server-side)).

[Spring documentation](http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#specifications) for `Specification`

## How to

### Apply filters

By default, the main search field is applied to all columns.

You can apply specific filter on a column with `table.columns(<your column id>).search(<your filter>).draw();` (or `table.columns(<your column name>:name)...`) (see [documentation](https://datatables.net/reference/api/columns().search())).

**Supported filters:**

* Strings (`WHERE <column> LIKE %<input>%`)
* Booleans
* Array of values (`WHERE <column> IN (<input>)` where input is something like 'PARAM1+PARAM2+PARAM4')
* `NULL` values are also supported: 'PARAM1+PARAM3+NULL' becomes `WHERE (<column> IN ('PARAM1', 'PARAM3') OR <column> IS NULL)` (to actually search for 'NULL' string, please use `\NULL`)

Also supports paging and sorting.

**Example:**

```
{
  "draw": 1,
  "columns": [
    {
      "data": "id",
      "name": "",
      "searchable": true,
      "orderable": true,
      "search": {
        "value": "",
        "regex": false
      }
    },
    {
      "data": "firstName",
      "name": "",
      "searchable": true,
      "orderable": true,
      "search": {
        "value": "",
        "regex": false
      }
    },
    {
      "data": "lastName",
      "name": "",
      "searchable": true,
      "orderable": true,
      "search": {
        "value": "",
        "regex": false
      }
    }
  ],
  "order": [
    {
      "column": 0,
      "dir": "asc"
    }
  ],
  "start": 0,
  "length": 10,
  "search": {
    "value": "john",
    "regex": false
  }
}
```

is converted into the following SQL (through the [Criteria API](https://www.objectdb.com/java/jpa/query/criteria)):

```sql
SELECT
    user0_.id AS id1_0_0_,
    user0_.first_name AS first_na3_0_0_,
    user0_.last_name AS last_nam4_0_0_
FROM
    users user0_
WHERE
    user0_.id LIKE "%john%"
    OR user0_.first_name LIKE "%john%"
    OR user0_.last_name LIKE "%john%"
ORDER BY user0_.id ASC
LIMIT 10
```

**Note**: the `regex` flag is currently ignored because JPQL only supports `LIKE` expressions (with `%` and `_` tokens).

Yet you should be able to use the DBMS-specific regex operator with the `CriteriaBuilder.function()` method.

Example with H2 [REGEXP_LIKE](http://www.h2database.com/html/functions.html#regexp_like):

```java
Column column = input.getColumn("my_column");
column.setSearchable(false); // so the default filter will not be applied
String regexValue = column.getSearch().getValue();
DataTablesOutput<...> output = repository.findAll(input, (root, query, builder) -> {
  Expression<String> regex = builder.function("REGEXP_LIKE", String.class, root.get("my_column"), builder.literal(regexValue));
  return builder.equal(regex, builder.literal(1));
});
```

### Manage non-searchable fields

If you have a column that does not match an attribute on the server-side (for example, an 'Edit' button), you'll have to set the [searchable](https://datatables.net/reference/option/columns.searchable) and [orderable](https://datatables.net/reference/option/columns.orderable) attributes to `false`.

```javascript
$(document).ready(function() {
  var table = $('table#sample').DataTable({
    'ajax' : '/data/users',
    'serverSide' : true,
    columns : [{
      data: 'id'
    }, {
      data: 'mail'
    }, {
      searchable: false,
      orderable: false
    }]
  });
});
```

### Limit the exposed attributes of the entities

There are several ways to restrict the attributes of an entity on the server-side:

- [with a DTO](#with-a-dto)
- [with `@JsonView`](#with-jsonview)
- [with `@JsonIgnore`](#with-jsonignore)

#### With a DTO

```java
@RestController
public class UserRestController {

  @Autowired
  private UserRepository userRepository;

  @RequestMapping(value = "/data/users", method = RequestMethod.GET)
  public DataTablesOutput<UserDTO> getUsers(@Valid DataTablesInput input) {
    return userRepository.findAll(input, toUserDTO);
  }
}
```

The `toUserDTO()` method converts the `User` entity into a `UserDTO` object. You can also use a mapping framework such as [Orika](https://github.com/orika-mapper/orika) or [MapStruct](https://mapstruct.org/).

#### With `@JsonView`

```java
@Entity
public class User {

  @JsonView(DataTablesOutput.View.class)
  private Integer id;

  // ignored
  private String mail;

}

@RestController
public class UserRestController {

  @Autowired
  private UserRepository userRepository;

  @JsonView(DataTablesOutput.View.class)
  @RequestMapping(value = "/data/users", method = RequestMethod.GET)
  public DataTablesOutput<User> getUsers(@Valid DataTablesInput input) {
    return userRepository.findAll(input);
  }
}

```

#### With `@JsonIgnore`

```java
@Entity
public class User {

  private Integer id;

  @JsonIgnore
  private String mail;

}
```

### Search on a rendered column

Let's say you have an `User` entity with two attributes, `firstName` and `lastName`.

To display the rendered column on the client-side:

```js
$('table#sample').DataTable({
  ajax: '/data/users',
  serverSide: true,
  columns : [
    {
      data: 'fullName',
      render: (_, __, row) => `${row.firstName} ${row.lastName}`,
      searchable: false,
      orderable: false
    }
  ]
});
```

Both `searchable` and `orderable` option are necessary, because the `User` entity has no`fullName` attribute.

To filter on the server-side, you'll have to manually create the matching specification:

```java
@RequestMapping(value = "/data/users", method = RequestMethod.GET)
public DataTablesOutput<User> list(@Valid DataTablesInput input) {
    String searchValue = escapeContent(input.getSearch().getValue());
    input.getSearch().setValue(""); // prevent search on other fields

    Specification<User> fullNameSpecification = (Specification<User>) (root, query, criteriaBuilder) -> {
        if (!hasText(searchValue)) {
            return null;
        }
        String[] parts = searchValue.split(" ");
        Expression<String> firstNameExpression = criteriaBuilder.lower(root.get("firstName"));
        Expression<String> lastNameExpression = criteriaBuilder.lower(root.get("lastName"));
        if (parts.length == 2 && hasText(parts[0]) && hasText(parts[1])) {
            return criteriaBuilder.or(
                    criteriaBuilder.and(
                            criteriaBuilder.equal(firstNameExpression, parts[0]),
                            criteriaBuilder.like(lastNameExpression, parts[1] + "%", '~')
                    ),
                    criteriaBuilder.and(
                            criteriaBuilder.equal(lastNameExpression, parts[0]),
                            criteriaBuilder.like(firstNameExpression, parts[1] + "%", '~')
                    )
            );
        } else {
            return criteriaBuilder.or(
                    criteriaBuilder.like(firstNameExpression, searchValue + "%", '~'),
                    criteriaBuilder.like(lastNameExpression, searchValue + "%", '~')
            );
        }
    };
    return userRepository.findAll(input, fullNameSpecification);
}

private String escapeContent(String content) {
    return content
            .replaceAll("~", "~~")
            .replaceAll("%", "~%")
            .replaceAll("_", "~_")
            .trim()
            .toLowerCase();
}
```

You can find a complete example [here](https://github.com/darrachequesne/spring-data-jpa-datatables-sample).

Back to [top](#contents).

### Use with the SearchPanes extension

Server-side:

```java
@RestController
@RequiredArgsConstructor
public class UserRestController {
  private final UserRepository userRepository;

  @RequestMapping(value = "/data/users", method = RequestMethod.GET)
  public DataTablesOutput<User> getUsers(@Valid DataTablesInput input, @RequestParam Map<String, String> queryParams) {
    input.parseSearchPanesFromQueryParams(queryParams, Arrays.asList("position", "status"));
    return userRepository.findAll(input);
  }
}
```

Client-side:

```js
$(document).ready(function() {
  var table = $('table#sample').DataTable({
    ajax : '/data/users',
    serverSide: true,
    dom: 'Pfrtip',
    columns : [{
      data : 'id'
    }, {
      data : 'mail'
    }, {
      data : 'position'
    }, {
      data : 'status'
    }]
  });
}
```

Regarding the deserialization issue detailed [above](#5-fix-the-serialization--deserialization-of-the-query-parameters), here is the compatibility matrix:

| Solution                                                    | Compatibility with the SearchPanes extension |
|-------------------------------------------------------------|----------------------------------------------|
| [Custom serialization](#solution-n1---custom-serialization) | YES                                          |
| [POST requests](#solution-n2---post-requests)               | NO                                           |
| [Manual serialization](#solution-n3---manual-serialization) | NO                                           |

### Fetch lazy fields

Fields marked with `FetchType.LAZY` can be manually fetched with an additional specification:

```java
@RestController
public class MyController {

  @RequestMapping(value = "/entities", method = RequestMethod.GET)
  public DataTablesOutput<MyEntity> list(@Valid DataTablesInput input) {
    return myRepository.findAll(input, (root, query, criteriaBuilder) -> {
      if (query.getResultType() != Long.class) {
        root.fetch("relatedEntity", JoinType.LEFT);
      }
      return null;
    });
  }
}
```

**Important**: trying to load a `@OneToMany` or `@ManyToMany` relationship with this method will result in the following warning by Hibernate:

```
firstResult/maxResults specified with collection fetch; applying in memory
```

### Search for a specific value in a column

By default, adding a search value will be converted to a `WHERE <column> LIKE %<value>%` clause.

You can have an exact match (`WHERE <column> IN (<values>)`) with:

```js
// single value
myTable.column($columnIndex).search("value1+").draw();

// multiple values
myTable.column($columnIndex).search("value1+value2+value3").draw();
```

Note: the column in the database will be inferred as a string. For more complex use cases, you will need a proper specification.


## Examples of additional specification

- [Specific date](#specific-date)
- [Range of integers](#range-of-integers)
- [Range of dates](#range-of-dates)

### Specific date

```java
class DateSpecification implements Specification<MyEntity> {
  private final LocalDate value;

  DateSpecification(Column column) {
    String value = column.getSearch().getValue();
    column.setSearchable(false); // either here or in the table definition
    this.value = parseValue(value);
  }

  private LocalDate parseValue(String value) {
    if (hasText(value)) {
      try {
        return LocalDate.parse(value);
      } catch (DateTimeParseException e) {
        return null;
      }
    }
    return null;
  }

  @Override
  public Predicate toPredicate(Root<MyEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
    Expression<LocalDate> expr = root.get("myColumn").as(LocalDate.class);
    if (this.value != null) {
      return criteriaBuilder.equal(expr, this.value);
    } else {
      return criteriaBuilder.conjunction();
    }
  }
}
```

And then:

```java
@RestController
public class MyController {

  @RequestMapping(value = "/entities", method = RequestMethod.GET)
  public DataTablesOutput<MyEntity> list(@Valid DataTablesInput input) {
    return myRepository.findAll(input, new DateSpecification(input.getColumn("myField")));
  }
}
```

### Range of integers

```java
class IntegerRangeSpecification implements Specification<MyEntity> {
  private final Integer minValue;
  private final Integer maxValue;

  IntegerRangeSpecification(Column column) {
    String value = column.getSearch().getValue();
    column.setSearchable(false); // either here or in the table definition
    if (!hasText(value)) {
      minValue = maxValue = null;
      return;
    }
    String[] bounds = value.split(";");
    minValue = parseValue(bounds, 0);
    maxValue = parseValue(bounds, 1);
  }

  private Integer parseValue(String[] bounds, int index) {
    if (bounds.length > index && hasText(bounds[index])) {
      try {
        return Integer.valueOf(bounds[index]);
      } catch (NumberFormatException e) {
        return null;
      }
    }
    return null;
  }

  @Override
  public Predicate toPredicate(Root<MyEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
    Expression<Integer> expr = root.get("myColumn").as(Integer.class);
    if (this.minValue != null && this.maxValue != null) {
      return criteriaBuilder.between(expr, this.minValue, this.maxValue);
    } else if (this.minValue != null) {
      return criteriaBuilder.greaterThanOrEqualTo(expr, this.minValue);
    } else if (this.maxValue != null) {
      return criteriaBuilder.lessThanOrEqualTo(expr, this.maxValue);
    } else {
      return criteriaBuilder.conjunction();
    }
  }
}
```

And then:

```java
@RestController
public class MyController {

  @RequestMapping(value = "/entities", method = RequestMethod.GET)
  public DataTablesOutput<MyEntity> list(@Valid DataTablesInput input) {
    return myRepository.findAll(input, new IntegerRangeSpecification(input.getColumn("myField")));
  }
}
```

With two text inputs on the client side:

```js
const minValueInput = $("input#minValue");
const maxValueInput = $("input#maxValue");

const onBoundChange = () => {
  table.column($columnIndex).search(minValueInput.val() + ';' + maxValueInput.val()).draw();
};

minValueInput.on("input", onBoundChange);
maxValueInput.on("input", onBoundChange);
```

### Range of dates

```java
class DateRangeSpecification implements Specification<MyEntity> {
  private final LocalDate minValue;
  private final LocalDate maxValue;

  DateRangeSpecification(Column column) {
    String value = column.getSearch().getValue();
    column.setSearchable(false); // either here or in the table definition
    if (!hasText(value)) {
      minValue = maxValue = null;
      return;
    }
    String[] bounds = value.split(";");
    minValue = parseValue(bounds, 0);
    maxValue = parseValue(bounds, 1);
  }

  private LocalDate parseValue(String[] bounds, int index) {
    if (bounds.length > index && hasText(bounds[index])) {
      try {
        return LocalDate.parse(bounds[index]);
      } catch (DateTimeParseException e) {
        return null;
      }
    }
    return null;
  }

  @Override
  public Predicate toPredicate(Root<MyEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
    Expression<LocalDate> expr = root.get("myColumn").as(LocalDate.class);
    if (this.minValue != null && this.maxValue != null) {
      return criteriaBuilder.between(expr, this.minValue, this.maxValue);
    } else if (minValue != null) {
      return criteriaBuilder.greaterThanOrEqualTo(expr, this.minValue);
    } else if (maxValue != null) {
      return criteriaBuilder.lessThanOrEqualTo(expr, this.maxValue);
    } else {
      return criteriaBuilder.conjunction();
    }
  }
}
```

And then:

```java
@RestController
public class MyController {

  @RequestMapping(value = "/entities", method = RequestMethod.GET)
  public DataTablesOutput<MyEntity> list(@Valid DataTablesInput input) {
    return myRepository.findAll(input, new DateRangeSpecification(input.getColumn("myField")));
  }
}
```

Back to [top](#contents).


## Troubleshooting

- `Invalid property 'columns[0][data]' of bean class [org.springframework.data.jpa.datatables.mapping.DataTablesInput]`

Please see [here](#5-fix-the-serialization--deserialization-of-the-query-parameters).

- `java.lang.IllegalArgumentException: Unable to locate Attribute with the the given name ...`

It seems you have a column with a `data` attribute that does not match the attribute of the `@Entity` on the server-side.

Please see [here](#manage-non-searchable-fields).

- `java.lang.NoClassDefFoundError: org/hibernate/jpa/criteria/path/AbstractPathImpl`

The versions `>= 5.0.0` of the library are not compatible with Spring 4 (Spring Boot 1.x), please use the previous versions.


Back to [top](#contents).
