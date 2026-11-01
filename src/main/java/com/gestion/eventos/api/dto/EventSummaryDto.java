package com.gestion.eventos.api.dto;

import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class EventSummaryDto {
    private Long id;
    private LocalDate date;
    private String name;
    private String location;
}
