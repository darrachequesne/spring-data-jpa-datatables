[![Build Status](https://api.travis-ci.org/darrachequesne/spring-data-jpa-datatables.svg)](https://travis-ci.org/darrachequesne/spring-data-jpa-datatables)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.darrachequesne/spring-data-jpa-datatables/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.darrachequesne/spring-data-jpa-datatables)

# spring-data-jpa-datatables
This project is an extension of the [Spring Data JPA](https://github.com/spring-projects/spring-data-jpa) project to ease its use with jQuery plugin [DataTables](http://datatables.net/) with **server-side processing enabled**.

This will allow you to handle the Ajax requests sent by DataTables for each draw of the information on the page (i.e. when paging, ordering, searching, etc.) from Spring **@RestController**.

## Maven dependency

```
<dependency>
	<groupId>com.github.darrachequesne</groupId>
	<artifactId>spring-data-jpa-datatables</artifactId>
	<version>1.3</version>
</dependency>
```

## How to use

Please see the [sample project](https://github.com/darrachequesne/spring-data-jpa-datatables-sample) for a complete example. 

#### 1. Enable the use of `DataTablesRepository` factory

With either
```
@EnableJpaRepositories(repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class)
```
or its XML counterpart
```
<jpa:repositories factory-class="org.springframework.data.jpa.datatables.repository.DataTablesRepositoryFactoryBean" />
```

You can restrict the scope of the factory with `@EnableJpaRepositories(repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class, basePackages = "my.package.for.datatables.repositories")`. In that case, only the repositories in the given package will be instantiated as `DataTablesRepositoryImpl` on run.

#### 2. Extend the DataTablesRepository interface

```
public interface UserRepository extends DataTablesRepository<User, Integer> {
  ...
}
```

The `DataTablesRepository` interface extends both `PagingAndSortingRepository` and `JpaSpecificationExecutor`.

#### 3. Expose your class' attributes

```
public class User {

	@JsonView(DataTablesOutput.View.class)
	private Integer id;

	@JsonView(DataTablesOutput.View.class)
	private String mail;

	// not exposed
	private String hiddenField;

	@ManyToOne
	@JoinColumn(name = "id_address")
	@JsonView(DataTablesOutput.View.class)
	private Address address;

}
```

#### 4. Include jquery.spring-friendly.js

It overrides jQuery data serialization to allow Spring MVC to correctly map input parameters (by changing `column[0][data]` to `column[0].data` in request payload)

#### On the server-side

The repositories now expose the following methods:
* `DataTablesOutput<T> findAll(DataTablesInput input);`
* `DataTablesOutput<T> findAll(DataTablesInput input, Specification<T> additionalSpecification);`

Your controllers should be able to handle the parameters sent by DataTables:

```
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
}
```

The `DataTablesInput` class maps the fields sent by the client (listed [there](https://datatables.net/manual/server-side)).

[Spring documentation](http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#specifications) for `Specification`

#### On the client-side

On the client-side, you can now define your table loading data dynamically :

```
$(document).ready(function() {
	var table = $('table#sample').DataTable({
		'ajax' : '/data/users',
		'serverSide' : true,
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

You can apply filters with `table.columns(<your column id>).search(<your filter>).draw();`
Supported filters:
* Strings (`WHERE <column> LIKE %<input>%`)
* Booleans
* Array of values (`WHERE <column> IN (<input>)` where input is something like 'PARAM1+PARAM2+PARAM4')

Also supports paging and sorting.
