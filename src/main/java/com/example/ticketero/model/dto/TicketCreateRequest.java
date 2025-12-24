package com.example.ticketero.model.dto;

import com.example.ticketero.model.enums.QueueType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * DTO para creación de tickets con validaciones Bean Validation completas.
 * 
 * Implementa: RF-001 (Creación de tickets)
 * Validaciones: RUT chileno, teléfono +56XXXXXXXXX, campos obligatorios
 * 
 * Validaciones implementadas:
 * - RUT: Formato ^[0-9]{7,8}-[0-9Kk]$ (estándar chileno)
 * - Teléfono: Formato ^\+56[0-9]{9}$ (móvil chileno)
 * - Título: 5-200 caracteres
 * - Descripción: 10-5000 caracteres
 * - Usuario ID: Número positivo
 * - Sucursal y tipo de cola: Obligatorios
 * 
 * Procesamiento: TicketController valida automáticamente con @Valid
 * 
 * @param titulo Título del ticket (5-200 caracteres)
 * @param descripcion Descripción detallada (10-5000 caracteres)
 * @param usuarioId ID del usuario que crea el ticket
 * @param nationalId RUT chileno con formato validado
 * @param telefono Teléfono móvil chileno (+56XXXXXXXXX)
 * @param branchOffice Sucursal donde se atiende
 * @param queueType Tipo de cola (CAJA, PERSONAL_BANKER, EMPRESAS, GERENCIA)
 * 
 * @author Sistema Ticketero
 * @version 1.0
 * @since 1.0
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
    
    /**
     * RUT chileno con validación de formato estándar.
     * Formato: 12345678-9 o 1234567-K
     */
    @NotBlank(message = "El RUT/ID es obligatorio")
    @Pattern(regexp = "^[0-9]{7,8}-[0-9Kk]$", message = "Formato de RUT inválido")
    String nationalId,
    
    /**
     * Teléfono móvil chileno con código de país.
     * Formato: +56912345678 (9 dígitos después de +56)
     */
    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^\\+56[0-9]{9}$", message = "Teléfono debe tener formato +56XXXXXXXXX")
    String telefono,
    
    @NotBlank(message = "La sucursal es obligatoria")
    String branchOffice,
    
    @NotNull(message = "El tipo de cola es obligatorio")
    QueueType queueType
) {}