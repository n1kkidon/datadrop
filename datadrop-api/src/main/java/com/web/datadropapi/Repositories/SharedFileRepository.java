package com.web.datadropapi.Repositories;

import com.web.datadropapi.Repositories.Entities.DirectoryEntity;
import com.web.datadropapi.Repositories.Entities.SharedFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SharedFileRepository extends JpaRepository<SharedFileEntity, Long> {
}
