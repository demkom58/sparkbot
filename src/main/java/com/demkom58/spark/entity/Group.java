package com.demkom58.spark.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "group_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "user_id", nullable = false)
    private User owner;
}
