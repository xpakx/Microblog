package io.github.xpakx.micro.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

import io.github.xpakx.micro.entity.Post;
import io.github.xpakx.micro.entity.User;
import io.github.xpakx.micro.entity.Notification;
import io.github.xpakx.micro.repository.NotificationRepository;
import io.github.xpakx.micro.error.NotFoundException;
import io.github.xpakx.micro.error.UserUnauthorized;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NotificationService
{
  private NotificationRepository notificationRepository;
  private UserService userService;
  
  @Autowired
  public NotificationService(NotificationRepository notificationRepository, UserService userService)
  {
    this.notificationRepository = notificationRepository;
    this.userService = userService;
  }

  public List<Notification> findAllByUserId(Integer userId)
  {
    
    List<Notification> allNotifications = notificationRepository.findAllByUserIdAndVisited(userId, false);
    
    for(Notification notification : allNotifications)
    {
      notification.setVisited(true);
      notificationRepository.save(notification);
    }
    return allNotifications;   
  }
  
  public Integer getNewNotificationsCount(Integer userId)
  {
    return notificationRepository.countByUserIdAndVisited(userId, false);
  }
  
  public void addMentions(String message, Post post, User caller)
  {
    List<String> allMentions = new ArrayList<String>();
    Matcher m = Pattern.compile("(\\s|\\A|>)@(\\w+)")
     .matcher(message);
    while (m.find()) 
    {
      Optional<User> user = userService.findByUsernameIgnoreCase(m.group(2));
      if(user.isPresent())
      {
        addNotification(user.get(), post, caller);
      }
    }
  }
  
  private void addNotification(User userToNotify, Post post, User caller)
  {
     Notification notification = new Notification();
     notification.setVisited(false);
     notification.setUser(userToNotify);
     notification.setPost(post);
     notification.setCaller(caller);
     notification.setId(null);
     notificationRepository.save(notification);
  }

}
