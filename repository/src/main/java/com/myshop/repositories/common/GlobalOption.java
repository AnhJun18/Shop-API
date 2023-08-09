package com.myshop.repositories.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Builder
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class GlobalOption {
    @Id
    @Column(name = "VALUE")
    private String value;

    @Column(name = "NAME")
    private String name;


}
