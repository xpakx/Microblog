package io.github.xpakx.micro.security;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecurityTest 
{
  private WebApplicationContext context;

  @Autowired
  public void setWebApplicationContext(WebApplicationContext context) 
  {
    this.context = context;
  }

  private MockMvc mockMvc;

  @Before
  public void setup() 
  {
    mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();
  }

  @Test
  public void adminUnavailableForAll() throws Exception 
  {
    //given
    mockMvc
    
    //when
    .perform(get("/admin"))
    
    //then
    .andExpect(status().is3xxRedirection())
    .andExpect(redirectedUrlPattern("**/login"));
  }

  @Test
  @WithMockUser
  public void adminUnavailableForUserNonAdmin() throws Exception 
  {
    //given
    mockMvc
    
    //when
    .perform(get("/admin"))
    
    //then
    .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles="ADMIN")
  public void adminAvailableForAdmin() throws Exception 
  {
    //given
    mockMvc
    
    //when
    .perform(get("/admin"))
    
    //then
    .andExpect(status().isOk());
  }

  @Test
  public void loginAvailableForAll() throws Exception 
  {
    //given
    mockMvc
    
    //when
    .perform(get("/login"))
    
    //then
    .andExpect(status().isOk());
  }
  
  @Test
  public void mainAvailableForAll() throws Exception 
  {
    //given
    mockMvc
    
    //when
    .perform(get("/"))
    
    //then
    .andExpect(status().isOk());
  }
  
  
  @Test
  public void userCanLog() throws Exception 
  {
  
    //given
    mockMvc
    
    //when
    .perform(formLogin().user("admin@gmail.com").password("aaaaaa"))
    
    //then
    .andExpect(status().isFound())
    .andExpect(redirectedUrl("/home"))
    .andExpect(authenticated().withUsername("Admin"));


    //given
    mockMvc
    
    //when
    .perform(logout())
    
    //then
    .andExpect(status().isFound())
    .andExpect(redirectedUrl("/login?logout"));
  }
  
  
  @Test
  public void invalidLoginDenied() throws Exception 
  {
    //given
    mockMvc
    
    //when
    .perform(formLogin().user("user").password("bbbbbb"))
    
    
    //then
    .andExpect(status().isFound())
    .andExpect(redirectedUrl("/login?error"))
    .andExpect(unauthenticated());
  }

}
