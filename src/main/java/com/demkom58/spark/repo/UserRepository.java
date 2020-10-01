package com.demkom58.spark.repo;

import com.demkom58.spark.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    User getById(Integer id);
}
