package io.github.xpakx.micro.controller;

import io.github.xpakx.micro.service.CommentService;
import io.github.xpakx.micro.service.UserService;
import io.github.xpakx.micro.service.PostService;
import io.github.xpakx.micro.entity.Post;
import io.github.xpakx.micro.entity.Comment;
import io.github.xpakx.micro.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.data.domain.Page;
import io.github.xpakx.micro.error.UserNotFound;
import io.github.xpakx.micro.error.UserUnauthorized;
import io.github.xpakx.micro.error.ForbiddenException;
import java.util.stream.Collectors;
import org.springframework.validation.BindingResult;


import java.security.Principal;

@Controller
public class CommentController
{
  private CommentService commentService;
  private UserService userService;
  private PostService postService;
  
  @Autowired
  public void setService(CommentService commentService, UserService userService, PostService postService)
  {
    this.commentService = commentService;
    this.userService = userService;
    this.postService = postService;
  }
  
  @GetMapping("/post/{id}/comment/add")
  public String addComment(Model model)
  {
    model.addAttribute("commentForm", new Comment());

    return "addComment";
  }
  
  @PostMapping("/post/{id}/comment/add")
  public String addComment(@PathVariable Integer id, @ModelAttribute("commentForm") Comment commentForm, BindingResult bindingResult, Principal principal, Model model) throws ForbiddenException
  {    
    User user = userService.findByUsername(principal.getName());
    
    boolean flag=false;
    if(user == null) 
    {
      throw new ForbiddenException();

    }
    else 
    {
      commentForm.setUser(user);
    }
    
    Post post = postService.findById(id);
    if(post == null) 
    {
      bindingResult.rejectValue("message", "error.commentForm", "Post not found"); 
      flag = true;
    }
    else 
    {
      commentForm.setPost(post);
    }
    
    if(commentForm.getMessage() == null || commentForm.getMessage().equals(""))
    {
      bindingResult.rejectValue("message", "error.commentForm", "Message cannot be empty!"); 
      flag = true;
      
    }
    
    if(flag){return "addComment";}
    commentService.addComment(commentForm);
    
    return "redirect:/post/" + id;
  }
    
  @GetMapping("/comment/{id}/edit")
  public String updateComment(@PathVariable Integer id, Model model)
  {
    Comment comment = commentService.findById(id);
    model.addAttribute("comment", comment);
    if(comment == null)
    {
      model.addAttribute("msg", "Comment doesn't exist!");
      model.addAttribute("err", true);
    }
    return "editComment";
  }
  
  @PostMapping("/comment/{id}/edit")
  public String updateComment(@PathVariable Integer id, @ModelAttribute("commentForm") Comment commentForm, BindingResult bindingResult, Model model, Principal principal)
  {
  
    User user = userService.findByUsername(principal.getName());
    
    if(user == null) 
    {
      throw new ForbiddenException();
    }
    
    boolean flag = false;
    
    if(commentForm.getMessage() == null || commentForm.getMessage().equals(""))
    {
      bindingResult.rejectValue("message", "error.commentForm", "Message cannot be empty!"); 
      flag = true;
    }
    
    Integer postId = 0;
    if(user.getRoles() != null && user.getRoles().stream()
                              .map((role) -> role.getName()).collect(Collectors.toList())
                              .contains("ROLE_MOD"))
    {
      try
      {
        postId = commentService.updateComment(id, commentForm);
      }
      catch(UserNotFound e)
      {
        bindingResult.rejectValue("message", "error.commentForm", "Comment not found!"); 
        flag = true;
      }
    }
    else
    {    
      try
      {
        postId = commentService.updateComment(id, user.getId(), commentForm);
      }
      catch(UserNotFound e)
      {
        bindingResult.rejectValue("message", "error.commentForm", "Comment not found!"); 
        flag = true;
      }
      catch(UserUnauthorized e)
      {
        throw new ForbiddenException();
      }    
    }
    
    if(flag)
    {
      return "editComment";
    }
    
    return "redirect:/post/"+ postId;
  }
  
  @GetMapping("/comment/{id}/delete")
  public String deleteComment(@PathVariable Integer id, Model model, Principal principal)
  {
    User user = userService.findByUsername(principal.getName());
    
    if(user == null) 
    {
      throw new ForbiddenException();
    }
    
    Integer postId = 0;
    if(user.getRoles() != null && user.getRoles().stream()
                              .map((role) -> role.getName()).collect(Collectors.toList())
                              .contains("ROLE_MOD"))
    {
      try
      {
        postId = commentService.deleteComment(id);
      }
      catch(UserNotFound e)
      {
        model.addAttribute("msg", "Comment not found!");
        return "delete";
      }
    }
    else
    {    
      try
      {
        postId = commentService.deleteComment(id, user.getId());
      }
      catch(UserNotFound e)
      {
        model.addAttribute("msg", "Comment not found!");
        return "delete";
      }
      catch(UserUnauthorized e)
      {
        throw new ForbiddenException();
      }    
    }
    
    return "redirect:/post/"+ postId;
  }
  
}
