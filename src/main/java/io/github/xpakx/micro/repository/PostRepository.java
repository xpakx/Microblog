package io.github.xpakx.micro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.github.xpakx.micro.entity.Post;

public interface PostRepository extends JpaRepository<Post, Integer>
{

}
