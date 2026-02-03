package com.devsuperior.examplemockspy.service;

import com.devsuperior.examplemockspy.dto.ProductDTO;
import com.devsuperior.examplemockspy.entities.Product;
import com.devsuperior.examplemockspy.repositories.ProductRepository;
import com.devsuperior.examplemockspy.services.ProductService;
import com.devsuperior.examplemockspy.services.exceptions.InvalidDataException;
import com.devsuperior.examplemockspy.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.swing.*;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    private Long existingId;
    private Long nonExistingId;
    private Product oldProduct;
    private Product product;
    private ProductDTO validProductDTO;


    @Test
    void contextLoads() {
    }

    @BeforeEach
    void setUp() throws Exception{

        existingId = 1L;
        nonExistingId = 2L;
        oldProduct = new Product(1L, "Carro", 30000.0);
        product = new Product(1L, "TV", 3000.0);
        validProductDTO = new ProductDTO(1L, "TV", 3000.0);

        Mockito.when(repository.getReferenceById(ArgumentMatchers.any())).thenReturn(oldProduct);
        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);

        Mockito.when(repository.getReferenceById(existingId)).thenReturn(product);
        Mockito.when(repository.getReferenceById(nonExistingId)).thenThrow(ResourceNotFoundException.class);
    }

    @Test
    public void insertShouldReturnProductDTOWhenValidData(){
        ProductService serviceSpy = Mockito.spy(service);
        Mockito.doNothing().when(serviceSpy).validateData(validProductDTO);

        ProductDTO result = serviceSpy.insert(validProductDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("TV", result.getName());


    }

    @Test
    public void insertShouldInsertValidProduct() {
        ProductDTO dto = service.insert(validProductDTO);

        Assertions.assertEquals(1L, dto.getId());
        Assertions.assertEquals("TV", dto.getName());
        Assertions.assertEquals(3000.0, dto.getPrice());
    }

    @Test
    public void insertShouldNotInsertInvalidProduct() {
        ProductDTO dto = new ProductDTO(1L, "", 3000.0);

        Assertions.assertThrows(InvalidDataException.class, () -> {
            service.insert(dto);
        });

        Mockito.verify(repository, Mockito.times(0)).save(product);
    }

    @Test
    public void updateShouldUpdateWhenValidProduct() {
        ProductDTO dto = service.update(1L, validProductDTO);

        Assertions.assertEquals(1L, dto.getId());
        Assertions.assertEquals("TV", dto.getName());
        Assertions.assertEquals(3000.0, dto.getPrice());
    }

    @Test
    public void updateShouldNotUpdateWhenInvalidProductName() {
        ProductDTO dto = new ProductDTO(1L, "", 3000.0);

        Assertions.assertThrows(InvalidDataException.class, () -> {
            service.update(1L, dto);
        });

        Mockito.verify(repository, Mockito.times(0)).save(product);
    }

    @Test
    public void updateShouldNotUpdateWhenInvalidProductPrice() {
        ProductDTO dto = new ProductDTO(1L, "TV", -1.0);

        Assertions.assertThrows(InvalidDataException.class, () -> {
            service.update(1L, dto);
        });

        Mockito.verify(repository, Mockito.times(0)).save(product);
    }

    @Test
    public void updateShouldNotUpdateWhenIdDoesNotExists() {
        ProductDTO dto = new ProductDTO(2L, "Caneca", 30.0);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.update(2L, dto);
        });
    }
}
