package org.springframework.data.jpa.datatables.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.Config;
import org.springframework.data.jpa.datatables.QConfig;
import org.springframework.data.jpa.datatables.qrepository.QEmployeeRepository;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {Config.class, QConfig.class})
class RepositoryTest {

  private @Autowired EmployeeRepository employeeRepository;
  private @Autowired QEmployeeRepository qEmployeeRepository;
  private @Autowired OfficeRepository officeRepository;

  @Test
  void checkGeneratedRepositories() {
    // we now use fragments to implement the DataTables functionality, so all beans extends the same class
    // reference: https://docs.spring.io/spring-data/jpa/reference/data-commons/repositories/custom-implementations.html
    assertThat(getTargetObject(employeeRepository)).isEqualTo(SimpleJpaRepository.class);
    assertThat(getTargetObject(officeRepository)).isEqualTo(SimpleJpaRepository.class);
    assertThat(getTargetObject(qEmployeeRepository)).isEqualTo(SimpleJpaRepository.class);
  }

  // returns the class of the proxied object
  private Class<?> getTargetObject(Object proxy) {
    return ((Advised) proxy).getTargetSource().getTargetClass();
  }

}
