package com.superkids.demo_identity.service;

import com.superkids.demo_identity.dto.request.RoleRequest;
import com.superkids.demo_identity.dto.response.RoleResponse;
import com.superkids.demo_identity.entity.Role;
import com.superkids.demo_identity.mapper.RoleMapper;
import com.superkids.demo_identity.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    RoleRepository roleRepository;
    RoleMapper roleMapper;

    public RoleResponse createRole(RoleRequest request){
        Role role = roleMapper.toRole(request);
        role = roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    public List<RoleResponse> getRoles(){
        var response = roleRepository.findAll();
        return response.stream().map(roleMapper::toRoleResponse).toList();
    }

    public void deleteRole(String role){
        roleRepository.deleteById(role);
    }


}
