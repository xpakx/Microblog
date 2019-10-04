package io.github.xpakx.micro.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ExceptionHandler;
import javax.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import java.security.Principal;
import io.github.xpakx.micro.entity.User;
import io.github.xpakx.micro.service.NotificationService;
import io.github.xpakx.micro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;

@ControllerAdvice 
public class GlobalControllerAdvice 
{
  private NotificationService notificationService;
  private UserService userService;
  
  @Autowired
  public GlobalControllerAdvice(UserService userService, NotificationService notificationService)
  {
    this.userService = userService;
    this.notificationService = notificationService;
  }

  @ExceptionHandler(Exception.class)
  public String defaultErrorHandler(HttpServletRequest request, Exception e, Model model)
  {
    model.addAttribute("exception", e);
    model.addAttribute("url", request.getRequestURL());
    return "error";
  }
  
  @ModelAttribute("notifcount")
  public Integer getNotificationCountForCurrentUser(Principal principal) 
  {
    Optional<User> user = userService.findByUsername(principal.getName());
    
    if(user.isPresent())
    {
      return notificationService.getNewNotificationsCount(user.get().getId());
    }
    else
    {
      return 0;
    }
    
    
  }
}
