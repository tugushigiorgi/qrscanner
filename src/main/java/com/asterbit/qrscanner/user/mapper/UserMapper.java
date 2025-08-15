package com.asterbit.qrscanner.user.mapper;

import com.asterbit.qrscanner.user.User;
import com.asterbit.qrscanner.user.dto.UserDto;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "surname", target = "surname"),
            @Mapping(source = "email", target = "email")})
    @BeanMapping(ignoreByDefault = true)
    UserDto toDto(User entity);
}
