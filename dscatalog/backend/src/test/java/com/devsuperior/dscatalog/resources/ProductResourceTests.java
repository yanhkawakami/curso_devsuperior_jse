package com.devsuperior.dscatalog.resources;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.factory.ProductFactory;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    ProductService service;

    private PageImpl<ProductDTO> page;
    private ProductDTO productDto;
    private Long existingId;
    private Long nonExistingId;

    @BeforeEach
    void setUp() throws Exception {
        productDto = ProductFactory.createProductDTO();
        page = new PageImpl<>(List.of(productDto));
        existingId = 1L;
        nonExistingId = 2L;

        Mockito.when(service.findAll(ArgumentMatchers.any(Pageable.class))).thenReturn(page);
        Mockito.when(service.findById(existingId)).thenReturn(productDto);
        Mockito.when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);
    }

    @Test
    public void findAllShoudReturnPage() throws Exception{
        ResultActions result =
                mockMvc.perform(get("/products")
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
    }

    @Test
    public void findByIdShouldReturnProductWhenIdExists() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/products/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenDoesNotIdExists() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/products/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.id").doesNotExist());
        result.andExpect(jsonPath("$.name").doesNotExist());
        result.andExpect(jsonPath("$.description").doesNotExist());
    }
}
