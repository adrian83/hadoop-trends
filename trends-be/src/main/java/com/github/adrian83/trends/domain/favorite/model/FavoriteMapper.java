package com.github.adrian83.trends.domain.favorite.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface FavoriteMapper {

  @Mappings({
    @Mapping(target = "id", source = "twittId"),
    @Mapping(target = "username", source = "username"),
    @Mapping(target = "count", source = "count")
  })
  FavoriteDoc dtoToDoc(Favorite favorite);

  @Mappings({
    @Mapping(target = "twittId", source = "id"),
    @Mapping(target = "username", source = "username"),
    @Mapping(target = "count", source = "count")
  })
  Favorite docToDto(FavoriteDoc favoriteDoc);
}
