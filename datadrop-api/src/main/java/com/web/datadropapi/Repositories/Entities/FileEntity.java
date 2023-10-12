package com.web.datadropapi.Repositories.Entities;

import com.web.datadropapi.Enums.SharedState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "files")
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id;

    @Column(name="name")
    private String name;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Column(name = "last_modified_date")
    private LocalDate lastModifiedDate;

    @Column(name = "shared_state")
    private SharedState sharedState;

    @ManyToOne
    private DirectoryEntity parentDirectory;
}

