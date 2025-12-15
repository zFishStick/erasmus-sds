package com.sds2.controller;

import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.sds2.service.AmadeusAuthService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AmadeusPageController.class)
class AmadeusPageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AmadeusAuthService amadeusService;

    @Test
    void testGetAccessToken() throws Exception {
        mockMvc.perform(get("/amadeus/access-token"))
               .andExpect(status().isOk())
               .andExpect(content().string(notNullValue()));
    }
}

