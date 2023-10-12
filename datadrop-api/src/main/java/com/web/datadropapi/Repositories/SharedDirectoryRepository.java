package com.web.datadropapi.Repositories;

import com.web.datadropapi.Repositories.Entities.DirectoryEntity;
import com.web.datadropapi.Repositories.Entities.SharedDirectoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SharedDirectoryRepository extends JpaRepository<SharedDirectoryEntity, Long> {
}
