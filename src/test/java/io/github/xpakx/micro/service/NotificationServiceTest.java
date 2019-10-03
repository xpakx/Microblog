package io.github.xpakx.micro.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import io.github.xpakx.micro.repository.NotificationRepository;
import io.github.xpakx.micro.entity.Post;
import io.github.xpakx.micro.entity.User;
import io.github.xpakx.micro.entity.Notification;
import io.github.xpakx.micro.error.NotFoundException;
import io.github.xpakx.micro.error.UserUnauthorized;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import static org.mockito.BDDMockito.*;
import static org.junit.Assert.*;
import org.mockito.MockitoAnnotations;
import static org.hamcrest.Matchers.*;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.mockito.ArgumentCaptor;


import static org.hamcrest.Matchers.any;

import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.List;
import java.util.ArrayList;

@RunWith(SpringRunner.class)
public class NotificationServiceTest 
{

  @Mock
  private NotificationRepository notificationRepository;
  
  @Mock
  private UserService userService;
  
  @Mock
  private PasswordEncoder encoder;
  
  @InjectMocks
  private NotificationService notificationService;

  @BeforeEach
  public void setup() 
  {
    MockitoAnnotations.initMocks(this);
  }
  
  @Test
  public void shouldFindAndUpdateAllUserNotifications() throws Exception 
  {
    //given
    List<Notification> notifs = new ArrayList<>();
    Notification notif1 = new Notification();
    notif1.setVisited(false);
    notif1.setId(1);
    Notification notif2 = new Notification();
    notif2.setVisited(false);
    notif2.setId(2);
    Notification notif3 = new Notification();
    notif3.setVisited(false);
    notif3.setId(3);
    notifs.add(notif1);
    notifs.add(notif2);
    notifs.add(notif3);
    
    given(notificationRepository.findAllByUserIdAndVisited(anyInt(), anyBoolean()))
    .willReturn(notifs);
    
    //when
    List<Notification> result = notificationService.findAllByUserId(1);
    
    //then
    then(notificationRepository)
    .should(times(1))
    .findAllByUserIdAndVisited(1, false);
    then(notificationRepository)
    .should(times(1))
    .save(notif1);
    then(notificationRepository)
    .should(times(1))
    .save(notif2);
    then(notificationRepository)
    .should(times(1))
    .save(notif3);
    then(notificationRepository).shouldHaveNoMoreInteractions();
    
    assertThat(result, hasItems(notif1, notif2, notif3));
    assertThat(result, hasSize(3));
  }
  
  @Test
  public void shouldReturnNotificationCount() throws Exception 
  {
    //given
    given(notificationRepository.countByUserIdAndVisited(anyInt(), anyBoolean()))
    .willReturn(25);
    
    //when
    Integer result = notificationService.getNewNotificationsCount(1);
    
    //then
    then(notificationRepository)
    .should(times(1))
    .countByUserIdAndVisited(1, false);
    then(notificationRepository).shouldHaveNoMoreInteractions();
    
    assertThat(result, is(25));
  }
  
  @Test
  public void shouldntAddMentions() throws Exception
  {
    //given
    Post post = new Post();
    User caller = new User();
    String message = "test message";
    
    //when
    notificationService.addMentions(message, post, caller);
    
    //then
    then(notificationRepository).shouldHaveZeroInteractions();
    then(userService).shouldHaveZeroInteractions();
  }
  
  @Test
  public void shouldAddMentions() throws Exception
  {
    //given
    Post post = new Post();
    User caller = new User();
    User user = new User();
    String message = "test message @User @Admin @NotUser @Test";
    given(userService.findByUsernameIgnoreCase("User"))
    .willReturn(Optional.of(user));
    given(userService.findByUsernameIgnoreCase("Admin"))
    .willReturn(Optional.of(user));
    given(userService.findByUsernameIgnoreCase("NotUser"))
    .willReturn(Optional.empty());
    given(userService.findByUsernameIgnoreCase("Test"))
    .willReturn(Optional.of(user));
    
    //when
    notificationService.addMentions(message, post, caller);
    
    //then
    then(userService)
    .should(times(4))
    .findByUsernameIgnoreCase(anyString());
  }
  
  
}
