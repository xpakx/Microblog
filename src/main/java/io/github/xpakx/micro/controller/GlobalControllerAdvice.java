package io.github.xpakx.micro.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import javax.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice 
public class GlobalControllerAdvice 
{

  @ExceptionHandler(Exception.class)
  public String defaultErrorHandler(HttpServletRequest request, Exception e, Model model)
  {
    model.addAttribute("exception", e);
    model.addAttribute("url", request.getRequestURL());
    return "error";
  }

}
