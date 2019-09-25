package io.github.xpakx.micro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.github.xpakx.micro.entity.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

public interface CommentRepository extends JpaRepository<Comment, Integer>
{
  Page<Comment> findAllByUserId(Integer id, Pageable pageable);
  Page<Comment> findAllByPostId(Integer id, Pageable pageable);
}
