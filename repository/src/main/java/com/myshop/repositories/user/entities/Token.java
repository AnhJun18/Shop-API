package com.myshop.repositories.user.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Data
@Table(name = "AUTH_TOKEN")
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Token {

    @Id
    @Column(name = "TOKENID")
    private String tokenId;

    @Column(name = "ACCOUNT")
    private String account;

    @Column(name = "EXPIREDTIME")
    private long expiredTime;
}
