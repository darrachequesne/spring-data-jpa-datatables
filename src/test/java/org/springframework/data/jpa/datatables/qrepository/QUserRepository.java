package org.springframework.data.jpa.datatables.qrepository;

import org.springframework.data.jpa.datatables.model.User;
import org.springframework.data.jpa.datatables.qrepository.QDataTablesRepository;

public interface QUserRepository extends QDataTablesRepository<User, Integer> {

}
