package org.springframework.data.jpa.datatables.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.Config;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.model.Bill;
import org.springframework.data.jpa.datatables.parameter.ColumnParameter;
import org.springframework.data.jpa.datatables.parameter.OrderParameter;
import org.springframework.data.jpa.datatables.parameter.SearchParameter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Config.class)
public class BillRepositoryTest {

	@Autowired
	private BillRepository billRepository;

	/**
	 * Insert sample data at the beginning of all tests
	 */
	@Before
	public void setUp() {
		if (billRepository.count() > 0)
			return;
		for (int i = 0; i < 12; i++) {
			Bill bill = new Bill();
			bill.setHasBeenPayed(i % 2 == 0);
			bill.setAmount((i + 1) * 100);
			billRepository.save(bill);
		}
	}

	@Test
	public void testWithoutFilter() {
		DataTablesInput input = getBasicInput();

		DataTablesOutput<Bill> output = billRepository.findAll(input);
		assertNotNull(output);
		assertNull(output.getError());
		assertEquals(12, (long) output.getRecordsFiltered());
		assertEquals(12, (long) output.getRecordsTotal());
	}

	@Test
	public void testBooleanFilter() {
		DataTablesInput input = getBasicInput();

		input.getColumns().get(2).getSearch().setValue("TRUE");
		DataTablesOutput<Bill> output = billRepository.findAll(input);
		assertNotNull(output);
		assertNull(output.getError());
		assertEquals(6, (long) output.getRecordsFiltered());
	}

	@Test
	public void testBooleanFilter2() {
		DataTablesInput input = getBasicInput();

		input.getColumns().get(2).getSearch().setValue("TRUE+FALSE");
		DataTablesOutput<Bill> output = billRepository.findAll(input);
		assertNotNull(output);
		assertNull(output.getError());
		assertEquals(12, (long) output.getRecordsFiltered());
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
		input.getColumns().add(
				new ColumnParameter("id", "", true, true, new SearchParameter(
						"", false)));
		input.getColumns().add(
				new ColumnParameter("amount", "", true, true,
						new SearchParameter("", false)));
		input.getColumns().add(
				new ColumnParameter("hasBeenPayed", "", true, true,
						new SearchParameter("", false)));

		return input;
	}
}