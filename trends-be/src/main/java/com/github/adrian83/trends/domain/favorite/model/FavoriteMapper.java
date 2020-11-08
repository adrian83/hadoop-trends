package com.github.adrian83.trends.domain.favorite.model;

import org.springframework.stereotype.Component;

@Component
public class FavoriteMapper {

  public FavoriteDoc dtoToDoc(Favorite favorite) {
    return FavoriteDoc.builder()
        .id(favorite.getTwittId())
        .username(favorite.getUsername())
        .count(favorite.getCount())
        .updated(null)
        .build();
  }

  public Favorite docToDto(FavoriteDoc favoriteDoc) {
    return Favorite.builder()
        .twittId(favoriteDoc.getId())
        .username(favoriteDoc.getUsername())
        .count(favoriteDoc.getCount())
        .build();
  }
}
