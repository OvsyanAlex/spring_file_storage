package org.example.repository;

import org.example.model.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;


public interface FileRepository extends JpaRepository<FileEntity, Integer> {

}
