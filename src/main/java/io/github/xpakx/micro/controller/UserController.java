package io.github.xpakx.micro.controller;

import org.springframework.stereotype.Controller;
import io.github.xpakx.micro.service.UserService;
import io.github.xpakx.micro.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import io.github.xpakx.micro.entity.Post;
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
  
  @Autowired
  public UserController(UserService userService, PostService postService)
  {
    this.userService = userService;
    this.postService = postService;
  }
  
  @GetMapping("register")
  public String registration(Model model)
  {
    model.addAttribute("userForm", new User());

    return "register";
  }
  
  @PostMapping("register")
  public String registration(@Valid @ModelAttribute("userForm") User userForm, BindingResult bindingResult, Model model)
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
    Page<Post> posts = postService.findAllByUserId(userId, 0);
    List<Post> postList = posts.getContent();
    
    model.addAttribute("posts", postList);
    return "userPosts";
  }
  
  @GetMapping("/user/{userId}/posts/{page}")
  public String getUserPosts(@PathVariable Integer userId, @PathVariable Integer page, Model model)
  {
    Page<Post> posts = postService.findAllByUserId(userId, page);
    List<Post> postList = posts.getContent();
    
    model.addAttribute("posts", postList);
    return "userPosts";
  }
  
  @GetMapping("/user/{userId}/comments")
  public String getUserComments(@PathVariable Integer userId, Model model)
  {
    return "";
  }
  
  @GetMapping("/user/{userId}/comments/{page}")
  public String getUserComments(@PathVariable Integer userId, @PathVariable Integer page, Model model)
  {
    return "";
  }
}
