package com.demkom58.spark.repo;

import com.demkom58.spark.entity.PendingTask;
import com.demkom58.spark.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface PendingTaskRepository extends CrudRepository<PendingTask, Long> {
    PendingTask getById(Long id);

    Collection<PendingTask> getAllByAuthor(User user);

    Collection<PendingTask> getAllByValidator(User validator);

    Collection<PendingTask> getAllByAuthorAndValid(User user, Boolean valid);

    Collection<PendingTask> getAllByValidatorAndValid(User validator, Boolean valid);
}
