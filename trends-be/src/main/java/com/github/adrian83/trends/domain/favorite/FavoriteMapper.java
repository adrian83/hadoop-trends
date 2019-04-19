package com.github.adrian83.trends.domain.favorite;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.github.adrian83.trends.domain.favorite.model.Favorite;
import com.github.adrian83.trends.domain.favorite.model.FavoriteDoc;

@Mapper(componentModel = "spring")
public interface FavoriteMapper {

  @Mappings({
    @Mapping(target = "twittId", source = "twittId", resultType = Long.class),
    @Mapping(target = "username", source = "username"),
    @Mapping(target = "count", source = "count")
  })
  FavoriteDoc dtoToDoc(Favorite favorite);

  @Mappings({
    @Mapping(target = "twittId", source = "twittId", resultType = String.class),
    @Mapping(target = "username", source = "username"),
    @Mapping(target = "count", source = "count")
  })
  Favorite docToDto(FavoriteDoc favoriteDoc);
}
