package org.springframework.data.jpa.datatables.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.Config;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.DisabledIf;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = Config.class)
public class UserRepositoryTest {

    @Autowired private UserRepository userRepository;

    private void init() {
        userRepository.save(new User(1L, "r"));
        userRepository.save(new User(2L, "ř"));
        userRepository.save(new User(3L, "ŗ"));
        userRepository.save(new User(4L, "ɍ"));
        userRepository.save(new User(5L, "ȓ"));
    }

    @Test
    @DisabledIf(value = "#{'${spring.profiles.active}' == 'mysql'}", loadContext = true)
    void withNationalizedColumn() {
        init();

        DataTablesInput input = new DataTablesInput();
        input.addColumn("name", true, true, "ř");

        DataTablesOutput<User> output = userRepository.findAll(input);

        assertThat(output.getRecordsFiltered()).isEqualTo(1);
        assertThat(output.getData().get(0).getId()).isEqualTo(2L);
    }

}
