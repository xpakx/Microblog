package io.github.xpakx.micro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.github.xpakx.micro.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

public interface PostRepository extends JpaRepository<Post, Integer>
{
  Page<Post> findAllByUserId(Integer id, Pageable pageable);
}
