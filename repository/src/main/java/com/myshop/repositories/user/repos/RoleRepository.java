package com.myshop.repositories.user.repos;

import com.myshop.repositories.user.entities.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends CrudRepository<Role, String> {
    Role findByName(String name);

    Optional<Role> findById(Long id);
}
