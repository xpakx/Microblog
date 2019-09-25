package io.github.xpakx.micro.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import io.github.xpakx.micro.entity.UserRole;


public interface UserRoleRepository extends JpaRepository<UserRole, Integer>
{  
  Optional<UserRole> findByName(String username);
}
