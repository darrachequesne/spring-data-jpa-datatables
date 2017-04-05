package org.springframework.data.jpa.datatables.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.jpa.datatables.Config;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.model.User;
import org.springframework.data.jpa.datatables.model.UserDto;
import org.springframework.data.jpa.datatables.model.UserRepository;
import org.springframework.data.jpa.datatables.specification.TestSpecification;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Config.class)
public class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Test
  public void testSort() {
    DataTablesInput input = getBasicInput();

    // sorting by id asc
    DataTablesOutput<User> output = userRepository.findAll(input);
    assertNotNull(output);
    assertEquals(1, (int) output.getDraw());
    assertNull(output.getError());
    assertEquals(24, output.getRecordsFiltered());
    assertEquals(24, output.getRecordsTotal());

    List<User> users = output.getData();
    assertNotNull(users);
    assertEquals(10, users.size());

    User firstUser = users.get(0);
    User lastUser = users.get(9);
    assertEquals(1, (int) firstUser.getId());
    assertEquals(10, (int) lastUser.getId());

    // sorting by id desc
    input.setDraw(2);
    input.getOrder().clear();
    input.addOrder("id", false);
    output = userRepository.findAll(input);
    assertNotNull(output);
    assertEquals(2, (int) output.getDraw());
    assertNull(output.getError());
    assertEquals(24, output.getRecordsFiltered());
    assertEquals(24, output.getRecordsTotal());

    users = output.getData();
    assertNotNull(users);
    assertEquals(10, users.size());

    firstUser = users.get(0);
    lastUser = users.get(9);
    assertEquals(24, (int) firstUser.getId());
    assertEquals(15, (int) lastUser.getId());
  }

  @Test
  public void testMultipleSort() {
    DataTablesInput input = getBasicInput();

    input.getOrder().clear();
    input.addOrder("status", false);
    input.addOrder("id", true);

    // sorting by id asc and status desc
    DataTablesOutput<User> output = userRepository.findAll(input);
    assertNotNull(output);
    List<User> users = output.getData();
    assertNotNull(users);
    assertEquals(10, users.size());

    User firstUser = users.get(0);
    User lastUser = users.get(9);
    assertEquals(2, (int) firstUser.getId());
    assertEquals(20, (int) lastUser.getId());
  }

  @Test
  public void testWithoutSort() {
    DataTablesInput input = getBasicInput();

    input.getOrder().clear();

    DataTablesOutput<User> output = userRepository.findAll(input);
    assertNotNull(output);
    assertNull(output.getError());
  }

  @Test
  public void testFilterGlobal() {
    DataTablesInput input = getBasicInput();

    input.getSearch().setValue("hn1");

    DataTablesOutput<User> output = userRepository.findAll(input);
    assertNotNull(output);
    assertEquals(1, output.getDraw());
    assertNull(output.getError());
    assertEquals(11, output.getRecordsFiltered());
    assertEquals(24, output.getRecordsTotal());

    List<User> users = output.getData();
    assertNotNull(users);
    assertEquals(10, users.size());

    User firstUser = users.get(0);
    User lastUser = users.get(9);
    assertEquals("john1", firstUser.getUsername());
    assertEquals("john18", lastUser.getUsername());
  }

  @Test
  public void testFilterGlobalIgnoreCase() {
    DataTablesInput input = getBasicInput();

    input.getSearch().setValue("OhN1");

    DataTablesOutput<User> output = userRepository.findAll(input);
    assertNotNull(output);
    assertEquals(11, output.getRecordsFiltered());
  }

  @Test
  public void testFilterOnOneColumnIgnoreCase() {
    DataTablesInput input = getBasicInput();

    input.getColumn("username").setSearchValue("OhN1");

    DataTablesOutput<User> output = userRepository.findAll(input);
    assertNotNull(output);
    assertEquals(11, output.getRecordsFiltered());
  }

  @Test
  public void testFilterOnSeveralColumns() {
    DataTablesInput input = getBasicInput();

    input.getColumn("role").setSearchValue("ADMIN");
    input.getColumn("status").setSearchValue("ACTIVE");

    DataTablesOutput<User> output = userRepository.findAll(input);
    assertNotNull(output);
    assertEquals(1, output.getDraw());
    assertNull(output.getError());
    assertEquals(4, output.getRecordsFiltered());
    assertEquals(24, output.getRecordsTotal());

    List<User> users = output.getData();
    assertNotNull(users);
    assertEquals(4, users.size());

    User firstUser = users.get(0);
    User lastUser = users.get(3);
    assertEquals("john0", firstUser.getUsername());
    assertEquals("ADMIN", firstUser.getRole().toString());
    assertEquals("ACTIVE", firstUser.getStatus().toString());
    assertEquals("john18", lastUser.getUsername());
    assertEquals("ADMIN", lastUser.getRole().toString());
    assertEquals("ACTIVE", lastUser.getStatus().toString());
  }

  @Test
  public void testNullColumnFilter() {
    DataTablesInput input = getBasicInput();
    input.getColumn("home.town").setSearchValue("town0+NULL");

    DataTablesOutput<User> output = userRepository.findAll(input);
    assertNotNull(output);
    assertEquals(1, output.getDraw());
    assertNull(output.getError());
    assertEquals(10, output.getRecordsFiltered());
    assertEquals(24, output.getRecordsTotal());
  }

  @Test
  public void testEscapedOrNull() {
    DataTablesInput input = getBasicInput();
    input.getColumn("home.town").setSearchValue("town0+\\NULL");

    DataTablesOutput<User> output = userRepository.findAll(input);
    assertNotNull(output);
    assertEquals(1, output.getDraw());
    assertNull(output.getError());
    assertEquals(6, output.getRecordsFiltered());
    assertEquals(24, output.getRecordsTotal());
  }

  @Test
  public void testEscapedNull() {
    DataTablesInput input = getBasicInput();
    input.getColumn("home.town").setSearchValue("\\NULL");

    DataTablesOutput<User> output = userRepository.findAll(input);
    assertNotNull(output);
    assertEquals(1, output.getDraw());
    assertNull(output.getError());
    assertEquals(1, output.getRecordsFiltered());
    assertEquals(24, output.getRecordsTotal());
  }

  @Test
  public void testMultiFilterOnSameColumn() {
    DataTablesInput input = getBasicInput();

    input.getColumn("role").setSearchValue("ADMIN+USER");

    DataTablesOutput<User> output = userRepository.findAll(input);
    assertNotNull(output);
    assertEquals(1, output.getDraw());
    assertNull(output.getError());
    assertEquals(16, output.getRecordsFiltered());
    assertEquals(24, output.getRecordsTotal());

    List<User> users = output.getData();
    assertNotNull(users);
    assertEquals(10, users.size());

    User firstUser = users.get(0);
    User lastUser = users.get(9);
    assertEquals("john0", firstUser.getUsername());
    assertEquals("ADMIN", firstUser.getRole().toString());
    assertEquals("john14", lastUser.getUsername());
    assertEquals("USER", lastUser.getRole().toString());
  }

  @Test
  public void testEmptyMultiFilterOnSameColumn() {
    DataTablesInput input = getBasicInput();

    input.getColumn("role").setSearchValue("+");

    DataTablesOutput<User> output = userRepository.findAll(input);
    assertNotNull(output);
    assertNull(output.getError());
    assertEquals(24, output.getRecordsFiltered());
  }

  @Test
  public void testFilterOnManyToOneRelationship() {
    DataTablesInput input = getBasicInput();

    input.getColumn("home.town").setSearchValue("town0");

    DataTablesOutput<User> output = userRepository.findAll(input);
    assertNotNull(output);
    assertEquals(1, output.getDraw());
    assertNull(output.getError());
    assertEquals(5, output.getRecordsFiltered());
    assertEquals(24, output.getRecordsTotal());

    List<User> users = output.getData();
    assertNotNull(users);
    assertEquals(5, users.size());

    User firstUser = users.get(0);
    User lastUser = users.get(4);
    assertEquals("john4", firstUser.getUsername());
    assertEquals("john20", lastUser.getUsername());
  }

  @Test
  public void testWithAdditionalSpecification() {
    DataTablesInput input = getBasicInput();

    DataTablesOutput<User> output = userRepository.findAll(input, new TestSpecification<User>());
    assertNotNull(output);
    assertNull(output.getError());
    assertEquals(12, output.getRecordsFiltered());
    assertEquals(24, output.getRecordsTotal());

  }

  @Test
  public void testWithZeroLength() {
    DataTablesInput input = getBasicInput();

    input.setLength(0);
    DataTablesOutput<User> output = userRepository.findAll(input);
    assertNotNull(output);
    assertEquals(0, output.getData().size());
    assertEquals(0, output.getRecordsFiltered());
  }

  @Test
  public void testWithNegativeLength() {
    DataTablesInput input = getBasicInput();

    input.setLength(-1);
    DataTablesOutput<User> output = userRepository.findAll(input);
    assertNotNull(output);
    assertNull(output.getError());
    assertEquals(24, output.getRecordsFiltered());
    assertEquals(24, output.getRecordsTotal());
  }

  @Test
  public void testWithConverter() {
    DataTablesInput input = getBasicInput();
    Converter<User, UserDto> userConverter = new Converter<User, UserDto>() {
      @Override
      public UserDto convert(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getRole().toString(),
            user.getStatus().toString());
      }
    };

    input.getColumn("id").setSearchValue("24");
    DataTablesOutput<UserDto> output = userRepository.findAll(input, userConverter);
    assertNotNull(output);
    UserDto user = output.getData().get(0);
    assertEquals(24, (int) user.getId());
    assertEquals("john23", user.getUsername());
    assertEquals("USER", user.getRole());
    assertEquals("BLOCKED", user.getStatus());
  }

  @Test
  public void testWithFancyPaging() {
    DataTablesInput input = getBasicInput();

    input.setLength(5);
    input.setStart(7);
    DataTablesOutput<User> output = userRepository.findAll(input);
    assertNotNull(output);
    assertNull(output.getError());
    assertEquals(5, output.getData().size());
    assertEquals(8, (int) output.getData().get(0).getId());
    assertEquals(8 + 5 - 1, (int) output.getData().get(4).getId());

    input.setLength(7);
    input.setStart(22);
    output = userRepository.findAll(input);
    assertNotNull(output);
    assertEquals(2, output.getData().size());
    assertEquals(23, (int) output.getData().get(0).getId());
  }

  /**
   * 
   * @return basic input parameters
   */
  private static DataTablesInput getBasicInput() {
    DataTablesInput input = new DataTablesInput();
    input.addColumn("id", true, true, "");
    input.addColumn("username", true, true, "");
    input.addColumn("role", true, true, "");
    input.addColumn("status", true, true, "");
    input.addColumn("home.town", true, true, "");
    input.addOrder("id", true);
    return input;
  }
}
