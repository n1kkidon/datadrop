package com.web.datadropapi.Repositories.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.web.datadropapi.Enums.SharedState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "directories")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class DirectoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "directory_id")
    private Long id;

    @Column(name="name")
    private String name;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Column(name = "last_modified_date")
    private LocalDate lastModifiedDate;

    @Column(name = "shared_state")
    private SharedState sharedState;

    @OneToMany(mappedBy = "parentDirectory", cascade = CascadeType.REMOVE)
    private List<FileEntity> files;

    @OneToMany(mappedBy = "parentDirectory", cascade = CascadeType.REMOVE)
    private List<DirectoryEntity> subdirectories;

    @ManyToOne
    private DirectoryEntity parentDirectory;

    @ManyToOne
    private UserEntity owner;
    @OneToMany(mappedBy = "directory", cascade = CascadeType.REMOVE)
    private List<SharedDirectoryEntity> sharedWith;

    public String getAbsolutePath(){
        if(parentDirectory == null) //root
            return "/";
        else return parentDirectory.getAbsolutePath() + name + "/";
    }
    public Resource getChildItemInSystem(String name) throws MalformedURLException {
        var path = Path.of(getAbsolutePath()).resolve(name);
        path = Paths.get("USER_FILES/" + owner.getId(), path.toString());
        return new UrlResource(path.toUri());
    }

    public boolean containsItem(String name){
        boolean fileNameExists = false;
        boolean directoryNameExists = false;
        if(getFiles() != null)
            fileNameExists = getFiles().stream().anyMatch(x -> x.getName().equals(name));
        if(getSubdirectories() != null)
            directoryNameExists = getSubdirectories().stream().anyMatch(x -> x.getName().equals(name));
        return fileNameExists || directoryNameExists;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DirectoryEntity that = (DirectoryEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
