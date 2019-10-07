package io.github.xpakx.micro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.github.xpakx.micro.entity.Tag;
import io.github.xpakx.micro.entity.Post;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Integer>
{
  Optional<Tag> findByName(String tagname);
}
