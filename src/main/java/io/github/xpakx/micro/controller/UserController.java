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
import org.springframework.validation.BindingResult;

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
}
