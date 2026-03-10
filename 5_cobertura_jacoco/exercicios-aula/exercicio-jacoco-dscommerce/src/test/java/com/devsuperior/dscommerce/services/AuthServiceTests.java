package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.services.exceptions.ForbiddenException;
import com.devsuperior.dscommerce.tests.UserFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class AuthServiceTests {

    @InjectMocks
    AuthService service;

    @Mock
    private UserService userService;

    private User admin, self, other;
    @BeforeEach
    void setUp(){
        admin = UserFactory.createAdminUser();
        self = UserFactory.createClientUser();
        other = UserFactory.createCustomClientUser(3L, "user@email.com");
    }

    @Test
    public void validateSelfOrAdminShouldReturnWhenUserIsAdmin() {
        Mockito.when(userService.authenticated()).thenReturn(admin);

        Assertions.assertDoesNotThrow(() -> {
            service.validateSelfOrAdmin(admin.getId());
        });
    }

    @Test
    public void validateSelfOrAdminShouldReturnWhenUserIsSelf() {
        Mockito.when(userService.authenticated()).thenReturn(self);

        Assertions.assertDoesNotThrow(() -> {
            service.validateSelfOrAdmin(self.getId());
        });
    }

    @Test
    public void validateSelfOrAdminShouldThrowForbiddenExceptionWhenUserIsOther() {
        Mockito.when(userService.authenticated()).thenReturn(other);
        Assertions.assertThrows(ForbiddenException.class, () -> {
            service.validateSelfOrAdmin(self.getId());
        });
    }

}
