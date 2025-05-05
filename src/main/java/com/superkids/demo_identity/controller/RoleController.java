package com.superkids.demo_identity.controller;

import com.superkids.demo_identity.dto.request.ApiResponse;
import com.superkids.demo_identity.dto.request.PermissionRequest;
import com.superkids.demo_identity.dto.request.RoleRequest;
import com.superkids.demo_identity.dto.response.PermissionResponse;
import com.superkids.demo_identity.dto.response.RoleResponse;
import com.superkids.demo_identity.service.PermissionService;
import com.superkids.demo_identity.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleController {
    RoleService roleService;

    @PostMapping
    ApiResponse<RoleResponse> createRole(@RequestBody RoleRequest request){
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.createRole(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<RoleResponse>> getAllRoles(){
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getRoles())
                .build();
    }

    @DeleteMapping("/{role}")
    ApiResponse<Void> deleteRole(@PathVariable String role){
        roleService.deleteRole(role);
        return ApiResponse.<Void>builder()
                .build();
    }

}
