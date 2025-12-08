package com.devsuperior.dscatalog.resources;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.factory.ProductFactory;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    ProductService service;

    @Autowired
    ObjectMapper objectMapper;

    private PageImpl<ProductDTO> page;
    private ProductDTO productDto;
    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;
    private String jsonBody;

    @BeforeEach
    void setUp() throws Exception {
        productDto = ProductFactory.createProductDTO();
        page = new PageImpl<>(List.of(productDto));
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;
        jsonBody = objectMapper.writeValueAsString(productDto);

        Mockito.when(service.findAll(ArgumentMatchers.any(Pageable.class))).thenReturn(page);

        Mockito.when(service.findById(existingId)).thenReturn(productDto);
        Mockito.when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        Mockito.when(service.put(ArgumentMatchers.eq(existingId), ArgumentMatchers.any(ProductDTO.class))).thenReturn(productDto);
        Mockito.when(service.put(ArgumentMatchers.eq(nonExistingId), ArgumentMatchers.any(ProductDTO.class))).thenThrow(ResourceNotFoundException.class);

        Mockito.doNothing().when(service).delete(existingId);
        Mockito.doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
        Mockito.doThrow(DatabaseException.class).when(service).delete(dependentId);

        Mockito.when(service.insert(ArgumentMatchers.any(ProductDTO.class))).thenReturn(productDto);
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

    @Test
    public void putShouldReturnProductWhenIdExists() throws Exception {
        ResultActions result =
                mockMvc.perform(put("/products/{id}", existingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void putShouldReturnNotFoundWhenDoesNotIdExists() throws Exception {
        ResultActions result =
                mockMvc.perform(put("/products/{id}", nonExistingId, productDto)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.id").doesNotExist());
        result.andExpect(jsonPath("$.name").doesNotExist());
        result.andExpect(jsonPath("$.description").doesNotExist());
    }

    @Test
    public void insertShouldCreateAndReturnProductDto() throws Exception {
        ResultActions result =
                mockMvc.perform(post("/products", productDto)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void deleteShouldDeleteExistingId() throws Exception {
        ResultActions result =
                mockMvc.perform(delete("/products/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNoContent());
    }

    @Test
    public void deleteShouldThrowNotFoundExceptionWhenIdDoesNotExist() throws Exception {
        ResultActions result =
                mockMvc.perform(delete("/products/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.id").doesNotExist());
        result.andExpect(jsonPath("$.name").doesNotExist());
        result.andExpect(jsonPath("$.description").doesNotExist());
    }

}
