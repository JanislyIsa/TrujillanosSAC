package com.example.TrujillanosSAC.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "PRODUCTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {
    private static final String IMAGEN_POR_DEFECTO = "/img/tetin-pop.jpeg";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PRODUCTO")
    private Long idProducto;

    @Builder.Default
    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    @Column(name = "ACTIVO", nullable = false)
    private Boolean activo = true;

    @NotBlank
    @jakarta.validation.constraints.Size(max = 100)
    @Column(name = "NOMBRE", length = 100, nullable = false)
    private String nombre;

    @NotBlank
    @Size(max = 255)
    @Column(name = "DESCRIPCION", length = 255, nullable = false)
    private String descripcion;

    @NotNull
    @Column(name = "PRECIO", precision = 10, scale = 2, nullable = false)
    private BigDecimal precio;

    @NotNull
    @jakarta.validation.constraints.PositiveOrZero
    @Column(name = "STOCK", nullable = false)
    private Integer stock;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CATEGORIA", nullable = false)
    private Categoria categoria;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PRESENTACION", nullable = false)
    private PresentacionProducto presentacion;

    @NotNull
    @Column(name = "STOCK_MINIMO", nullable = false)
    private Integer stockMinimo;

    @NotNull
    @Column(name = "STOCK_LIMITE", nullable = false)
    private Integer stockLimite;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ALMACEN", nullable = false)
    private Almacen almacen;

    @Size(max = 255)
    @Column(name = "IMAGEN_RUTA", length = 255)
    private String imagenRuta;

    public String getImagenUrl() {
        if (imagenRuta == null || imagenRuta.isBlank()) {
            return IMAGEN_POR_DEFECTO;
        }

        String ruta = imagenRuta.trim().replace("\\", "/").replace(" ", "%20");

        if (ruta.startsWith("http://") || ruta.startsWith("https://") || ruta.startsWith("/")) {
            return ruta;
        }
        if (ruta.startsWith("img/")) {
            return "/" + ruta;
        }
        return "/img/" + ruta;
    }

}
