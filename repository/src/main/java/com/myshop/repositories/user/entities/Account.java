package com.myshop.repositories.user.entities;

import com.myshop.repositories.Auditing;
import lombok.*;
import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@Table(name = "account")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Account extends Auditing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(unique=true)
    private String username;

    @NonNull
    private String password;

    private String email;


    @OneToOne(fetch = FetchType.EAGER)
    private Role role;

    private  boolean deleteFlag;
}
