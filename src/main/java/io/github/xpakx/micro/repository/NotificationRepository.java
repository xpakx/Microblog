package io.github.xpakx.micro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.github.xpakx.micro.entity.Notification;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;

public interface NotificationRepository extends JpaRepository<Notification, Integer>
{
  @EntityGraph(attributePaths={"caller", "post"})
  List<Notification> findAllByUserIdAndVisited(Integer userId, boolean visited);
  
  Integer countByUserIdAndVisited(Integer userId, boolean visited);

}
