package io.github.xpakx.micro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.github.xpakx.micro.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Integer>
{

}
