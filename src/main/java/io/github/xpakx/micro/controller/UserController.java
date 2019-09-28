package io.github.xpakx.micro.controller;

import org.springframework.stereotype.Controller;
import io.github.xpakx.micro.service.UserService;
import io.github.xpakx.micro.service.PostService;
import io.github.xpakx.micro.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import io.github.xpakx.micro.entity.Post;
import io.github.xpakx.micro.entity.Comment;
import io.github.xpakx.micro.entity.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import javax.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.data.domain.Page;
import java.util.List;

@Controller
public class UserController
{
  private UserService userService;
  private PostService postService;
  private CommentService commentService;
  
  @Autowired
  public UserController(UserService userService, PostService postService, CommentService commentService)
  {
    this.userService = userService;
    this.postService = postService;
    this.commentService = commentService;
  }
  
  @GetMapping("register")
  public String registration(Model model)
  {
    model.addAttribute("userForm", new User());

    return "register";
  }
  
  private void testUserFormForErrors(User userForm, BindingResult bindingResult)
  {
    if(!userForm.getPassword().equals(userForm.getConfirmPassword()))
    {
      bindingResult.rejectValue("confirmPassword", "error.userForm", "Passwords specified must be identical!");
    }
    
    User user = userService.findByUsername(userForm.getUsername());
    if(user != null)
    {
      bindingResult.rejectValue("username", "error.userForm", "User with specified username exists!");
    }
    
    user = userService.findByEmail(userForm.getEmail());
    if(user != null)
    {
      bindingResult.rejectValue("email", "error.userForm", "User with specified email exists!");
    }
  }
  
  @PostMapping("register")
  public String registration(@Valid @ModelAttribute("userForm") User userForm, BindingResult bindingResult, Model model)
  {
    testUserFormForErrors(userForm, bindingResult);
    
    if (bindingResult.hasErrors()) 
    {
       return "register";
    }
    
    userForm.setId(null);
    userService.save(userForm);
    return "redirect:/login";
  }
  
  @GetMapping("/user/{userId}/posts")
  public String getUserPosts(@PathVariable Integer userId, Model model)
  {
    model.addAttribute("user", userService.findById(userId));
    model.addAttribute("posts", postService.findAllByUserId(userId, 0).getContent());
    return "userPosts";
  }
  
  @GetMapping("/user/{userId}/posts/{page}")
  public String getUserPosts(@PathVariable Integer userId, @PathVariable Integer page, Model model)
  {
    model.addAttribute("user", userService.findById(userId));
    model.addAttribute("posts", postService.findAllByUserId(userId, page).getContent());
    return "userPosts";
  }
  
  @GetMapping("/user/{userId}/comments")
  public String getUserComments(@PathVariable Integer userId, Model model)
  {
    model.addAttribute("user", userService.findById(userId));
    model.addAttribute("comments", commentService.findAllByUserId(userId, 0).getContent());
    return "userComments";
  }
  
  @GetMapping("/user/{userId}/comments/{page}")
  public String getUserComments(@PathVariable Integer userId, @PathVariable Integer page, Model model)
  {
    model.addAttribute("user", userService.findById(userId));
    model.addAttribute("comments", commentService.findAllByUserId(userId, page).getContent());
    return "userComments";
  }
}
