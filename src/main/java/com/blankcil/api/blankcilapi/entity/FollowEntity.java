package com.blankcil.api.blankcilapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Optional;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="follow")
@ToString
public class FollowEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "follower_id")
    private UserEntity follower;

    @ManyToOne
    @JoinColumn(name = "target_id")
    private UserEntity target;

    public FollowEntity(UserEntity follower, Optional<UserEntity> target) {
    }
}
