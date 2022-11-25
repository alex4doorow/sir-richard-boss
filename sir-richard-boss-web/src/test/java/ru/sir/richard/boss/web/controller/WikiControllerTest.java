package ru.sir.richard.boss.web.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class WikiControllerTest {
	
	@Autowired
	private WikiController wikiController;
	
	@Autowired
	private MockMvc mockMvc;

	@WithMockUser(value = "user1")
	@Test
	public void testWikiProductById() throws Exception {
		
		mockMvc.perform(get("/wiki/products/32")).andDo(print()).andExpect(status().isOk())
			.andExpect(view().name("wiki/listproducts"));	
		
	}


}
