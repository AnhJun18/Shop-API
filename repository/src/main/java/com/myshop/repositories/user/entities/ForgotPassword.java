package com.myshop.repositories.user.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Data
@Table(name = "FORGOT_PASSWORD")
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ForgotPassword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "VERIFYCODE")
    private String verifyCode;
    @Column(name = "USEDCODE")
    private boolean usedCode;
    @Column(name = "EXPIRYDATE")
    private Long expiryDate;
}
