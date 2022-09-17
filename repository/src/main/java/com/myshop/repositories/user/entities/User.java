package com.myshop.repositories.user.entities;

import com.myshop.repositories.Auditing;
import lombok.*;
import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@Table(name = "auth_user")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class User extends Auditing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(unique=true)
    private String username;

    @NonNull
    private String password;

    @NonNull
    private String salt;

    @OneToOne(fetch = FetchType.LAZY)
    private Role role;

    private int deleteFlag;

    private String fullName;

    private String phone;

    private String dob;

    private String email;

    private String address;

    private String firstName;

    private String lastName;

    private String gender;
}
