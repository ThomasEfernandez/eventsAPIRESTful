package com.gestion.eventos.api.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import com.gestion.eventos.api.domain.Role;
import com.gestion.eventos.api.domain.User;
import com.gestion.eventos.api.dto.RoleDto;
import com.gestion.eventos.api.dto.UserResponseDto;
import com.gestion.eventos.api.exceptions.ResourceNotFoundException;
import com.gestion.eventos.api.repository.RoleRepository;
import com.gestion.eventos.api.security.dto.RegisterDto;
import org.mapstruct.ReportingPolicy;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserMapper {

    @Autowired
    protected RoleRepository roleRepository;

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(
        target = "roles",
        source = "roles",
        qualifiedByName = "mapRoleStringsToRoles"
    )
    @Mapping(target = "attendedEvents", ignore = true)
    public abstract User registerDtoToUser(RegisterDto registerDto);


    @Named("mapRoleStringsToRoles")
    public Set<Role> mapRoleStringsToRoles(Set<String> roleNames) {

        if (roleNames == null || roleNames.isEmpty()) {
            return roleRepository.findByName("ROLE_USER")
                    .map(Collections::singleton)
                    .orElseThrow(() -> new ResourceNotFoundException(
                        "Error: Rol 'ROLE_USER' no encontrado en la base de datos. " +
                        "Asegurese de que el rol ROLE_USER exista al iniciar la aplicacion."
                    ));
        }

        return roleNames.stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow
                        (() -> new ResourceNotFoundException("Error: Rol no encontrado: " + roleName )))
                .collect(Collectors.toSet());
    }

    //mapear usuario -> usuarioResponseDto
    //Mappear  lista de usuarios -> lista de usuarioResponseDto

    public abstract UserResponseDto userToUserResponseDto(User user);
    public abstract List<UserResponseDto> userListToUserResponseDtoList(List<User> users);
    public abstract RoleDto roleToRoleDto(Role role);

}