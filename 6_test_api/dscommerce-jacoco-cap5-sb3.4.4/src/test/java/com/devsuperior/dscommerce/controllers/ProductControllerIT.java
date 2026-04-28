package com.devsuperior.dscommerce.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import com.devsuperior.dscommerce.dto.ProductDTO;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.tests.ProductFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String productName;
    private Product product;
    private ProductDTO productDTO;
    private long existingProductId;
    private long nonExistingProductId;
    private long dependentProductId;


    @BeforeEach
    void setUp (){
        productName = "Macbook";
        product = ProductFactory.createProduct();
        existingProductId = 1L;
        nonExistingProductId = 10000L;
        dependentProductId = 3L;

        productDTO = new ProductDTO(product);
    }

    @Test
    public void findAllShouldReturnPageWhenNameParamIsNotEmpty() throws Exception {
        ResultActions result = mockMvc
                .perform(get("/products?name={productName}", productName)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath(".content[0].id").value(3));
        result.andExpect(jsonPath(".content[0].name").value("Macbook Pro"));
        result.andExpect(jsonPath(".content[0].price").value(1250.0));
        result.andExpect(jsonPath(".content[0].imgUrl").value("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/3-big.jpg"));
    }

    @Test
    public void findAllShouldReturnPageWhenNameParamIsEmpty() throws Exception {
        ResultActions result = mockMvc
                .perform(get("/products?name={productName}", (Object) null)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath(".content[0].id").value(1));
   }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void insertShouldReturnProductDTOCreatedWhenAdminLogged() throws Exception {
        ResultActions result = mockMvc
                .perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO))
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.name").value("Console PlayStation 5"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidProducName() throws Exception {
        product.setName("ab");
        productDTO = new ProductDTO();

        ResultActions result = mockMvc
                .perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO))
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidProducDescription() throws Exception {
        product.setDescription("ab");
        productDTO = new ProductDTO();

        ResultActions result = mockMvc
                .perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO))
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndNegativeProducPrice() throws Exception {
        product.setPrice(-1.0);
        productDTO = new ProductDTO();

        ResultActions result = mockMvc
                .perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO))
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndZeroProducPrice() throws Exception {
        product.setPrice(0.0);
        productDTO = new ProductDTO();

        ResultActions result = mockMvc
                .perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO))
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndHasNoProductCategory() throws Exception {
        product.getCategories().clear();
        productDTO = new ProductDTO();

        ResultActions result = mockMvc
                .perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO))
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(authorities = "ROLE_CLIENT")
    public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {
        ResultActions result = mockMvc
                .perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO))
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isForbidden());
    }

    @Test
    public void insertShouldReturnForbiddenWhenUserIsNotLogged() throws Exception {
        ResultActions result = mockMvc
                .perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO))
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void deleteShouldReturnNoContentWhenProductExistsAndAdminLogged() throws Exception {
        ResultActions result = mockMvc
                .perform(delete("/products/{id}", existingProductId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void deleteShouldReturnNotFoundWhenProductDoesNotExistsAndAdminLogged() throws Exception {
        ResultActions result = mockMvc
                .perform(delete("/products/{id}", nonExistingProductId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "ROLE_CLIENT")
    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteShouldReturnBadRequestWhenProductExistsAndProductIsDependentAndAdminLogged() throws Exception {
        ResultActions result = mockMvc
                .perform(delete("/products/{id}", dependentProductId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(authorities = "ROLE_CLIENT")
    public void deleteShouldReturnForbiddenWhenProductExistsAndClientLogged() throws Exception {
        ResultActions result = mockMvc
                .perform(delete("/products/{id}", existingProductId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isForbidden());
    }

    @Test
    public void deleteShouldReturnUnauthorizedWhenProductExistsAndNoLogin() throws Exception {
        ResultActions result = mockMvc
                .perform(delete("/products/{id}", existingProductId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnauthorized());
    }


}
