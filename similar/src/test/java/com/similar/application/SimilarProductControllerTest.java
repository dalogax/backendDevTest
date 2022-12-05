package com.similar.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@ExtendWith(MockitoExtension.class)
class SimilarProductControllerTest {

    private MockMvc mvc;

    @InjectMocks
    private SimilarProductController sut;

    @BeforeEach
    public void setup(){
        mvc = MockMvcBuilders.standaloneSetup(sut).build();
    }

    @Test
    public void returnsSimilarProductsById() throws Exception {
        // given

        // when
        MockHttpServletResponse response = mvc.perform(
                        get("/product/1/similar")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }


}