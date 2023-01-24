package unit.com.inditex.myapp.application.mapper;

import com.inditex.myapp.application.mapper.OutputProductDetailMapper;
import com.inditex.myapp.domain.model.ProductDetail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class OutputProductDetailMapperUnitTest {

    @InjectMocks
    private OutputProductDetailMapper outputProductDetailMapper;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void mapUnitTest() {
        outputProductDetailMapper.map(new ProductDetail());

        Mockito.verify(modelMapper).map(Mockito.any(), Mockito.any());
    }

    @Test
    void mapListUnitTest() {
        List<ProductDetail> productDetailList = Collections.singletonList(new ProductDetail());

        outputProductDetailMapper.map(productDetailList);

        Mockito.verify(modelMapper, Mockito.times(productDetailList.size())).map(Mockito.any(), Mockito.any());
    }

}
