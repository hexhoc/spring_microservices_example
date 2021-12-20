package com.optimagrowth.license.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter @Setter @ToString
//tells spring that this is JPA class
@Entity
//Maps to the database table
@Table(name = "licenses")
public class License extends RepresentationModel<License> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String licenseId;
    private String description;
    @Column(name = "organization_id", nullable = false)
    private String organizationId;
    @Column(name = "product_name", nullable = false)
    private String productName;
    @Column(name = "license_type", nullable = false)
    private String licenseType;
    @Column(name = "comment")
    private String comment;
    @Column(name = "created_at")
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    @Version
    private long version;

    public License withComment(String comment){
        this.setComment(comment);
        return this;
    }

}
