package com.atsistemas.bakenddevtesttnm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tnavarro
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDto {
	
	private Integer id;
	private String name;
	private Long price;
	private Boolean availability;

}
