package com.demkom58.spark.service;

import com.demkom58.spark.entity.User;
import com.demkom58.spark.repo.UserRepository;
import com.demkom58.springram.controller.user.SpringramUserDetails;
import com.demkom58.springram.controller.user.SpringramUserDetailsService;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
public class SparkUserDetailsService implements SpringramUserDetailsService {
    private final UserRepository repository;

    public SparkUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    @Nullable
    @Override
    public SpringramUserDetails loadById(long id) {
        final User byId = repository.getById(id);
        return byId == null ? null : new MySpringramUserDetails(byId);
    }

    record MySpringramUserDetails(User user) implements SpringramUserDetails {
        @Nullable
        @Override
        public String getChain() {
            return user.getChain();
        }
    }
}
