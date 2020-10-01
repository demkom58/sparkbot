package com.demkom58.spark.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GroupJoinRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "request_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "user_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "group_id", nullable = false)
    private Group group;
}
