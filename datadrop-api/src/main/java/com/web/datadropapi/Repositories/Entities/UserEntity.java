package com.web.datadropapi.Repositories.Entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.web.datadropapi.Enums.*;
import com.web.datadropapi.Models.UserDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "directories")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name="name")
    private String name;

    @Column(name="email")
    private String email;

    @Column(name="password")
    private String password;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Column(name = "last_modified_date")
    private LocalDate lastModifiedDate;

    @Column(name = "state")
    private UserState state;

    @Column(name = "role")
    private UserRole role;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.REMOVE)
    private List<DirectoryEntity> directories;

    @OneToMany(mappedBy = "sharedWith", cascade = CascadeType.REMOVE)
    private List<SharedFileEntity> sharedFilesWithUser;
    @OneToMany(mappedBy = "sharedWith", cascade = CascadeType.REMOVE)
    private List<SharedDirectoryEntity> sharedDirectoriesWithUser;

}