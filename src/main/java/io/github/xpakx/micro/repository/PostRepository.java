package io.github.xpakx.micro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.github.xpakx.micro.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import org.springframework.data.jpa.repository.EntityGraph;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Integer>
{
  @EntityGraph(attributePaths={"user"})
  Page<Post> findAllByUserId(Integer id, Pageable pageable);
  
  @EntityGraph(attributePaths={"user"})
  Page<Post> findAllByMessageContaining(String message, Pageable pageable);
  
  @EntityGraph(attributePaths={"user"})
  Page<Post> findAll(Pageable page);
  
  @EntityGraph(attributePaths={"user"})
  Optional<Post> findById(Integer i);

}
