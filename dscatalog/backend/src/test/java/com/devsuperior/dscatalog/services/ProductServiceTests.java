package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.factory.CategoryFactory;
import com.devsuperior.dscatalog.factory.ProductFactory;
import com.devsuperior.dscatalog.projections.ProductProjection;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    // Necessário usar um mock, pois se não, estamos a testar integrado e não por unidade
    // Não podemos usar o @Autowired, se não vai injetar as dependências
    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    @Mock
    private CategoryRepository categoryRepository;

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private PageImpl<Product> page;
    private PageImpl<ProductProjection> pageProjection;
    private Product product;
    private ProductDTO productDto;
    private ProductProjection productProjection;
    private Category category;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;

        product = ProductFactory.createProduct();
        productDto = ProductFactory.createProductDTO();
        productProjection = ProductFactory.createProductProjection();
        page = new PageImpl<>(List.of(product));
        pageProjection = new PageImpl<>(List.of(productProjection));
        category = CategoryFactory.createCategory();

        // Mocka o find all do repository
        Mockito.when(repository.findAll(ArgumentMatchers.any(Pageable.class))).thenReturn(page);

        Mockito.when(repository.searchProducts(ArgumentMatchers.anyList(), ArgumentMatchers.anyString(), ArgumentMatchers.any(Pageable.class))).thenReturn(pageProjection);
        Mockito.when(repository.searchProductsWithCategories(ArgumentMatchers.anyList())).thenReturn(List.of(product));

        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Mockito.when(repository.getReferenceById(existingId)).thenReturn(product);
        Mockito.when(repository.getReferenceById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        Mockito.when(categoryRepository.getReferenceById(existingId)).thenReturn(category);
        Mockito.when(categoryRepository.getReferenceById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        Mockito.doNothing().when(repository).deleteById(existingId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);

        Mockito.when(repository.existsById(existingId)).thenReturn(true);
        Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);
        Mockito.when(repository.existsById(dependentId)).thenReturn(true);
    }

    @Test
    public void findAllShouldReturnPage(){
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductDTO> result = service.findAll("ma", "1,3", pageable);

        Assertions.assertNotNull(result);
        Mockito.verify(repository).searchProducts(List.of(1L, 3L), "ma", pageable);
        Mockito.verify(repository).searchProductsWithCategories(List.of(1L));

    }

    @Test
    public void findByIdShouldReturnExistingProduct(){
        ProductDTO result = service.findById(existingId);

        Assertions.assertNotNull(result);
        Mockito.verify(repository).findById(existingId);
        Assertions.assertEquals(existingId, result.getId());
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists(){
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistingId);
        });
    }

    @Test
    public void putShouldUpdateExistingProduct() {
        ProductDTO result = service.put(1L, productDto);

        Assertions.assertNotNull(result);
        Mockito.verify(repository).save(product);
    }

    @Test
    public void putShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists(){
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.put(nonExistingId, productDto);
        });
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {
        Assertions.assertDoesNotThrow(() -> {
            service.delete(existingId);
        });

        Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
        Assertions.assertThrows(DatabaseException.class, () -> {
            service.delete(dependentId);
        });
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });

        Mockito.verify(repository, Mockito.times(0)).deleteById(existingId);
    }

}
