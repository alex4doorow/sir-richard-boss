package ru.sir.richard.boss.web.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class SecuredControllerWebMvcIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @WithMockUser(value = "user1")
    @Test
    public void testGivenAuthRequestOnPrivateService_shouldSucceedWith200() throws Exception {
    	mockMvc.perform(get("/orders/9776/orders")
    			.contentType(MediaType.APPLICATION_JSON))
          		.andExpect(status().isOk());
    }
    
    @Test
    public void testGivenAuthRequestOnPrivateService_shouldSucceedWith300x() throws Exception {
        mockMvc.perform(get("/orders/9776/orders"))
        		.andExpect(status().is3xxRedirection());
    }

}
