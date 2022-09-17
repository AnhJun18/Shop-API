package com.myshop.repositories.user.entities;

import lombok.*;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Table(name = "auth_role")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String grand;
    private Boolean status;
    private boolean showRole;
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Permission> permissions = new ArrayList<>();
}
