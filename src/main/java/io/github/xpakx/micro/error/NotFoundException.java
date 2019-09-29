package io.github.xpakx.micro.error;

public class NotFoundException extends RuntimeException
{
  public NotFoundException(String message) 
  {
    super(message);
  }
}
