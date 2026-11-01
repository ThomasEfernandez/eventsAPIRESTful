package com.gestion.eventos.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data 
public class SpeakerRequestDto {

    @NotBlank (message = "El nombre del ponente no puede estar vacio.")
    @Size (max = 50, message = "El nombre del ponente debe tener al menos 10 caracteres.")
    private String name;

    @Email (message = "El correo del ponente no es valido.")
    @Size (max = 100, message = "El correo del ponente debe tener al menos 10 caracteres.")  
    private String email;

    @Size(max = 500, message = "La biografía del ponente debe tener al menos 10 caracteres.")
    private String bio;
   
    
}
