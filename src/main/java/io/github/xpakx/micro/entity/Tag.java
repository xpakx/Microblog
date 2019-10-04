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
import javax.persistence.ManyToMany;
import javax.persistence.CascadeType;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;

import org.hibernate.validator.constraints.NotEmpty;
import javax.persistence.Transient;
import java.util.List;
import javax.validation.constraints.Size;

@Entity
@Data
public class Tag
{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  
  private String name;
  
  @ManyToMany(cascade=CascadeType.MERGE)
  @JoinTable(name="post_tag", 
             joinColumns={@JoinColumn(name="tag_id",  referencedColumnName="id")},
             inverseJoinColumns={@JoinColumn(name="post_id",  referencedColumnName="id")})
  private List<Post> posts;  
}
