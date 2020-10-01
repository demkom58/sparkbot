package com.demkom58.spark.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PendingTask {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "pending_task_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "user_id", nullable = false)
    private User author;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "category_id", nullable = false)
    private Category category;

    @Column(name = "description")
    @Nullable
    private String description;

    @ManyToOne
    @JoinColumn(name = "validator_id", referencedColumnName = "user_id")
    private User validator;
    private String validatorAnswer;
    private Boolean valid;

}
