package com.demkom58.spark.repo;

import com.demkom58.spark.entity.Group;
import com.demkom58.spark.entity.GroupAccess;
import com.demkom58.spark.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface GroupAccessRepository extends CrudRepository<GroupAccess, Long> {
    GroupAccess getById(Long id);

    Collection<GroupAccess> getAllByGroup(Group group);

    Collection<GroupAccess> getAllByUser(User user);
}
