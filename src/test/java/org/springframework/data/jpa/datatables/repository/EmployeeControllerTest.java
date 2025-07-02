package org.springframework.data.jpa.datatables.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.datatables.TestApplication;
import org.springframework.data.jpa.datatables.model.Employee;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = TestApplication.class)
@AutoConfigureMockMvc
class EmployeeControllerTest {

  @Autowired private EmployeeRepository employeeRepository;

  @BeforeEach
  public void init() {
    this.employeeRepository.saveAll(Employee.ALL);
  }

  private static Map<String, String> createQuery() {
    var query = new HashMap<String, String>();

    query.put("draw", "1");
    query.put("start", "0");
    query.put("length", "10");

    query.put("search.value", "");
    query.put("search.regex", "false");

    query.put("order[0].column", "0");
    query.put("order[0].dir", "asc");

    query.put("columns[0].data", "id");
    query.put("columns[0].searchable", "true");
    query.put("columns[0].orderable", "true");
    query.put("columns[0].search.value", "");
    query.put("columns[0].search.regex", "false");

    return query;
  }

  @Test
  void basic(@Autowired MockMvc mvc) throws Exception {
    var query = createQuery();

    mvc.perform(get("/employees").queryParams(MultiValueMap.fromSingleValue(query)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("draw").value(1))
        .andExpect(jsonPath("recordsTotal").value(6))
        .andExpect(jsonPath("recordsFiltered").value(6))
        .andExpect(jsonPath("data[0].firstName").value("Brenden"))
        .andExpect(jsonPath("error").isEmpty());
  }

  @Test
  void page(@Autowired MockMvc mvc) throws Exception {
    var query = createQuery();

    query.put("draw", "2");
    query.put("start", "1");
    query.put("length", "1");

    mvc.perform(get("/employees").queryParams(MultiValueMap.fromSingleValue(query)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("draw").value(2))
        .andExpect(jsonPath("recordsTotal").value(6))
        .andExpect(jsonPath("recordsFiltered").value(6))
        .andExpect(jsonPath("data[0].firstName").value("Ashton"))
        .andExpect(jsonPath("error").isEmpty());
  }

  @Test
  void invalidStart(@Autowired MockMvc mvc) throws Exception {
    var query = createQuery();

    query.put("start", "-1");

    mvc.perform(get("/employees").queryParams(MultiValueMap.fromSingleValue(query)))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void withPOST(@Autowired MockMvc mvc) throws Exception {
    mvc.perform(
            post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
"""
{
  "draw": "1",
  "start": "0",
  "length": "10",

  "search": {
    "value": "",
    "regex": false
  },

  "order": [
    {
      "column": 0,
      "dir": "asc"
    }
  ],

  "columns": [
    {
      "data": "id",
      "searchable": true,
      "orderable": true,
      "search": {
        "value": "",
        "regex": false
      }
    }
  ]
}
"""))
        .andExpect(status().isOk())
        .andExpect(jsonPath("draw").value(1))
        .andExpect(jsonPath("recordsTotal").value(6))
        .andExpect(jsonPath("recordsFiltered").value(6))
        .andExpect(jsonPath("data[0].firstName").value("Brenden"))
        .andExpect(jsonPath("error").isEmpty());
  }
}
