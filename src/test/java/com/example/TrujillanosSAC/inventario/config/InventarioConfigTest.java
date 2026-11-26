package com.example.TrujillanosSAC.inventario.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InventarioConfigTest {

    @Test
    void quitaTodasLasBarrasFinalesDeLaUrlDelFrontend() {
        assertEquals("http://127.0.0.1:4200", InventarioConfig.quitarBarrasFinales("http://127.0.0.1:4200///"));
    }

    @Test
    void conservaLaUrlSiNoTerminaEnBarra() {
        assertEquals("https://tienda.example", InventarioConfig.quitarBarrasFinales("https://tienda.example"));
    }
}
