package com.devsuperior.dscommerce.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.devsuperior.dscommerce.tests.TokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OrderControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenUtil tokenUtil;

    private long existingOrderId, nonExistingOrderId, clientOrderId, nonClientOrderId;
    private String adminToken, clientToken;
    private String adminUser, adminPassword, clientUser, clientPassword;

    @BeforeEach
    void setUp () throws Exception {
        existingOrderId = 1L;
        nonExistingOrderId = 4L;
        clientOrderId = 1L;
        nonClientOrderId = 2L;

        adminUser = "alex@gmail.com";
        adminPassword = "123456";
        clientUser = "maria@gmail.com";
        clientPassword = "123456";

        adminToken = tokenUtil.obtainAccessToken(mockMvc, adminUser, adminPassword);
        clientToken = tokenUtil.obtainAccessToken(mockMvc, clientUser, clientPassword);

    }

    @Test
    public void findByIdShouldReturnOrderWhenIdExistsAndAdminLogged() throws Exception {
        ResultActions result = mockMvc
                .perform(get("/orders/{id}", existingOrderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.status").value("PAID"));
        result.andExpect(jsonPath("$.client.id").value(1));
    }

    @Test
    public void findByIdShouldReturnOrderWhenIdExistsAndClientLoggedAndClientOwnsOrder() throws Exception {
        ResultActions result = mockMvc
                .perform(get("/orders/{id}", clientOrderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + clientToken)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.status").value("PAID"));
        result.andExpect(jsonPath("$.client.id").value(1));
    }

    @Test
    public void findByIdShouldReturnOrderWhenIdExistsAndClientLoggedAndClientDoesNotOwnOrder() throws Exception {
        ResultActions result = mockMvc
                .perform(get("/orders/{id}", nonClientOrderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + clientToken)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isForbidden());
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenOrderDoesNotExistAndAdminLogged() throws Exception {
        ResultActions result = mockMvc
                .perform(get("/orders/{id}", nonExistingOrderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenOrderDoesNotExistAndClientLogged() throws Exception {
        ResultActions result = mockMvc
                .perform(get("/orders/{id}", nonExistingOrderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + clientToken)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    public void findByIdShouldReturnUnauthorizedWhenNotLogged() throws Exception {
        ResultActions result = mockMvc
                .perform(get("/orders/{id}", nonExistingOrderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnauthorized());
    }


}
