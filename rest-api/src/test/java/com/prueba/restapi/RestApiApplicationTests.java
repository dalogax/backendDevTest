package com.prueba.restapi;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class RestApiApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void similarIdsSholdReturnListOfIntger() throws Exception {
		this.mockMvc.perform(get("/product/1/similar")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0]", is(2)))
				.andExpect(jsonPath("$[1]", is(3)))
				.andExpect(jsonPath("$[2]", is(4)));
	}

	@Test
	public void similarIdsSholdReturnNotFound() throws Exception {
		this.mockMvc.perform(get("/product/7/similar")).andDo(print()).andExpect(status().isNotFound())
				.andExpect(content().string("Not found code returned from url"));
	}

	@Test
	public void findByIdSholdReturnProductObject() throws Exception {
		this.mockMvc.perform(get("/product/1")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.name", is("Shirt")))
				.andExpect(jsonPath("$.price", is(9.99)))
				.andExpect(jsonPath("$.availability", is(true)));
	}

	@Test
	public void findByIdSholdReturnInternalServerError() throws Exception {
		this.mockMvc.perform(get("/product/6")).andDo(print()).andExpect(status().isInternalServerError());
	}

	@Test
	public void findByIdSholdReturnNotFound() throws Exception {
		this.mockMvc.perform(get("/product/7")).andDo(print()).andExpect(status().isNotFound());
	}
}
