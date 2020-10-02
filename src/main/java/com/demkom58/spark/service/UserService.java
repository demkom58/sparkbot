package com.demkom58.spark.service;

import com.demkom58.spark.entity.User;
import com.demkom58.spark.repo.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public @NotNull User getUser(int id) {
        User found = userRepository.getById(id);

        if (found == null) {
            found = new User(id, 0L);
            userRepository.save(found);
        }

        return found;
    }

}
