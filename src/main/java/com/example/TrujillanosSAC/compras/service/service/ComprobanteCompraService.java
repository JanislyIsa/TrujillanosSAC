package com.example.TrujillanosSAC.compras.service.service;

import com.example.TrujillanosSAC.compras.dto.ComprobanteCompraRequestDTO;
import com.example.TrujillanosSAC.compras.dto.ComprobanteCompraResponseDTO;
import com.example.TrujillanosSAC.compras.service.generic.CrudService;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

public interface ComprobanteCompraService extends CrudService<ComprobanteCompraRequestDTO, ComprobanteCompraResponseDTO, Long> {
    ComprobanteCompraResponseDTO enviarFacturaProveedor(Long ordenCompraId, String numeroFactura,
                                                         LocalDate fechaEmision, BigDecimal monto,
                                                         MultipartFile archivo, String username) throws IOException;
    byte[] descargarFactura(Long id, String username, boolean puedeVerTodas);
}
