package org.springframework.data.jpa.datatables.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.Config;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.model.Game;
import org.springframework.data.jpa.datatables.model.GameRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Config.class)
public class GameRepositoryTest {

  @Autowired
  private GameRepository gameRepository;

  @Test
  public void testWithoutFilter() {
    DataTablesInput input = getBasicInput();

    DataTablesOutput<Game> output = gameRepository.findAll(input);
    assertNotNull(output);
    assertNull(output.getError());
    assertEquals(12, output.getRecordsFiltered());
    assertEquals(12, output.getRecordsTotal());
  }

  @Test
  public void testSearchOnEmbeddedProperty() {
    DataTablesInput input = getBasicInput();

    input.getColumn("prize.name").setSearchValue("prize1");
    DataTablesOutput<Game> output = gameRepository.findAll(input);
    assertNotNull(output);
    assertNull(output.getError());
    assertEquals(3, output.getRecordsFiltered());
  }

  @Test
  public void testOrderOnEmbeddedProperty() {
    DataTablesInput input = getBasicInput();
    input.getOrder().clear();
    input.addOrder("prize.name", false);

    DataTablesOutput<Game> output = gameRepository.findAll(input);
    assertNotNull(output);
    assertNull(output.getError());
    assertEquals(12, output.getRecordsFiltered());
    assertEquals("prize9", output.getData().get(0).getPrize().getName());
  }

  /**
   *
   * @return basic input parameters
   */
  private static DataTablesInput getBasicInput() {
    DataTablesInput input = new DataTablesInput();
    input.addColumn("id", true, true, "");
    input.addColumn("prize.name", true, true, "");
    input.addOrder("id", true);
    return input;
  }
}
