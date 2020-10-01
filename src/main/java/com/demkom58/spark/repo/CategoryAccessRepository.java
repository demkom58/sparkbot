package com.demkom58.spark.repo;

import com.demkom58.spark.entity.Category;
import com.demkom58.spark.entity.CategoryAccess;
import com.demkom58.spark.entity.Group;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface CategoryAccessRepository extends CrudRepository<CategoryAccess, Long> {
    CategoryAccess getById(Long id);

    Collection<CategoryAccess> getAllByCategory(Category category);

    Collection<CategoryAccess> getAllByGroup(Group group);
}
