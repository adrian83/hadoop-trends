package com.github.adrian83.trends.domain.retweet.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface RetweetMapper {

  @Mappings({
    @Mapping(target = "twittId", source = "twittId", resultType = Long.class),
    @Mapping(target = "username", source = "username"),
    @Mapping(target = "count", source = "count")
  })
  RetweetDoc dtoToDoc(Retweet retweet);

  @Mappings({
    @Mapping(target = "twittId", source = "twittId", resultType = String.class),
    @Mapping(target = "username", source = "username"),
    @Mapping(target = "count", source = "count")
  })
  Retweet docToDto(RetweetDoc retweetDoc);
}
