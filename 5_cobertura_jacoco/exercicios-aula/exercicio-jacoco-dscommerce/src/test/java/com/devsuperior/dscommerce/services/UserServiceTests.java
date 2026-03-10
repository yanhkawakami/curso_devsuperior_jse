package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.dto.UserDTO;
import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.projections.UserDetailsProjection;
import com.devsuperior.dscommerce.repositories.UserRepository;
import com.devsuperior.dscommerce.tests.UserDetailsFactory;
import com.devsuperior.dscommerce.tests.UserFactory;
import com.devsuperior.dscommerce.utils.CustomUserUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;


@ExtendWith(SpringExtension.class)
public class UserServiceTests {

    @InjectMocks
    private UserService service;

    @Mock
    private UserRepository repository;

    @Mock
    private CustomUserUtil customUserUtil;

    private String existingUsername;
    private String nonExistingUsername;

    private User user;

    private List<UserDetailsProjection> userDetails;
    private List<UserDetailsProjection> emptyUserDetails;

    private UserDTO userDTO;

    @BeforeEach
    void setUp(){
        existingUsername = "maria@gmail.com";
        nonExistingUsername = "email@gmail.com";

        user = UserFactory.createCustomClientUser(1L, existingUsername);
        userDetails = UserDetailsFactory.createCustomClientUser(existingUsername);
        emptyUserDetails = UserDetailsFactory.createEmptyUser();

        Mockito.when(repository.searchUserAndRolesByEmail(existingUsername)).thenReturn(userDetails);
        Mockito.when(repository.searchUserAndRolesByEmail(nonExistingUsername)).thenReturn(emptyUserDetails);

        Mockito.when(repository.findByEmail(existingUsername)).thenReturn(Optional.of(user));
        Mockito.when(repository.findByEmail(nonExistingUsername)).thenReturn(Optional.empty());

    }

    @Test
    public void loadByUserNameShouldReturnUserWhenEmailExists() {
        UserDetails result = service.loadUserByUsername(existingUsername);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(user.getUsername(), result.getUsername());
    }

    @Test
    public void loadByUserNameShouldThrowUsernameNotFoundExceptionWhenEmailDoesNotExist() {
        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            service.loadUserByUsername(nonExistingUsername);
        });
    }

    @Test
    public void authenticatedShouldReturnUserWhenEmailExists() {
        Mockito.when(customUserUtil.getLoggerUsername()).thenReturn(existingUsername);

        User result = service.authenticated();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
        Mockito.doThrow(ClassCastException.class).when(customUserUtil).getLoggerUsername();

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            service.authenticated();
        });
    }

    @Test
    public void getMeShouldReturnUserDTOWhenUsernameExists() {
        UserService userService = Mockito.spy(service);
        Mockito.when(customUserUtil.getLoggerUsername()).thenReturn(existingUsername);
        Mockito.doReturn(user).when(userService).authenticated();

        userDTO = service.getMe();

        Assertions.assertNotNull(userDTO);
        Assertions.assertEquals(user.getEmail(), userDTO.getEmail());
    }

    @Test
    public void getMeShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
        UserService userService = Mockito.spy(service);
        Mockito.doReturn(user).when(userService).authenticated();

        Mockito.doThrow(ClassCastException.class).when(customUserUtil).getLoggerUsername();


        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            service.getMe();
        });
    }

}


