package io.github.xpakx.micro.controller;

import io.github.xpakx.micro.service.PostService;
import io.github.xpakx.micro.entity.Post;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.data.domain.Page;


@Controller
public class PostController
{
  private PostService postService;
  
  @Autowired
  public void setService(PostService postService)
  {
    this.postService = postService;
  }
  
  @GetMapping({"/all", "/posts"})
  public String getAllPosts(Model model)
  {
    Page<Post> posts = postService.findAll(0);
    model.addAttribute("posts", posts);
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
    return "test";
  }
  
  @PostMapping("/post/add")
  public String addPost(@ModelAttribute("postForm") Post postForm)
  {
    return "test";
  }
  
  @GetMapping("/post/{id}/edit")
  public String updatePost(@PathVariable Integer id, Model model)
  {
    return "test";
  }
  
  @PostMapping("/post/{id}/edit")
  public String updatePost(@PathVariable Integer id, @ModelAttribute("postForm") Post postForm)
  {
    return "test";
  }
  
  @PostMapping("/post/{id}/delete")
  public String updatePost(@PathVariable Integer id)
  {
    return "test";
  }
  
}
