package io.github.xpakx.micro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import io.github.xpakx.micro.service.UserService;
import io.github.xpakx.micro.service.NotificationService;
import io.github.xpakx.micro.entity.Post;
import io.github.xpakx.micro.entity.Comment;
import io.github.xpakx.micro.entity.User;
import io.github.xpakx.micro.error.NotFoundException;

import javax.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import java.util.List;
import io.github.xpakx.micro.error.ForbiddenException;
import java.security.Principal;

@Controller
public class NotificationController
{
  private UserService userService;
  private NotificationService notificationService;
  
  @Autowired
  public NotificationController(UserService userService, NotificationService notificationService)
  {
    this.userService = userService;
    this.notificationService = notificationService;
  }
  
  @GetMapping("/notifications")
  public String getUserPosts(Model model, Principal principal)
  { 
  
    User user = userService.findByUsername(principal.getName()).orElseThrow(() -> new ForbiddenException());
    
    model.addAttribute("user", user);
    model.addAttribute("notifications", notificationService.findAllByUserId(user.getId()));
    return "notifications";
  }
  
 
}
