package com.asterbit.qrscanner.user.mapper;

import com.asterbit.qrscanner.user.User;
import com.asterbit.qrscanner.user.dto.UserDto;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "lastName", target = "lastName"),
            @Mapping(source = "firstName", target = "firstName"),
            @Mapping(source = "email", target = "email")})
    @BeanMapping(ignoreByDefault = true)
    UserDto toDto(User entity);
}
