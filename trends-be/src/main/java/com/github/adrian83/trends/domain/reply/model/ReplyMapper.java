package com.github.adrian83.trends.domain.reply.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ReplyMapper {

  @Mappings({
    @Mapping(target = "twittId", source = "twittId", resultType = Long.class),
    @Mapping(target = "username", source = "username"),
    @Mapping(target = "count", source = "count")
  })
  ReplyDoc dtoToDoc(Reply reply);

  @Mappings({
    @Mapping(target = "twittId", source = "twittId", resultType = String.class),
    @Mapping(target = "username", source = "username"),
    @Mapping(target = "count", source = "count")
  })
  Reply docToDto(ReplyDoc replyDoc);
}
