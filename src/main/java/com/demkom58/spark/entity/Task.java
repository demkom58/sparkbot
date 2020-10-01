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
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "task_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "user_id", nullable = false)
    private User author;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "category_id", nullable = false)
    private Category category;

    @Column(name = "description")
    private String description;

    /**
     * Nullable in case when uploaded by user with role that can upload force.
     */
    @ManyToOne
    @JoinColumn(name = "pending_task_id", referencedColumnName = "pending_task_id")
    @Nullable
    private PendingTask pendingTask;
}
