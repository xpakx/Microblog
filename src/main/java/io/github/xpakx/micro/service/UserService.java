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
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService
{
  private UserRepository userRepository;
  private UserRoleRepository roleRepository;
  private PasswordEncoder passwordEncoder;
  
  @Autowired
  public UserService(UserRepository userRepository, UserRoleRepository roleRepository, PasswordEncoder passwordEncoder)
  {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
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
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    UserRole role = roleRepository.findByName("ROLE_USER").orElse(null);
    List<UserRole> roles = new ArrayList<>();
    roles.add(role);
    user.setRoles(roles);
    userRepository.save(user);
  }
  

}
