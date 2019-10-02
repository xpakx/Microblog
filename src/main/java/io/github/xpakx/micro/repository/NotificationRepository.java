package io.github.xpakx.micro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import io.github.xpakx.micro.entity.Notification;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer>
{
  List<Notification> findAllByUserIdAndVisited(Integer userId, boolean visited);
  Integer countByUserIdAndVisited(Integer userId, boolean visited);

}
