package com.web.datadropapi.Repositories;

import com.web.datadropapi.Repositories.Entities.DirectoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DirectoryRepository extends JpaRepository<DirectoryEntity, Long> {

}
