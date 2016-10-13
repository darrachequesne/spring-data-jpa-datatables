package org.springframework.data.jpa.datatables.qrepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.Config;
import org.springframework.data.jpa.datatables.QConfig;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.model.Lesson;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Config.class, QConfig.class})
public class QLessonRepositoryTest {

  @Autowired
  private QLessonRepository lessonRepository;

  @Autowired
  private SessionFactory sessionFactory;

  @Test
  public void testThroughTwoManyToOneRelationships() {
    DataTablesInput input = getBasicInput();

    input.getColumn("course.type.name").setSearchValue("CourseTypeA");
    DataTablesOutput<Lesson> output = lessonRepository.findAll(input);
    assertNotNull(output);
    assertNull(output.getError());
    assertEquals(5, output.getRecordsFiltered());
    assertEquals(7, output.getRecordsTotal());

    input.getColumn("course.name").setSearchValue("CourseA-2");
    output = lessonRepository.findAll(input);
    assertNotNull(output);
    assertNull(output.getError());
    assertEquals(2, output.getRecordsFiltered());
    assertEquals(7, output.getRecordsTotal());
  }

  @Test
  @Ignore
  public void testEagerLoading() {
    DataTablesInput input = getBasicInput();

    Statistics statistics = sessionFactory.getStatistics();
    statistics.setStatisticsEnabled(true);
    DataTablesOutput<Lesson> output = lessonRepository.findAll(input);
    assertEquals("CourseTypeA", output.getData().get(0).getCourse().getType().getName());
    statistics.setStatisticsEnabled(false);

    // there should be only three executed queries : count unfiltered, count filtered and actual
    // data (with FETCH JOIN)
    assertEquals(3, statistics.getPrepareStatementCount());
    assertEquals(7 + 3 + 2, statistics.getEntityLoadCount());
  }

  /**
   * 
   * @return basic input parameters
   */
  private static DataTablesInput getBasicInput() {
    DataTablesInput input = new DataTablesInput();
    input.addColumn("id", true, true, "");
    input.addColumn("name", true, true, "");
    input.addColumn("course.name", true, true, "");
    input.addColumn("course.type.name", true, true, "");
    input.addOrder("id", true);
    return input;
  }
}
