package com.asterbit.qrscanner.activity.mapper;

import com.asterbit.qrscanner.activity.Activity;
import com.asterbit.qrscanner.activity.dto.ActivityDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ActivityMapper {

  @Mappings({
      @Mapping(source = "id", target = "id"),
      @Mapping(source = "title", target = "title"),
      @Mapping(source = "startTime", target = "startTime"),
      @Mapping(source = "endTime", target = "endTime"),
      @Mapping(source = "description", target = "description")})
  @BeanMapping(ignoreByDefault = true)
  ActivityDto toDto(Activity entity);
}
