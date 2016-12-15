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
import org.springframework.data.jpa.datatables.model.Bill;
import org.springframework.data.jpa.datatables.model.BillRepository;
import org.springframework.data.jpa.datatables.specification.PreFilteringSpecification;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Config.class)
public class BillRepositoryTest {

  @Autowired
  private BillRepository billRepository;

  @Test
  public void testWithoutFilter() {
    DataTablesInput input = getBasicInput();

    DataTablesOutput<Bill> output = billRepository.findAll(input);
    assertNotNull(output);
    assertNull(output.getError());
    assertEquals(13, output.getRecordsFiltered());
    assertEquals(13, output.getRecordsTotal());
  }

  @Test
  public void testBooleanFilter() {
    DataTablesInput input = getBasicInput();

    input.getColumn("hasBeenPayed").setSearchValue("TRUE");
    DataTablesOutput<Bill> output = billRepository.findAll(input);
    assertNotNull(output);
    assertNull(output.getError());
    assertEquals(6, output.getRecordsFiltered());
  }

  @Test
  public void testBooleanFilterAndNull() {
    DataTablesInput input = getBasicInput();

    input.getColumn("hasBeenPayed").setSearchValue("TRUE+NULL");
    DataTablesOutput<Bill> output = billRepository.findAll(input);
    assertNotNull(output);
    assertNull(output.getError());
    assertEquals(7, output.getRecordsFiltered());
  }

  @Test
  public void testFilterIsNull() {
    DataTablesInput input = getBasicInput();

    input.getColumn("hasBeenPayed").setSearchValue("NULL");
    DataTablesOutput<Bill> output = billRepository.findAll(input);
    assertNotNull(output);
    assertNull(output.getError());
    assertEquals(1, output.getRecordsFiltered());
  }

  @Test
  public void testBooleanFilter2() {
    DataTablesInput input = getBasicInput();

    input.getColumn("hasBeenPayed").setSearchValue("TRUE+FALSE");
    DataTablesOutput<Bill> output = billRepository.findAll(input);
    assertNotNull(output);
    assertNull(output.getError());
    assertEquals(12, output.getRecordsFiltered());
  }

  @Test
  public void testEscapeCharacter() {
    DataTablesInput input = getBasicInput();

    input.getColumn("description").setSearchValue("foo%");
    DataTablesOutput<Bill> output = billRepository.findAll(input);
    assertNotNull(output);
    assertEquals(1, output.getRecordsFiltered());

    input.getColumn("description").setSearchValue("foo_");
    output = billRepository.findAll(input);
    assertNotNull(output);
    assertEquals(1, output.getRecordsFiltered());
  }

  @Test
  public void testWithPreFiltering() {
    DataTablesInput input = getBasicInput();

    DataTablesOutput<Bill> output =
        billRepository.findAll(input, null, new PreFilteringSpecification<Bill>());
    assertNotNull(output);
    assertEquals(6, output.getRecordsFiltered());
    assertEquals(6, output.getRecordsTotal());
  }

  /**
   * 
   * @return basic input parameters
   */
  private static DataTablesInput getBasicInput() {
    DataTablesInput input = new DataTablesInput();
    input.addColumn("id", true, true, "");
    input.addColumn("amount", true, true, "");
    input.addColumn("hasBeenPayed", true, true, "");
    input.addColumn("description", true, true, "");
    input.addOrder("id", true);
    return input;
  }
}
