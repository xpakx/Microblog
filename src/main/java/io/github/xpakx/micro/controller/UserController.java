package io.github.xpakx.micro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import io.github.xpakx.micro.service.UserService;
import io.github.xpakx.micro.service.PostService;
import io.github.xpakx.micro.service.CommentService;
import io.github.xpakx.micro.service.SecurityService;
import io.github.xpakx.micro.entity.Post;
import io.github.xpakx.micro.entity.Comment;
import io.github.xpakx.micro.entity.User;
import io.github.xpakx.micro.error.NotFoundException;

import javax.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import org.springframework.data.domain.Page;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

@Controller
public class UserController
{
  private UserService userService;
  private PostService postService;
  private SecurityService securityService;
  private CommentService commentService;
  
  @Autowired
  public UserController(UserService userService, PostService postService, CommentService commentService, SecurityService securityService)
  {
    this.userService = userService;
    this.postService = postService;
    this.commentService = commentService;
    this.securityService = securityService;
  }
  
  @GetMapping("register")
  public String registration(Model model)
  {
    model.addAttribute("userForm", new User());
    return "register";
  }
  
  @PostMapping("register")
  public String registration(@Valid @ModelAttribute("userForm") User userForm, BindingResult bindingResult, Model model, HttpServletRequest request)
  {
    bindingResult.addAllErrors(testUserFormForErrors(userForm));
    if(bindingResult.hasErrors()) 
    {
       return "register";
    }
    String password = userForm.getPassword();
    userService.save(userForm);
    
    securityService.autologin(userForm.getEmail(), password, request);
    
    return "redirect:/posts";
  }
  
  private Errors testUserFormForErrors(User userForm)
  {
    BindException errors = new BindException(userForm, "userForm");
    
    if(userService.isPasswordAndConfirmPasswordDifferent(userForm))
    {
      errors.rejectValue("confirmPassword", "error.userForm", "Passwords specified must be identical!");
    }
    
    if(userService.isUserWithGivenUsernameExistIgnoreCase(userForm))
    {
      errors.rejectValue("username", "error.userForm", "User with specified username exists!");
    }
    
    if(userService.isUserWithGivenEmailExist(userForm))
    {
      errors.rejectValue("email", "error.userForm", "User with specified email exists!");
    }
    
    return errors;
  }
    
  @GetMapping("/user/{username}/posts")
  public String getUserPosts(@PathVariable String username, Model model)
  { 
    User user = userService.findByUsernameIgnoreCase(username).orElseThrow(() -> new NotFoundException("No such user!"));
    model.addAttribute("user", user);
    model.addAttribute("posts", postService.findAllByUserId(user.getId(), 0).getContent());
    return "userPosts";
  }
  
  @GetMapping("/user/{username}/posts/{page}")
  public String getUserPosts(@PathVariable String username, @PathVariable Integer page, Model model)
  {
    User user = userService.findByUsernameIgnoreCase(username).orElseThrow(() -> new NotFoundException("No such user!"));
    model.addAttribute("user", user);
    model.addAttribute("posts", postService.findAllByUserId(user.getId(), page).getContent());
    return "userPosts";
  }
  
  @GetMapping("/user/{username}/comments")
  public String getUserComments(@PathVariable String username, Model model)
  {
    User user = userService.findByUsernameIgnoreCase(username).orElseThrow(() -> new NotFoundException("No such user!"));
    model.addAttribute("user", user);
    model.addAttribute("comments", commentService.findAllByUserId(user.getId(), 0).getContent());
    return "userComments";
  }
  
  @GetMapping("/user/{username}/comments/{page}")
  public String getUserComments(@PathVariable String username, @PathVariable Integer page, Model model)
  {
    User user = userService.findByUsernameIgnoreCase(username).orElseThrow(() -> new NotFoundException("No such user!"));
    model.addAttribute("user", user);
    model.addAttribute("comments", commentService.findAllByUserId(user.getId(), page).getContent());
    return "userComments";
  }
}
