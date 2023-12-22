package com.web.datadropapi.Repositories;

import com.web.datadropapi.Repositories.Entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByName(String name);

    @Query(value = "SELECT DISTINCT u.* FROM file_users fu " +
            "INNER JOIN files f ON fu.directory_file_id = f.file_id " +
            "INNER JOIN users u ON f.owner_user_id = u.user_id " +
            "WHERE fu.shared_with_user_id = :userId " +
            "UNION " +
            "SELECT DISTINCT u.* FROM directory_users du " +
            "INNER JOIN directories d ON du.directory_directory_id = d.directory_id " +
            "INNER JOIN users u ON d.owner_user_id = u.user_id " +
            "WHERE du.shared_with_user_id = :userId", nativeQuery = true)
    List<UserEntity> findUsersSharingFilesWithUser(@Param("userId") Long userId);
}
