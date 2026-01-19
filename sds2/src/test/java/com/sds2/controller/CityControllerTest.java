package com.sds2.controller;

import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sds2.repository.CityRepository;

@SpringBootTest
@AutoConfigureMockMvc
class CityControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void testGetCityByPattern() throws Exception {
		this.mockMvc
        .perform(get("/city/Paris"))
        .andDo(print())
        .andExpect(status().isOk())
		.andExpect(content().string(notNullValue()));
	}

	@Mock
    private CityRepository cityRepository;

	@Test
	void cityNotInDB() throws Exception {
		when(cityRepository.save(any())
        ).thenReturn(null);

		this.mockMvc
        .perform(get("/city/CityNotInDB"))
        .andDo(print())
        .andExpect(status().isOk())
		.andExpect(content().string(notNullValue()));
	}
}
