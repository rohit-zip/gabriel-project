package com.gn128.processor;

import com.gn128.dao.repository.RoleRepository;
import com.gn128.entity.Role;
import com.gn128.payloads.RolePayload;
import com.gn128.properties.FetchRoleProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.processor
 * Created_on - November 10 - 2024
 * Created_at - 13:28
 */

@Component
@RequiredArgsConstructor
public class RoleProcessor {

    private final FetchRoleProperties fetchRoleProperties;
    private final RoleRepository roleRepository;

    public void process() {
        Map<String, RolePayload> roles = fetchRoleProperties.data;
        if (roles.isEmpty()) return;
        long count = roleRepository.count();
        if (count == 0) {
            List<Role> rolesToSave = roles
                    .values()
                    .stream()
                    .map(rolePayload -> Role
                            .builder()
                            .roleId(rolePayload.getId())
                            .name(rolePayload.getName())
                            .build()
                    )
                    .toList();
            roleRepository.saveAll(rolesToSave);
        } else {
            List<Role> allRoles = roleRepository.findAll();
            List<Role> rolesToSave = allRoles
                    .parallelStream()
                    .filter(roleEntity -> roles.values().stream().noneMatch(role -> role.getId().equals(roleEntity.getRoleId())))
                    .toList();
            roleRepository.saveAll(rolesToSave);
        }
    }
}