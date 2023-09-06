package com.dev.test.service.implementation;

import java.util.List;

import com.dev.test.domain.aggregate.ProductDetail;
import com.dev.test.domain.exceptions.InternalErrorException;
import com.dev.test.domain.exceptions.NotFoundException;
import com.dev.test.domain.mapper.ProductMapper;
import com.dev.test.mock.MockClient;
import com.dev.test.mock.response.MockClientResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.dev.test.domain.service.ProductService;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {


	private final MockClient mockClient;
	private final ProductMapper productMapper;
	
	private final static String NOT_FOUND_DETAIL_MESSAGE = "Can not found detail of id: %s";
	private final static String NOT_FOUND_SIMILAR_IDS_MESSAGE = "Can not found similar ids of %s";
	private final static String SERVER_ERROR_MESSAGE = "Cannot access the mock client, please try again later or contact the administrator.";
	

	@Override
	public ProductDetail provideDetailById(String productId) {
		try {
			log.info("Call mockClient.findSimilarIds");
			final MockClientResponse mockClientResponse = mockClient.findProductDetail(productId);
			log.info("Successful call mockClient.findSimilarIds");
			if (mockClientResponse == null) {
				log.debug("Detail not found, productId: {}", productId);
				throw new NotFoundException(String.format(NOT_FOUND_DETAIL_MESSAGE, productId));
			}
			return productMapper.asProductDetail(mockClientResponse);
		} catch (HttpClientErrorException e) {
			log.error(e.getMessage());
			throw new NotFoundException(String.format(NOT_FOUND_DETAIL_MESSAGE, productId));
		}catch (HttpServerErrorException | ResourceAccessException e){
			log.error(e.getMessage());
			throw new InternalErrorException(SERVER_ERROR_MESSAGE);
		}
	}

	@Override
	public List<Integer> provideSimilarIdListById(String productId) {
		try {
			log.info("Call mockClient.findSimilarIds");
			final List<Integer> similarIds = mockClient.findSimilarIds(productId);
			log.info("Successful call mockClient.findSimilarIds");
			if(CollectionUtils.isEmpty(similarIds)){
				log.debug("Similar ids not found, productId: {}", productId);
				throw new NotFoundException(String.format(NOT_FOUND_SIMILAR_IDS_MESSAGE, productId));
			}
			return similarIds;
			}catch (HttpClientErrorException e){
				log.error(e.getMessage());
				throw new NotFoundException(String.format(NOT_FOUND_SIMILAR_IDS_MESSAGE, productId));
			}catch (HttpServerErrorException | ResourceAccessException e){
				log.error(e.getMessage());
				throw new InternalErrorException(SERVER_ERROR_MESSAGE);
			}
	}

}
