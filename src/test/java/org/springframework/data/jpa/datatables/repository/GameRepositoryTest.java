package org.springframework.data.jpa.datatables.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.Config;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.model.Bill;
import org.springframework.data.jpa.datatables.model.Game;
import org.springframework.data.jpa.datatables.model.Prize;
import org.springframework.data.jpa.datatables.parameter.ColumnParameter;
import org.springframework.data.jpa.datatables.parameter.OrderParameter;
import org.springframework.data.jpa.datatables.parameter.SearchParameter;
import org.springframework.data.jpa.datatables.specification.PreFilteringSpecification;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Config.class)
public class GameRepositoryTest {

  @Autowired
  private GameRepository gameRepository;

  /**
   * Insert sample data at the beginning of all tests
   */
  @Before
  public void setUp() {
    if (gameRepository.count() > 0)
      return;
    for (int i = 0; i < 12; i++) {
      Game game = new Game(new Prize("prize" + i));
      gameRepository.save(game);
    }
  }

  @Test
  public void testWithoutFilter() {
    DataTablesInput input = getBasicInput();

    DataTablesOutput<Game> output = gameRepository.findAll(input);
    assertNotNull(output);
    assertNull(output.getError());
    assertEquals(12, (long) output.getRecordsFiltered());
    assertEquals(12, (long) output.getRecordsTotal());
  }

  @Test
  public void testSearchOnEmbeddedProperty() {
    DataTablesInput input = getBasicInput();

    input.getColumns().get(1).getSearch().setValue("prize1");
    DataTablesOutput<Game> output = gameRepository.findAll(input);
    assertNotNull(output);
    assertNull(output.getError());
    assertEquals(3, (long) output.getRecordsFiltered());
  }

  @Test
  public void testOrderOnEmbeddedProperty() {
    DataTablesInput input = getBasicInput();
    input.setOrder(new ArrayList<OrderParameter>());
    input.getOrder().add(new OrderParameter(1, "desc"));

    DataTablesOutput<Game> output = gameRepository.findAll(input);
    assertNotNull(output);
    assertNull(output.getError());
    assertEquals(12, (long) output.getRecordsFiltered());
    assertEquals("prize9", output.getData().get(0).getPrize().getName());
  }

  /**
   *
   * @return basic input parameters
   */
  private static DataTablesInput getBasicInput() {
    DataTablesInput input = new DataTablesInput();
    input.setDraw(1);
    input.setStart(0);
    input.setLength(10);
    input.setSearch(new SearchParameter("", false));
    input.setOrder(new ArrayList<OrderParameter>());
    input.getOrder().add(new OrderParameter(0, "asc"));

    input.setColumns(new ArrayList<ColumnParameter>());
    input.getColumns()
        .add(new ColumnParameter("id", "", true, true, new SearchParameter("", false)));
    input.getColumns()
        .add(new ColumnParameter("prize.name", "", true, true, new SearchParameter("", false)));

    return input;
  }
}
