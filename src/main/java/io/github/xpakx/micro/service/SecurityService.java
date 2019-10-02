package io.github.xpakx.micro.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.core.Authentication;

@Service
public class SecurityService
{
  @Autowired
  private AuthenticationManager authenticationManager;

  public void autologin(String email, String password, HttpServletRequest request) 
  {    
    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);
    authToken.setDetails(new WebAuthenticationDetails(request));
    
    Authentication authentication = authenticationManager.authenticate(authToken);
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }
}
