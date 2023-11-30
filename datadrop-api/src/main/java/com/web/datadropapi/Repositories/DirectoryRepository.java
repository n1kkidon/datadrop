package com.web.datadropapi.Repositories;

import com.web.datadropapi.Repositories.Entities.DirectoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DirectoryRepository extends JpaRepository<DirectoryEntity, Long> {
    //@Query(value = "SELECT * FROM directories WHERE owner_user_id = :ownerId AND parent_directory_directory_id = NULL",  nativeQuery = true)
    List<DirectoryEntity> findByOwner_idAndParentDirectory_IdIsNull(Long ownerId);
    Optional<DirectoryEntity> findByName(String name);
}
