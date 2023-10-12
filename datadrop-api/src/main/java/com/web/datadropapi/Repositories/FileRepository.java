package com.web.datadropapi.Repositories;

import com.web.datadropapi.Repositories.Entities.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileEntity, Long> {
}
