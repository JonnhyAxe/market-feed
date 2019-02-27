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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Markets {
	
	@NotNull
	private String code; 
	@NotNull
	private String name;
	
	

}
