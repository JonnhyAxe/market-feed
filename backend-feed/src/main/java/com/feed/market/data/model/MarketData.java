package com.feed.market.data.model;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ApiModel(description = "All details about the artist ")
@Data(staticConstructor="of")
@ToString(includeFieldNames=true)
@Builder(toBuilder=true)
@NoArgsConstructor
@AllArgsConstructor
public class MarketData {
	
	@NotNull
	private String code; 
	@NotNull
	private String name;
	@NotNull
	@Builder.Default
	private Double bid = new Double(0);
	@NotNull
	@Builder.Default
	private Double mid = new Double(0);
	@NotNull
	@Builder.Default
	private Double ask = new Double(0);
	@NotNull
	@Builder.Default
	private Double volume = new Double(0);
	
	

}
