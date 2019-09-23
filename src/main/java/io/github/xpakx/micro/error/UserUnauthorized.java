package io.github.xpakx.micro.error;

public class UserUnauthorized extends RuntimeException
{
  public UserUnauthorized(String message) 
  {
    super(message);
  }
}
