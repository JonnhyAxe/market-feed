package com.feed.market.data.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.feed.market.data.dto.DataDTO;
import com.feed.market.data.model.Data;

@Mapper(componentModel = "spring")
public interface DataMapper {
	

    @Mapping(target="message", source="body")
	Data dtoToModel(DataDTO messageDTO);
    
    @Mappings({
        @Mapping(target="body", source="message"),
      })
	DataDTO modelToDto(String message);
}
