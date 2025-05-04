package com.superkids.demo_identity.mapper;

import com.superkids.demo_identity.dto.request.PermissionRequest;
import com.superkids.demo_identity.dto.request.UserCreationRequest;
import com.superkids.demo_identity.dto.request.UserUpdateRequest;
import com.superkids.demo_identity.dto.response.PermissionResponse;
import com.superkids.demo_identity.dto.response.UserResponse;
import com.superkids.demo_identity.entity.Permission;
import com.superkids.demo_identity.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);
    PermissionResponse toPermissionResponse(Permission permission);
}
