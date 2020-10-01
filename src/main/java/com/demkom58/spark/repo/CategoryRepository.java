package com.demkom58.spark.repo;

import com.demkom58.spark.entity.Category;
import com.demkom58.spark.entity.Group;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {
    Category getById(Long id);

    Collection<Category> getAllByOwner(Group owner);
}
