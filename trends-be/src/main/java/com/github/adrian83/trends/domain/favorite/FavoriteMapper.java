package com.github.adrian83.trends.domain.favorite;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FavoriteMapper {

  @Mapping(target = "twittId", source = "twittId", resultType = Long.class)
  FavoriteDoc dtoToDoc(Favorite favorite);

  @Mapping(target = "twittId", source = "twittId", resultType = String.class)
  Favorite docToDto(FavoriteDoc favoriteDoc);
}
