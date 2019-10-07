package io.github.xpakx.micro.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

import io.github.xpakx.micro.entity.Post;
import io.github.xpakx.micro.entity.User;
import io.github.xpakx.micro.repository.PostRepository;
import io.github.xpakx.micro.error.NotFoundException;
import io.github.xpakx.micro.error.UserUnauthorized;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;



@Service
public class PostService
{
  private PostRepository postRepository;
  private NotificationService notificationService;
  private TagService tagService;
  
  @Autowired
  public PostService(PostRepository postRepository,  NotificationService notificationService, TagService tagService)
  {
    this.postRepository = postRepository;
    this.notificationService = notificationService;
    this.tagService = tagService;
  }
  
  public Post findById(Integer i)
  {
    return postRepository.findById(i).orElse(null);
  }
  
  public void deletePost(Integer i) throws NotFoundException
  {
    Optional<Post> post =  postRepository.findById(i);
    postRepository.delete(post.orElseThrow(() -> new NotFoundException("User not found!")));
  }
  
  public void deletePost(Integer i, Integer userId) throws NotFoundException, UserUnauthorized
  {
    Optional<Post> post =  postRepository.findById(i);
    Post postToDelete = post.orElseThrow(() -> new NotFoundException("User not found!"));
    if(postToDelete.getUser().getId() != userId) 
    {throw new UserUnauthorized("User unauthorized!");}
    postRepository.delete(postToDelete);
  }
  
  public void updatePost(Integer i, Post post) throws NotFoundException
  {
    Optional<Post> foundPost = postRepository.findById(i);
    Post fPost = foundPost.orElseThrow(() -> new NotFoundException("User not found!"));
    fPost.setMessage(post.getMessage());
    tagService.addTags(fPost.getMessage(), fPost);
    postRepository.save(fPost);
  }
  
  public void updatePost(Integer i, Integer userId, Post post) throws NotFoundException, UserUnauthorized
  {
    Optional<Post> foundPost = postRepository.findById(i);
    Post fPost = foundPost.orElseThrow(() -> new NotFoundException("User not found!"));
    if(fPost.getUser().getId() != userId) 
    {throw new UserUnauthorized("User unauthorized!");}
    fPost.setMessage(post.getMessage());
    tagService.addTags(fPost.getMessage(), fPost);
    postRepository.save(fPost);
  }
  
  public Post addPost(Post post)
  {
    post.setId(null);
    tagService.addTags(post.getMessage(), post);
    postRepository.save(post);
    
    notificationService.addMentions(post.getMessage(), post, post.getUser());
    return post;
  }
    
  public Page<Post> findAll(Integer page)
  {
    Pageable nthPageWith20Elements = PageRequest.of(page, 20, Sort.by("id").descending());
    Page<Post> allPosts = postRepository.findAll(nthPageWith20Elements);
    
    return allPosts;
  }
  
  public Page<Post> findAllByUserId(Integer userId, Integer page)
  {
    Pageable nthPageWith20Elements = PageRequest.of(page, 20, Sort.by("id").descending());
    Page<Post> allPosts = postRepository.findAllByUserId(userId, nthPageWith20Elements);
    
    return allPosts;   
  }
  
  public Page<Post> findAllByTag(String tag, Integer page)
  {
    Pageable nthPageWith20Elements = PageRequest.of(page, 20, Sort.by("id").descending());
    Page<Post> allPosts = postRepository.findAllByTagName(tag, nthPageWith20Elements);
    
    return allPosts;   
  }
  

}
