package io.github.xpakx.micro.error;

public class UserNotFound extends RuntimeException
{
  public UserNotFound(String message) 
  {
    super(message);
  }
}
