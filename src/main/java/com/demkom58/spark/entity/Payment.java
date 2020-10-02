package com.demkom58.spark.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "payment_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "user_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", referencedColumnName = "user_id", nullable = false)
    private User receiver;

    @ManyToOne
    @JoinColumn(name = "task_id", referencedColumnName = "task_id", nullable = false)
    private Task task;

    @Column(name = "time", nullable = false)
    private LocalDateTime time;

    @Column(name = "value", nullable = false)
    private Long value;

    public Payment(@NotNull User sender,
                   @NotNull User receiver,
                   @NotNull Task task,
                   @NotNull LocalDateTime time,
                   @NotNull Long value) {
        this.sender = sender;
        this.receiver = receiver;
        this.task = task;
        this.time = time;
        this.value = value;
    }

    public Payment(@NotNull User sender, @NotNull User receiver, @NotNull Task task, @NotNull Long value) {
        this(sender, receiver, task, LocalDateTime.now(), value);
    }

}
