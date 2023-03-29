package product;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HttpRequestTest {

    @Autowired
    private TestRestTemplate restTemplate;


    @Test
    public void returnProductsInformationTest(){
        String body = restTemplate.getForObject("/product/1/similar", String.class);
        assertThat(body.contains("Blazer"));
    }

    @Test
    public void notReturnProductsInformationTest(){
        String body = restTemplate.getForObject("/product/1/similar", String.class);
        assertThat(!body.contains("Nike"));
    }


}
