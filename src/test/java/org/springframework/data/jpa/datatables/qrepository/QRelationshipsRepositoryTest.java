package org.springframework.data.jpa.datatables.qrepository;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.Config;
import org.springframework.data.jpa.datatables.QConfig;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.model.A;
import org.springframework.data.jpa.datatables.repository.RelationshipsRepositoryTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {Config.class, QConfig.class})
public class QRelationshipsRepositoryTest extends RelationshipsRepositoryTest {

  @Autowired
  private QRelationshipsRepository repository;

  @Override
  protected DataTablesOutput<A> getOutput(DataTablesInput input) {
    return repository.findAll(input);
  }

  @Test
  @Disabled
  @Override
  public void checkFetchJoin() {
    // see https://github.com/darrachequesne/spring-data-jpa-datatables/issues/7
  }
}