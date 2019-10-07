package io.github.xpakx.micro.entity;

import javax.persistence.Entity;
import lombok.Data;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;

import javax.persistence.ManyToOne;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import org.hibernate.validator.constraints.NotEmpty;
import javax.persistence.Transient;
import java.util.List;
import javax.validation.constraints.Size;

import javax.persistence.ManyToMany;
import javax.persistence.CascadeType;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;

@Entity
@Data
public class Post
{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  
  @Column(nullable = false, length=20000)
  @Size(max=20000, message="Message cannot be longer than 20000 characters!")
  @NotEmpty
  private String message;


  @ManyToOne(fetch=FetchType.LAZY, optional=false)
  @JoinColumn(name="user_id", nullable=false)
  @OnDelete(action=OnDeleteAction.CASCADE)
  private User user;  
  
  @Transient
  private List<Comment> comments;
  
  @ManyToMany(cascade = CascadeType.MERGE)
  @JoinTable(name="post_tag", 
             joinColumns={@JoinColumn(name="post_id",  referencedColumnName="id")},
             inverseJoinColumns={@JoinColumn(name="tag_id",  referencedColumnName="id")})
  private List<Tag> tags;
}
