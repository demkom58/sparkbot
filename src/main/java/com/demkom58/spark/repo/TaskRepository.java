package com.demkom58.spark.repo;

import com.demkom58.spark.entity.Category;
import com.demkom58.spark.entity.Task;
import com.demkom58.spark.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface TaskRepository extends CrudRepository<Task, Long> {
    Task getById(Long id);

    Collection<Task> getAllByAuthor(User author);

    Collection<Task> getAllByCategory(Category category);
}
