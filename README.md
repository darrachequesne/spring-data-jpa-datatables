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
	<version>1.2</version>
</dependency>
```

## How to use

Please see the [sample project](https://github.com/darrachequesne/spring-data-jpa-datatables-sample) for a complete example. 

1/ Enable the use of DataTablesRepository factory with either
```
@EnableJpaRepositories(repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class)
```
or 
```
<jpa:repositories factory-class="org.springframework.data.jpa.datatables.repository.DataTablesRepositoryFactoryBean" />
```

2/ Make your repositories extend the interface DataTablesRepository :

```
public interface UserRepository extends DataTablesRepository<User, Integer> {
  ...
}
```

3/ Expose your class' attributes according to your needs with ```@JsonView(DataTablesOutput.View.class)```. Please check the sample class [User](https://github.com/darrachequesne/spring-data-jpa-datatables-sample/blob/master/src/main/java/sample/model/User.java).

4/ The repositories now expose the following method : ```DataTablesOutput<T> findAll(DataTablesInput input);```. Your controllers should be able to handle the parameters sent by DataTables

```
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

5/ And last but not least, do not forget to include the JavaScript file jquery.spring-friendly.js, overriding jQuery data serialization to allow Spring MVC to correctly map input parameters (changing column[0]\[data] to column[0].data)


On the client-side, you can now define your table loading data dynamically :

```
$(document).ready(function() {
	var table = $('table#sample').DataTable({
		'ajax' : '/data/users',
		'serverSide' : true,
		columns : [ ... ]
	});
}
```

Features :
- Paging
- Ordering
- Searching by global value (LIKE clause) or per-column (LIKE or IN clause)
