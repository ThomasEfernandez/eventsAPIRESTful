package com.gestion.eventos.api.dto;

import java.time.LocalDate;
import java.util.Set;

import lombok.Data;

@Data
public class EventResponseDto {
    private Long id;
    private String name;
    private LocalDate date;
    private String location;

    private Long categoryId;
    private String categoryName;

    private Set<SpeakerResponseDto> speakers;
}
