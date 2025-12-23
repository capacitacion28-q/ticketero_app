package com.example.ticketero.model.dto;

import com.example.ticketero.model.enums.QueueType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * DTO para creación de tickets - RF-001
 * Campos completos según especificaciones del Sistema Ticketero
 */
public record TicketCreateRequest(
    @NotBlank(message = "El título es obligatorio")
    @Size(min = 5, max = 200, message = "Título debe tener entre 5-200 caracteres")
    String titulo,
    
    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 10, max = 5000, message = "Descripción debe tener entre 10-5000 caracteres")
    String descripcion,
    
    @NotNull(message = "El usuario ID es obligatorio")
    @Positive(message = "Usuario ID debe ser positivo")
    Long usuarioId,
    
    @NotBlank(message = "El RUT/ID es obligatorio")
    @Pattern(regexp = "^[0-9]{7,8}-[0-9Kk]$", message = "Formato de RUT inválido")
    String nationalId,
    
    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^\\+56[0-9]{9}$", message = "Teléfono debe tener formato +56XXXXXXXXX")
    String telefono,
    
    @NotBlank(message = "La sucursal es obligatoria")
    String branchOffice,
    
    @NotNull(message = "El tipo de cola es obligatorio")
    QueueType queueType
) {}