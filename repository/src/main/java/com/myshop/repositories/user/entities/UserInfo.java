package com.myshop.repositories.user.entities;

import com.myshop.repositories.Auditing;
import lombok.*;
import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
@Table(name = "userInfo")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo extends Auditing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "nvarchar(255)")
    private String firstName;

    @Column(columnDefinition = "nvarchar(255)")
    private String lastName;

    private String gender;

    @Column(unique = true)
    private String phone;

    @Column(columnDefinition = "nvarchar(255)")
    private String address;

    @OneToOne(fetch = FetchType.EAGER)
    private Account account;
}
