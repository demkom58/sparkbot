package com.demkom58.spark.repo;

import com.demkom58.spark.entity.Group;
import com.demkom58.spark.entity.GroupJoinRequest;
import com.demkom58.spark.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface GroupJoinRequestRepository extends CrudRepository<GroupJoinRequest, Long> {
    GroupJoinRequest getById(Long id);

    Collection<GroupJoinRequest> getAllByGroup(Group group);

    Collection<GroupJoinRequest> getAllBySender(User sender);
}
