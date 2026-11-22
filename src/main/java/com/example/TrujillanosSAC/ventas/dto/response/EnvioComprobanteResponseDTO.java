package com.example.TrujillanosSAC.ventas.dto.response;

/** Respuesta del reenvío del comprobante: el mensaje para mostrar y a qué correo se envió. */
public record EnvioComprobanteResponseDTO(String mensaje, String destinatario) {
}
