package org.springframework.data.jpa.datatables.repository;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.Config;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Config.class)
public class RepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private HomeRepository homeRepository;

  @Test
  public void checkGeneratedRepositories() throws Exception {
    assertEquals(DataTablesRepositoryImpl.class, getTargetObject(userRepository));
    assertEquals(SimpleJpaRepository.class, getTargetObject(homeRepository));
  }

  // returns the class of the proxied object
  @SuppressWarnings("unchecked")
  protected <T> T getTargetObject(Object proxy) throws Exception {
    return (T) ((Advised) proxy).getTargetSource().getTargetClass();
  }

}
