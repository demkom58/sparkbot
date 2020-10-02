package com.demkom58.spark.service;

import com.bgerstle.result.Result;
import com.demkom58.spark.entity.Payment;
import com.demkom58.spark.entity.Task;
import com.demkom58.spark.entity.User;
import com.demkom58.spark.repo.CategoryAccessRepository;
import com.demkom58.spark.repo.PaymentRepository;
import com.demkom58.spark.repo.UserRepository;
import com.demkom58.spark.util.LightweightException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final CategoryAccessRepository categoryAccessRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository,
                          UserRepository userRepository,
                          CategoryAccessRepository categoryAccessRepository) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.categoryAccessRepository = categoryAccessRepository;
    }

    /**
     * Execute transaction payment.
     *
     * @param user existing bot user
     * @param task task that user want to buy
     * @return payment object or empty optional if can't buy.
     */
    @Transactional
    @Description("Produces transaction request for task buying")
    public Result<Payment, LightweightException> pay(@NotNull final User user,
                                                     @NotNull final Task task) {
        final var balance = user.getBalance();
        if (balance < task.getPrice())
            return Result.failure(new LightweightException("На вашем счету недостаточно стредств."));

        final var taskCategory = task.getCategory();
        if (!categoryAccessRepository.hasUserAccess(taskCategory, user))
            return Result.failure(new LightweightException("У вас нет доступа к этой категории."));

        final var price = task.getPrice();
        final var author = task.getAuthor();
        final var payment = new Payment(user, author, task, price);

        user.setBalance(user.getBalance() - price);
        author.setBalance(author.getBalance() + price);

        paymentRepository.save(payment);
        userRepository.saveAll(Arrays.asList(user, author));

        return Result.success(payment);
    }

}

