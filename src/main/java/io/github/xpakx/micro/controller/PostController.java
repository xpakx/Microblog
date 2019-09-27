package io.github.xpakx.micro.controller;

import io.github.xpakx.micro.service.PostService;
import io.github.xpakx.micro.service.UserService;
import io.github.xpakx.micro.service.CommentService;
import io.github.xpakx.micro.entity.Post;
import io.github.xpakx.micro.entity.User;
import io.github.xpakx.micro.entity.Comment;
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
import java.util.List;
import java.util.Collections;

import java.security.Principal;

@Controller
public class PostController
{
  private PostService postService;
  private UserService userService;
  private CommentService commentService;
  
  @Autowired
  public void setService(PostService postService, UserService userService, CommentService commentService)
  {
    this.postService = postService;
    this.userService = userService;
    this.commentService = commentService;
  }

  
  @GetMapping({"/all", "/posts"})
  public String getAllPosts(Model model)
  {
    Page<Post> posts = postService.findAll(0);
    List<Post> postList = posts.getContent();
    
    for(Post post : postList)
    {
      List<Comment> comments = commentService.findTwoByPostId(post.getId()).getContent();
      post.setComments(comments);
    }
    
    model.addAttribute("posts", postList);
    return "posts";
  }
  
  @GetMapping("/posts/{page}")
  public String getAllPosts(@PathVariable Integer page, Model model)
  {
    Page<Post> posts = postService.findAll(page);
    model.addAttribute("posts", posts);
    return "posts";
  }
  
  @GetMapping("/post/add")
  public String addPost(Model model)
  {
    model.addAttribute("postForm", new Post());

    return "addPost";
  }
  
  @PostMapping("/post/add")
  public String addPost(@ModelAttribute("postForm") Post postForm, BindingResult bindingResult, Principal principal, Model model) throws ForbiddenException
  {    
    User user = userService.findByUsername(principal.getName());
    
    if(user == null) 
    {
      throw new ForbiddenException();
    }
    else 
    {
      postForm.setUser(user);
    }
    
    if(postForm.getMessage() == null || postForm.getMessage().equals(""))
    {
      bindingResult.rejectValue("message", "error.postForm", "Message cannot be empty!"); 
      return "addPost";
    }
    
    postService.addPost(postForm);
    
    return "redirect:/posts";
  }
  
  @GetMapping("/post/{id}")
  public String getPost(@PathVariable Integer id, Model model)
  {
    Post post = postService.findById(id);
    List<Comment> comments = commentService.findAllByPostId(post.getId(), 0).getContent();
    post.setComments(comments);
    
    model.addAttribute("post", post);
    return "post";
  }
  
  @GetMapping("/post/{id}/{pageId}")
  public String getPost(@PathVariable Integer id, @PathVariable Integer pageId, Model model)
  {
    Post post = postService.findById(id);
    List<Comment> comments = commentService.findAllByPostId(post.getId(), pageId).getContent();
    post.setComments(comments);
    
    model.addAttribute("post", post);
    return "post";
  }
  
  @GetMapping("/post/{id}/edit")
  public String updatePost(@PathVariable Integer id, Model model)
  {
    Post post = postService.findById(id);
    model.addAttribute("post", post);
    if(post == null)
    {
      model.addAttribute("msg", "Post doesn't exist!");
      model.addAttribute("err", true);
    }
    return "editPost";
  }
  
  @PostMapping("/post/{id}/edit")
  public String updatePost(@PathVariable Integer id, @ModelAttribute("postForm") Post postForm, BindingResult bindingResult, Model model, Principal principal)
  {
  
    User user = userService.findByUsername(principal.getName());
    
    if(user == null) 
    {
      throw new ForbiddenException();
    }
    
    boolean flag = false;
    
    if(postForm.getMessage() == null || postForm.getMessage().equals(""))
    {
      bindingResult.rejectValue("message", "error.postForm", "Message cannot be empty!"); 
      flag = true;
    }
    
    
    if(user.getRoles() != null && user.getRoles().stream()
                              .map((role) -> role.getName()).collect(Collectors.toList())
                              .contains("ROLE_MOD"))
    {
      try
      {
        postService.updatePost(id, postForm);
      }
      catch(UserNotFound e)
      {
        bindingResult.rejectValue("message", "error.postForm", "Post not found!"); 
        flag = true;
      }
    }
    else
    {    
      try
      {
        postService.updatePost(id, user.getId(), postForm);
      }
      catch(UserNotFound e)
      {
        bindingResult.rejectValue("message", "error.postForm", "Post not found!"); 
        flag = true;
      }
      catch(UserUnauthorized e)
      {
        throw new ForbiddenException();
      }    
    }
    
    if(flag)
    {
      return "editPost";
    }
    
    
    return "redirect:/posts";
  }
  
  @GetMapping("/post/{id}/delete")
  public String deletePost(@PathVariable Integer id, Model model, Principal principal)
  {
    User user = userService.findByUsername(principal.getName());
    
    if(user == null) 
    {
      throw new ForbiddenException();
    }
    
    
    if(user.getRoles() != null && user.getRoles().stream()
                              .map((role) -> role.getName()).collect(Collectors.toList())
                              .contains("ROLE_MOD"))
    {
      try
      {
        postService.deletePost(id);
      }
      catch(UserNotFound e)
      {
        model.addAttribute("msg", "Post not found!");
        return "delete";
      }
    }
    else
    {    
      try
      {
        postService.deletePost(id, user.getId());
      }
      catch(UserNotFound e)
      {
        model.addAttribute("msg", "Post not found!");
        return "delete";
      }
      catch(UserUnauthorized e)
      {
        throw new ForbiddenException();
      }    
    }
    
    return "redirect:/posts";
  }
  
}
