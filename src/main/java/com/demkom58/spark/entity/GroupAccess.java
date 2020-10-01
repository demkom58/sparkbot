package com.demkom58.spark.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GroupAccess {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "group_access_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "group_id", nullable = false)
    private Group group;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @Column(name = "role", nullable = false)
    private GroupRole role;
}
