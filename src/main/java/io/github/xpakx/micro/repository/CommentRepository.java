package io.github.xpakx.micro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.github.xpakx.micro.entity.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import org.springframework.data.jpa.repository.EntityGraph;

public interface CommentRepository extends JpaRepository<Comment, Integer>
{
  @EntityGraph(attributePaths={"user"})
  Page<Comment> findAllByUserId(Integer id, Pageable pageable);
  
  @EntityGraph(attributePaths={"user"})
  Page<Comment> findAllByPostId(Integer id, Pageable pageable);
  
  @EntityGraph(attributePaths={"user"})
  Page<Comment> findAll(Pageable page);
}
