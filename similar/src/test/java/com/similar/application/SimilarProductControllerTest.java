package com.similar.application;

import com.similar.domain.SimilarProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@ExtendWith(MockitoExtension.class)
class SimilarProductControllerTest {

    private MockMvc mvc;

    @Mock
    private SimilarProductService similarProductService;

    @InjectMocks
    private SimilarProductController sut;

    @BeforeEach
    public void setup(){
        mvc = MockMvcBuilders.standaloneSetup(sut).build();
    }

    @Test
    public void returnsSimilarProductsById() throws Exception {
        // given
        given(similarProductService.getSimilarProductBy(1L)).willReturn(Collections.emptyList());

        // when
        MockHttpServletResponse response = mvc.perform(
                        get("/product/1/similar")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(similarProductService).getSimilarProductBy(1L);
    }


}