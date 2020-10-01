package com.demkom58.spark.repo;

import com.demkom58.spark.entity.Group;
import com.demkom58.spark.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface GroupRepository extends CrudRepository<Group, Long> {
    Group getById(Long id);

    Collection<Group> getAllByOwner(User user);
}
