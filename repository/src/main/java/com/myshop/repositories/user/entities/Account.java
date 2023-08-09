package com.myshop.repositories.user.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Data
@Table(name = "ACCOUNT")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "ROLEID")
    private Long roleId;

    @Column(name = "ISDELETED")
    private boolean isDeleted;
}
