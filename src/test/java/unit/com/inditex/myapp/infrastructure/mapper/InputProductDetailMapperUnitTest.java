package unit.com.inditex.myapp.infrastructure.mapper;

import com.inditex.myapp.application.model.ProductDetailResponse;
import com.inditex.myapp.infrastructure.mapper.InputProductDetailMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class InputProductDetailMapperUnitTest {

    @InjectMocks
    private InputProductDetailMapper inputProductDetailMapper;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void mapUnitTest() {
        inputProductDetailMapper.map(new ProductDetailResponse());

        Mockito.verify(modelMapper).map(Mockito.any(), Mockito.any());
    }
}
