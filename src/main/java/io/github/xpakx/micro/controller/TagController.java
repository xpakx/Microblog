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
import io.github.xpakx.micro.error.NotFoundException;
import io.github.xpakx.micro.error.UserUnauthorized;
import io.github.xpakx.micro.error.ForbiddenException;
import java.util.stream.Collectors;
import org.springframework.validation.BindingResult;
import java.util.List;
import java.util.Collections;

import java.security.Principal;
import javax.validation.Valid;

@Controller
public class TagController
{
  private PostService postService;
  private CommentService commentService;
  
  @Autowired
  public void setService(PostService postService, CommentService commentService)
  {
    this.postService = postService;
    this.commentService = commentService;
  }

  
  @GetMapping({"/tag/{tag}"})
  public String getAllPosts(@PathVariable String tag, Model model)
  {
    List<Post> posts = postService.findAllByTag(tag, 0).getContent();
    
    for(Post post : posts)
    {
      List<Comment> comments = commentService.findTwoByPostId(post.getId()).getContent();
      post.setComments(comments);
    }
    
    model.addAttribute("posts", posts);
    return "posts";
  }
  
  @GetMapping("/tag/{tag}/{page}")
  public String getAllPosts(@PathVariable String tag, @PathVariable Integer page, Model model)
  {
    List<Post> posts = postService.findAllByTag(tag, page).getContent();
    
    for(Post post : posts)
    {
      List<Comment> comments = commentService.findTwoByPostId(post.getId()).getContent();
      post.setComments(comments);
    }
    
    model.addAttribute("posts", posts);
    return "posts";
  }
  
  
}
