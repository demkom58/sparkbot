package com.demkom58.spark.repo;

import com.demkom58.spark.entity.Category;
import com.demkom58.spark.entity.CategoryAccess;
import com.demkom58.spark.entity.Group;
import com.demkom58.spark.entity.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface CategoryAccessRepository extends JpaRepository<CategoryAccess, Long> {
    CategoryAccess getById(Long id);

    Collection<CategoryAccess> getAllByCategory(Category category);

    Collection<CategoryAccess> getAllByGroup(Group group);

    @Query("""
            SELECT (COUNT(ga.group.id) > 0) FROM GroupAccess ga WHERE ga.user = ?2 AND ga.group.id IN (
                SELECT ca.group.id FROM CategoryAccess ca WHERE ca.category.id = ?1
            )
            """)
    boolean hasUserAccess(@NotNull final Category category, @NotNull final User user);
}
