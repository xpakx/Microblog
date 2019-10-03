package io.github.xpakx.micro.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import io.github.xpakx.micro.entity.User;

import org.springframework.data.jpa.repository.EntityGraph;

public interface UserRepository extends JpaRepository<User, Integer>
{
  Optional<User> findByEmail(String email);
  
  Optional<User> findById(String email);
  
  @EntityGraph(attributePaths={"roles"})
  Optional<User> findByUsername(String username);
  
  Optional<User> findByUsernameIgnoreCase(String username);
}
