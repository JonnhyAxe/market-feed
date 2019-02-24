package com.feed.market.data.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.feed.market.data.dto.DataDTO;
import com.feed.market.data.model.Data;

@Mapper(componentModel = "spring")
public interface DataMapper {
	

    @Mapping(target="message", source="body")
	Data dtoToModel(DataDTO artistDTO);
    
    @Mappings({
        @Mapping(target="body", source="artist"),
      })
	DataDTO modelToDto(String artist);
}
