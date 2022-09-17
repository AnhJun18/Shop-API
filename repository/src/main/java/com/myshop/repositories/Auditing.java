package com.myshop.repositories;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.Instant;

@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@MappedSuperclass
@JsonIgnoreProperties(
        value = {"createdDate", "lastModifiedDate"},
        allowGetters = true
)
public class Auditing {

    @CreatedBy
    protected Long createdBy;

    @CreatedDate
    protected Instant createdDate;

    @LastModifiedBy
    protected Long lastModifiedBy;

    @LastModifiedDate
    protected Instant lastModifiedDate;

}
