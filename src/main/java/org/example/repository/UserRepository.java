package org.example.repository;



import org.example.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<UserEntity, Integer> {


    Optional<UserEntity> findByUserName(String userName);

    @Query("SELECT u FROM UserEntity u " +
           "LEFT JOIN FETCH u.events " +
           "WHERE u.id = :id")
    Optional<UserEntity> findByIdWithEvents(@Param("id") Integer id);

    @Query("SELECT u FROM UserEntity u " +
           "LEFT JOIN FETCH u.events ")
    List<UserEntity> findAll();

}
