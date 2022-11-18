package com.myshop.repositories;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.Instant;

@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@MappedSuperclass
@JsonIgnoreProperties(
        value = {"createdDate"},
        allowGetters = true
)
public class Auditing {

    @CreatedDate
    protected Instant createdDate;
    
}
