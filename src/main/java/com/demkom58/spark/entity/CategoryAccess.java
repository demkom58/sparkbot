package com.demkom58.spark.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryAccess {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "category_access_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "catrgory_id", referencedColumnName = "catrgory_id", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "group_id", nullable = false)
    private Group group;

    public CategoryAccess(@NotNull Category category, @NotNull Group group) {
        this.category = category;
        this.group = group;
    }
}
