package com.demkom58.spark.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    @Id
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Column(name = "balance", nullable = false)
    private Long balance = 0L;

    @Column(name = "chain", nullable = false)
    private String chain = "default";

    public User(Long id) {
        this.id = id;
    }
}
