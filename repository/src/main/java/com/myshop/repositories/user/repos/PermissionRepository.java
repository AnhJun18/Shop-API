package com.myshop.repositories.user.repos;

import com.myshop.repositories.user.entities.Permission;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends CrudRepository<Permission, Long> {
}
