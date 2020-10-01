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
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "category_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "group_id", nullable = false)
    private Group owner;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "category_id")
    @Nullable
    private Category parent;

}
