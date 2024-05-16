package com.blankcil.api.blankcilapi.repository;

import java.util.List;
import java.util.Optional;

import com.blankcil.api.blankcilapi.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

  Optional<UserEntity> findByEmail(String email);
  Optional<UserEntity> findByEmailAndCode(String email, String code);
  boolean existsUserEntityByEmail(String email);
  List<UserEntity> findByFullnameIgnoreCaseContaining(String fullname);
}
