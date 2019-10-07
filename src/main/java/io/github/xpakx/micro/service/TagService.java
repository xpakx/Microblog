package io.github.xpakx.micro.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

import io.github.xpakx.micro.entity.Post;
import io.github.xpakx.micro.entity.User;
import io.github.xpakx.micro.entity.Tag;
import io.github.xpakx.micro.repository.TagRepository;
import io.github.xpakx.micro.error.NotFoundException;
import io.github.xpakx.micro.error.UserUnauthorized;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class TagService
{
  private TagRepository tagRepository;
  
  @Autowired
  public TagService(TagRepository tagRepository)
  {
    this.tagRepository = tagRepository;
  }
  
  public void addTags(String message, Post post)
  {
    List<String> allMentions = new ArrayList<String>();
    Matcher m = Pattern.compile("(\\s|\\A|>)#(\\w+)")
     .matcher(message);
    post.setTags(new ArrayList<>());
     
    while (m.find()) 
    {
      allMentions.add(m.group(2));
    }
      
    allMentions.stream()
      .map(tag -> tag.toLowerCase())
      .distinct()
      .forEach(tag -> processTag(tag, post));
  }
  
  private void processTag(String name, Post post)
  {
     Optional<Tag> tagFound = tagRepository.findByName(name);
     Tag tag;
     if(tagFound.isPresent())
     {
       tag = tagFound.get();
     }
     else 
     {
       tag = new Tag();
       tag.setName(name);
       tag.setId(null);
       tagRepository.save(tag);
     }
     post.getTags().add(tag);
  }
  
  

}
