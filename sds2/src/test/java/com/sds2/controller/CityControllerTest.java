package com.sds2.controller;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.sds2.dto.CityDTO;
import com.sds2.repository.CityRepository;
import com.sds2.service.CityService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

@WebMvcTest(CityController.class)
class CityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CityService cityService;

    @Test
    void testGetCityByPattern() throws Exception {
        when(cityService.getCity("Paris"))
            .thenReturn(List.of(new CityDTO("Paris", "France", 48.85341, 2.3488)));

        mockMvc.perform(get("/city/Paris"))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(content().string("[{\"name\":\"Paris\",\"country\":\"France\",\"latitude\":48.85341,\"longitude\":2.3488}]"));
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
		.andExpect(content().string("[]"));
	}
}

