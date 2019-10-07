package io.github.xpakx.micro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.github.xpakx.micro.entity.Tag;

import org.springframework.data.jpa.repository.EntityGraph;

public interface TagRepository extends JpaRepository<Tag, Integer>
{
  
}
