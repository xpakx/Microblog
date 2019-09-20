package io.github.xpakx.micro.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import org.hibernate.validator.constraints.NotEmpty;
import javax.persistence.ManyToMany;

import java.util.List;

@Entity
@Table(name="role")
@Data
public class UserRole
{
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Integer id;
  
  @Column(nullable = false, unique = true)
  @NotEmpty
  private String name;
  
  @ManyToMany(mappedBy = "roles")
  private List<User> users;
}
