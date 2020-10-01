package com.demkom58.spark.repo;

import com.demkom58.spark.entity.Payment;
import com.demkom58.spark.entity.Task;
import com.demkom58.spark.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface PaymentRepository extends CrudRepository<Payment, Long> {
    Payment getById(Long id);

    Collection<Payment> getAllByTask(Task user);

    Collection<Payment> getAllByUser(User user);

    Collection<Payment> getAllByValue(Long value);
}
