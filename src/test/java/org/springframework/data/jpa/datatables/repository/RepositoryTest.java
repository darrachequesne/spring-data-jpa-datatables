package org.springframework.data.jpa.datatables.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.Config;
import org.springframework.data.jpa.datatables.QConfig;
import org.springframework.data.jpa.datatables.qrepository.QDataTablesRepositoryImpl;
import org.springframework.data.jpa.datatables.qrepository.QEmployeeRepository;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Config.class, QConfig.class})
public class RepositoryTest {

  private @Autowired EmployeeRepository employeeRepository;
  private @Autowired QEmployeeRepository qEmployeeRepository;
  private @Autowired OfficeRepository officeRepository;

  @Test
  public void checkGeneratedRepositories() {
    assertThat(getTargetObject(employeeRepository)).isEqualTo(DataTablesRepositoryImpl.class);
    assertThat(getTargetObject(officeRepository)).isEqualTo(SimpleJpaRepository.class);
    assertThat(getTargetObject(qEmployeeRepository)).isEqualTo(QDataTablesRepositoryImpl.class);
  }

  // returns the class of the proxied object
  private Class getTargetObject(Object proxy) {
    return ((Advised) proxy).getTargetSource().getTargetClass();
  }

}
