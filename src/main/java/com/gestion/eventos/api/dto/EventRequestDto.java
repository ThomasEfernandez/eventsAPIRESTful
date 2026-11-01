package com.gestion.eventos.api.dto;

import lombok.Data;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;



@Data
@Schema(description = "DTO para la solicitud de creación o actualización de un evento")
public class EventRequestDto {
    @Schema(description = "Nombre del evento", example = "Conferencia de Tecnología")
    @NotBlank (message = "El nombre del evento no puede estar vacio.")
    private String name;
    @Schema(description = "Fecha del evento", example = "2023-12-25")
    @NotNull (message = "La fecha del evento no puede estar vacio.")
    private LocalDate date;
    @Schema(description = "Ubicación del evento", example = "Sala de conferencias")
    @NotBlank (message = "La Ubicación del evento no puede estar vacio.")
    private String location;
    @Schema(description = "ID de la categoría del evento")
    @NotNull(message="La categoria es obligatoria.")
     private Long categoryId;
    @Schema(description = "IDs de los oradores del evento")
    private Set<Long> speakerIds;
}
