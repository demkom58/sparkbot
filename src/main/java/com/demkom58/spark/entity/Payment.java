package com.demkom58.spark.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "payment_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "task_id", referencedColumnName = "task_id", nullable = false)
    private Task task;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "time", nullable = false)
    private LocalDateTime time;

    @Column(name = "value", nullable = false)
    private Long value;

    public Payment(@NotNull User user, @NotNull Task task, @NotNull LocalDateTime time, @NotNull Long value) {
        this.user = user;
        this.task = task;
        this.time = time;
        this.value = value;
    }

    public Payment(@NotNull User user, @NotNull Task task, @NotNull Long value) {
        this(user, task, LocalDateTime.now(), value);
    }

}
