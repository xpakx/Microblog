package io.github.xpakx.micro.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

import io.github.xpakx.micro.entity.User;
import io.github.xpakx.micro.entity.UserRole;
import io.github.xpakx.micro.repository.UserRepository;
import io.github.xpakx.micro.repository.UserRoleRepository;
import io.github.xpakx.micro.error.UserNotFound;

@Service
public class UserService
{
  private UserRepository userRepository;
  private UserRoleRepository roleRepository;
  
  @Autowired
  public UserService(UserRepository userRepository)
  {
    this.userRepository = userRepository;
  }
  
  public User findById(Integer i)
  {
    return userRepository.findById(i).orElse(null);
  }
  
  public User findByUsername(String email)
  {
    return userRepository.findByUsername(email).orElse(null);
  }
  
  public void save(User user)
  {
  
  }
  

}
