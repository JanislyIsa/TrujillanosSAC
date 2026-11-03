package com.example.TrujillanosSAC.inventario.dto;

import com.example.TrujillanosSAC.inventario.model.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public final class InventarioDtos {
    private InventarioDtos() {}
    public record ProductoRequest(
            @NotBlank @Size(max=100) String nombre,
            @NotBlank @Size(max=255) String descripcion,
            @NotNull @DecimalMin("0.01") @Digits(integer=8, fraction=2) BigDecimal precio,
            @PositiveOrZero Integer stock,
            @NotNull @PositiveOrZero Integer stockMinimo,
            @NotNull @PositiveOrZero Integer stockLimite,
            @NotNull @Positive Long idCategoria,
            @NotNull @Positive Long idPresentacion,
            @NotNull @Positive Long idAlmacen,
            @Size(max=255) String imagenRuta, Boolean activo) {
        public ProductoRequest(String nombre,String descripcion,BigDecimal precio,Integer stock,Integer stockMinimo,Integer stockLimite,
                Long idCategoria,Long idPresentacion,Long idAlmacen,String imagenRuta) {
            this(nombre,descripcion,precio,stock,stockMinimo,stockLimite,idCategoria,idPresentacion,idAlmacen,imagenRuta,null);
        }
    }
    public record InsumoRequest(
            @NotBlank @Size(max=100) String nombre,
            @PositiveOrZero Integer stock,
            @NotNull @PositiveOrZero Integer stockMinimo,
            @NotNull @PositiveOrZero Integer stockLimite,
            @NotNull @Positive Long idUnidadMedida,
            @NotNull @Positive Long idAlmacen, Boolean activo) {
        public InsumoRequest(String nombre,Integer stock,Integer stockMinimo,Integer stockLimite,Long idUnidadMedida,Long idAlmacen) {
            this(nombre,stock,stockMinimo,stockLimite,idUnidadMedida,idAlmacen,null);
        }
    }
    public record ItemResponse(Long id, TipoItem tipoItem, String nombre, String descripcion,
            BigDecimal precio, Integer stock, Integer stockMinimo, Integer stockLimite,
            EstadoStock estadoStock, Long idAlmacen, String almacen, Long idCategoria,
            String categoria, Long idUnidad, String unidad, String imagenUrl, boolean activo) {}
    public record PaginaResponse<T>(List<T> contenido, int pagina, int tamanio,
            long totalElementos, int totalPaginas) {
        public static <T> PaginaResponse<T> de(List<T> items, int pagina, int tamanio) {
            if (pagina < 0 || tamanio < 1 || tamanio > 100)
                throw new IllegalArgumentException("Use pagina >= 0 y tamanio entre 1 y 100");
            long inicio = (long) pagina * tamanio;
            return new PaginaResponse<>(items.subList((int)Math.min(inicio, items.size()),
                    (int)Math.min(inicio+tamanio, items.size())), pagina, tamanio,
                    items.size(), (items.size()+tamanio-1)/tamanio);
        }
    }
    public record MovimientoRequest(@NotNull TipoMovimiento tipoMovimiento,
            @NotNull TipoItem tipoItem, @NotNull @Positive Long idItem,
            @NotNull @Positive Integer cantidad, @NotBlank @Size(max=200) String motivo) {}
    public record ComprobanteResponse(MovimientoInventarioDto movimiento,
            String responsable, String motivo) {}
    public record AlertaResponse(Long id, String codigo, TipoItem tipoItem, Long idItem,
            String tipoAlerta, String descripcion, Integer stockActual, Integer stockMinimo,
            Integer stockLimite, LocalDateTime fechaHora, LocalDateTime resueltaEn) {}
    public record NotificacionResponse(Long id, AlertaResponse alerta,
            LocalDateTime creadaEn, LocalDateTime leidaEn) {}
    public record FaltanteResponse(Long idInsumo, String nombre, int stock,
            int stockMinimo, int stockLimite, int cantidadSugerida, String unidad) {}
    public record SolicitudLineaRequest(@NotNull @Positive Long idInsumo,
            @NotNull @DecimalMin("0.01") @Digits(integer=8, fraction=2) BigDecimal cantidad) {}
    public record SolicitudRequest(@NotNull @Positive Long idProveedor,
            @NotEmpty @Size(max=100) List<@NotNull @Valid SolicitudLineaRequest> detalles) {}
    public record SolicitudAutomaticaRequest(@NotNull @Positive Long idProveedor) {}
    public record SolicitudDetalleResponse(Long idInsumo, String nombre,
            BigDecimal cantidad, String unidad) {}
    public record SolicitudResponse(Long id, String codigo, LocalDate fecha, String estado,
            Long idProveedor, Long idEmpleado, List<SolicitudDetalleResponse> detalles) {}
    public record CatalogoResponse(Long id, String nombre) {}
    public record ErrorResponse(int estado, String mensaje, String ruta,
            LocalDateTime fechaHora, java.util.Map<String,String> errores) {}
}
