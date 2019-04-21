package com.github.adrian83.trends.domain.hashtag.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface HashtagMapper {

  @Mappings({
    @Mapping(target = "name", source = "name"),
    @Mapping(target = "count", source = "count")
  })
  HashtagDoc dtoToDoc(Hashtag hashtag);

  @Mappings({
    @Mapping(target = "name", source = "name"),
    @Mapping(target = "count", source = "count")
  })
  Hashtag docToDto(HashtagDoc hashtagDoc);
}
