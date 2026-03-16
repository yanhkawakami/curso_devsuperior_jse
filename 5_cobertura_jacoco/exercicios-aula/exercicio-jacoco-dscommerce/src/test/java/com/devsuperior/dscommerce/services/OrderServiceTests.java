package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.dto.OrderDTO;
import com.devsuperior.dscommerce.entities.Order;
import com.devsuperior.dscommerce.entities.OrderItem;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.repositories.OrderItemRepository;
import com.devsuperior.dscommerce.repositories.OrderRepository;
import com.devsuperior.dscommerce.repositories.ProductRepository;
import com.devsuperior.dscommerce.services.exceptions.ForbiddenException;
import com.devsuperior.dscommerce.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscommerce.tests.OrderFactory;
import com.devsuperior.dscommerce.tests.ProductFactory;
import com.devsuperior.dscommerce.tests.UserFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class OrderServiceTests {

    @InjectMocks
    private OrderService service;

    @Mock
    private OrderRepository repository;

    @Mock
    private AuthService authService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private UserService userService;

    private Long existingOrderId;
    private Long nonExistingOrderId;

    private Long existingProduct;
    private Long nonExistingProduct;

    private Order order;
    private OrderDTO orderDTO;

    private Product product;

    private User admin;
    private User self;

    @BeforeEach
    void setUp(){

        existingOrderId = 1L;
        nonExistingOrderId = 2L;

        existingProduct = 1L;
        nonExistingProduct = 2L;

        admin = UserFactory.createCustomAdminUser(1L, "admin");
        self = UserFactory.createCustomClientUser(2L, "self");

        order = OrderFactory.createOrder(self);
        product = ProductFactory.createProduct();

        orderDTO = new OrderDTO(order);

        Mockito.when(repository.findById(existingOrderId)).thenReturn(Optional.ofNullable(order));
        Mockito.when(repository.findById(nonExistingOrderId)).thenThrow(ResourceNotFoundException.class);

        Mockito.when(productRepository.getReferenceById(existingProduct)).thenReturn(product);
        Mockito.when(productRepository.getReferenceById(nonExistingProduct)).thenThrow(EntityNotFoundException.class);

        Mockito.when(repository.save(any())).thenReturn(order);

        Mockito.when(orderItemRepository.saveAll(any())).thenReturn(new ArrayList<>(order.getItems()));
    }

    @Test
    public void findByIdShouldReturnOrderDTOWhenOrderExistsAndAdminLogged(){
        Mockito.doNothing().when(authService).validateSelfOrAdmin(any());

        orderDTO = service.findById(existingOrderId);

        Assertions.assertNotNull(orderDTO);
        Assertions.assertEquals(existingOrderId, orderDTO.getId());
        Assertions.assertEquals(self.getId(), orderDTO.getClient().getId());
    }

    @Test
    public void findByIdShouldReturnOrderDTOWhenOrderExistsAndSelfLogged() {
        Mockito.doNothing().when(authService).validateSelfOrAdmin(any());

        orderDTO = service.findById(existingOrderId);

        Assertions.assertNotNull(orderDTO);
        Assertions.assertEquals(existingOrderId, orderDTO.getId());
    }

    @Test
    public void findByIdShouldThrowsForbidenExceptionWhenIdExistsAndOtherLogged(){
        Mockito.doThrow(ForbiddenException.class).when(authService).validateSelfOrAdmin(any());

        Assertions.assertThrows(ForbiddenException.class, () -> {
            service.findById(existingOrderId);
        });
    }

    @Test
    public void findByIdShouldThrowsResourceNotFoundExceptionWhenOrderDoesNotExist(){
        Mockito.doNothing().when(authService).validateSelfOrAdmin(any());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistingOrderId);
        });
    }

    @Test
    public void insertShouldReturnOrderDTOWhenAdminLogged(){
        Mockito.when(userService.authenticated()).thenReturn(admin);

        OrderDTO result = service.insert(orderDTO);

        Assertions.assertNotNull(result);

    }

    @Test
    public void insertShouldReturnOrderDTOWhenClientLogged(){
        Mockito.when(userService.authenticated()).thenReturn(self);

        OrderDTO result = service.insert(orderDTO);

        Assertions.assertNotNull(result);

    }

    @Test
    public void insertShouldThrowsUsernameNotFoundExceptionWhenUserNotLogged(){
        Mockito.doThrow(UsernameNotFoundException.class).when(userService).authenticated();

        order.setClient(new User());
        orderDTO = new OrderDTO(order);

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            service.insert(orderDTO);
        });
    }

    @Test
    public void insertShouldThrowsEntityNotFoundExceptionWhenOrderProductIdDoesNotExist() {
        Mockito.when(userService.authenticated()).thenReturn(self);

        product.setId(nonExistingProduct);
        OrderItem orderItem = new OrderItem(order, product, 2, 10.0);
        order.getItems().add(orderItem);

        orderDTO = new OrderDTO(order);

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            service.insert(orderDTO);
        });
    }

}
