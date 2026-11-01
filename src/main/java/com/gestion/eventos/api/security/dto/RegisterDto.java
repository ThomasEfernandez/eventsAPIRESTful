package com.gestion.eventos.api.security.dto;

import java.util.Set;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDto {
    @NotBlank(message = "El username no puede estar vacio")
    @Size(min=4,max = 20,message = "La contraseña debe tener al menos 6 caracteres")
    private String username;
    
    @NotBlank(message = "El password no puede estar vacio")
    @Size(min=6,message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
    
    @NotBlank(message = "El email no puede estar vacio")
    @Email(message="Debe ser una dirección de correo válida")
    private String email;

    @NotBlank(message = "El name no puede estar vacio")
    private String name;

    private Set<String> roles;
}
