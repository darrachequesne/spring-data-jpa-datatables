package org.springframework.data.jpa.datatables.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.Config;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.model.Lesson;
import org.springframework.data.jpa.datatables.model.LessonRepository;
import org.springframework.data.jpa.datatables.parameter.ColumnParameter;
import org.springframework.data.jpa.datatables.parameter.OrderParameter;
import org.springframework.data.jpa.datatables.parameter.SearchParameter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Config.class)
public class LessonRepositoryTest {

  @Autowired
  private LessonRepository lessonRepository;

  @Test
  public void testThroughTwoManyToOneRelationships() {
    DataTablesInput input = getBasicInput();

    input.getColumns().get(3).getSearch().setValue("CourseTypeA");
    DataTablesOutput<Lesson> output = lessonRepository.findAll(input);
    assertNotNull(output);
    assertNull(output.getError());
    assertEquals(5, (long) output.getRecordsFiltered());
    assertEquals(7, (long) output.getRecordsTotal());

    input.getColumns().get(2).getSearch().setValue("CourseA-2");
    output = lessonRepository.findAll(input);
    assertNotNull(output);
    assertNull(output.getError());
    assertEquals(2, (long) output.getRecordsFiltered());
    assertEquals(7, (long) output.getRecordsTotal());
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
        .add(new ColumnParameter("name", "", true, true, new SearchParameter("", false)));
    input.getColumns()
        .add(new ColumnParameter("course.name", "", true, true, new SearchParameter("", false)));
    input.getColumns().add(
        new ColumnParameter("course.type.name", "", true, true, new SearchParameter("", false)));

    return input;
  }
}
