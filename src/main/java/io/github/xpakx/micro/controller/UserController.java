package io.github.xpakx.micro.controller;

import org.springframework.stereotype.Controller;
import io.github.xpakx.micro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import io.github.xpakx.micro.entity.Post;
import io.github.xpakx.micro.entity.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import javax.validation.Valid;


@Controller
public class UserController
{
  private UserService userService;
  
  @Autowired
  public UserController(UserService userService)
  {
    this.userService = userService;
  }
  
  @GetMapping("register")
  public String registration(Model model)
  {
    model.addAttribute("userForm", new User());

    return "register";
  }
  
  @PostMapping("register")
  public String registration(@Valid @ModelAttribute("userForm") User userForm, Model model, HttpServletRequest request)
  {
    if(!userForm.getPassword().equals(userForm.getConfirmPassword()))
    {
      model.addAttribute("userForm", userForm);
      model.addAttribute("msg", "Passwords specified must be identical!");
      return "register";
    }
    User user = userService.findByUsername(userForm.getUsername());
    if(user != null)
    {
      model.addAttribute("userForm", userForm);
      model.addAttribute("msg", "User with specified username exists!");
      return "register";
    }
    user = userService.findByEmail(userForm.getEmail());
    if(user != null)
    {
      model.addAttribute("userForm", userForm);
      model.addAttribute("msg", "User with specified email exists!");
      return "register";
    }
    
    userForm.setId(null);
    
    userService.save(userForm);
    
    return "redirect:/login";
  }
}
