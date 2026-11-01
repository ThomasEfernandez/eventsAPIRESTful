package com.gestion.eventos.api.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String name;
    private String email;

    private List<RoleDto> roles;
    private List<EventSummaryDto> attendedEvents;
}
