package io.github.xpakx.micro.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import io.github.xpakx.micro.entity.User;


public interface UserRepository extends JpaRepository<User, Integer>
{
  Optional<User> findByEmail(String email);
}
