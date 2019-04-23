package com.github.adrian83.trends.domain.retwitt.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface RetwittMapper {

  @Mappings({
    @Mapping(target = "twittId", source = "twittId", resultType = Long.class),
    @Mapping(target = "username", source = "username"),
    @Mapping(target = "count", source = "count")
  })
  RetwittDoc dtoToDoc(Retwitt retwitt);

  @Mappings({
    @Mapping(target = "twittId", source = "twittId", resultType = String.class),
    @Mapping(target = "username", source = "username"),
    @Mapping(target = "count", source = "count")
  })
  Retwitt docToDto(RetwittDoc retwittDoc);
}
