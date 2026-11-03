document.addEventListener('DOMContentLoaded', () => {
    const search = document.querySelector('#inventorySearch');
    const tabs = document.querySelectorAll('[data-inventory-tab]');
    const panels = document.querySelectorAll('[data-inventory-panel]');
    const panelActions = document.querySelectorAll('[data-panel-action]');
    const productImageInput = document.querySelector('#productFormImage');
    const stockStatusFilter = document.querySelector('#stockStatusFilter');

    tabs.forEach((tab) => {
        tab.addEventListener('click', () => {
            const target = tab.dataset.inventoryTab;

            tabs.forEach((item) => item.classList.toggle('is-active', item === tab));
            panels.forEach((panel) => {
                panel.classList.toggle('is-active', panel.dataset.inventoryPanel === target);
            });
            panelActions.forEach((action) => {
                action.classList.toggle('is-active', action.dataset.panelAction === target);
            });

            filtrarFilas(search?.value || '');
        });
    });

    search?.addEventListener('input', () => filtrarFilas(search.value));
    stockStatusFilter?.addEventListener('change', () => filtrarFilas(search?.value || ''));
    document.querySelector('[data-verify-stock]')?.addEventListener('click', verificarStock);

    document.querySelector('[data-open-product-create]')?.addEventListener('click', prepararCrearProducto);
    document.querySelector('[data-open-insumo-create]')?.addEventListener('click', prepararCrearInsumo);

    document.querySelectorAll('[data-product-detail]').forEach((button) => {
        button.addEventListener('click', () => abrirDetalleProducto(button.closest('tr')));
    });
    document.querySelectorAll('[data-product-edit]').forEach((button) => {
        button.addEventListener('click', () => prepararEditarProducto(button.closest('tr')));
    });
    document.querySelectorAll('[data-product-delete]').forEach((button) => {
        button.addEventListener('click', () => prepararEliminar(button.closest('tr'), 'producto'));
    });

    document.querySelectorAll('[data-insumo-detail]').forEach((button) => {
        button.addEventListener('click', () => abrirDetalleInsumo(button.closest('tr')));
    });
    document.querySelectorAll('[data-insumo-edit]').forEach((button) => {
        button.addEventListener('click', () => prepararEditarInsumo(button.closest('tr')));
    });
    document.querySelectorAll('[data-insumo-delete]').forEach((button) => {
        button.addEventListener('click', () => prepararEliminar(button.closest('tr'), 'insumo'));
    });
    document.querySelectorAll('[data-request-purchase]').forEach((button) => {
        button.addEventListener('click', () => prepararSolicitudCompra(button.closest('tr')));
    });
    document.querySelectorAll('[data-stock-detail]').forEach((button) => {
        button.addEventListener('click', () => abrirDetalleStock(button.closest('tr')));
    });

    document.querySelectorAll('[data-close-modal]').forEach((button) => {
        button.addEventListener('click', () => cerrarModal(button.closest('.inventory-modal')));
    });
    document.querySelectorAll('.inventory-modal').forEach((modal) => {
        modal.addEventListener('click', (event) => {
            if (event.target === modal) {
                cerrarModal(modal);
            }
        });
    });

    productImageInput?.addEventListener('change', () => {
        document.querySelector('#productImageText').textContent =
            productImageInput.files?.[0]?.name || 'Cargar imagen del producto';
    });

    document.querySelector('#purchaseQuantity')?.addEventListener('input', actualizarResumenSolicitud);
});

function filtrarFilas(valor) {
    const query = valor.trim().toLowerCase();
    const activePanel = document.querySelector('[data-inventory-panel].is-active');
    const statusFilter = document.querySelector('#stockStatusFilter')?.value || 'todos';

    activePanel?.querySelectorAll('[data-inventory-search]').forEach((row) => {
        const matchesSearch = row.dataset.inventorySearch.includes(query);
        const matchesStatus = statusFilter === 'todos' || row.dataset.stockState === statusFilter;
        row.style.display = matchesSearch && matchesStatus ? '' : 'none';
    });
}

function verificarStock() {
    document.querySelectorAll('[data-inventory-search]').forEach((row) => {
        const estado = calcularEstadoStock(row);
        row.dataset.estado = estado.label;
        row.dataset.stockState = estado.key;

        const status = row.querySelector('[data-stock-status]');
        if (status) {
            status.textContent = estado.label;
            status.className = `status-pill ${estado.className}`;
        }

        row.querySelectorAll('.stock-value').forEach((value) => {
            value.classList.toggle('low', estado.key === 'stock-bajo');
            value.classList.toggle('over', estado.key === 'sobrestock');
        });
    });

    filtrarFilas(document.querySelector('#inventorySearch')?.value || '');
}

function abrirModal(selector) {
    const modal = document.querySelector(selector);
    modal?.classList.add('is-open');
    modal?.setAttribute('aria-hidden', 'false');
}

function cerrarModal(modal) {
    modal?.classList.remove('is-open');
    modal?.setAttribute('aria-hidden', 'true');
}

function abrirDetalleProducto(row) {
    if (!row) {
        return;
    }

    asignarValor('[data-product-detail-code]', row.dataset.codigo, true);
    asignarValor('[data-product-detail-name]', row.dataset.nombre, true);
    asignarValor('[data-product-detail-description]', row.dataset.descripcion, true);
    asignarTexto('[data-product-detail-category]', row.dataset.categoria);
    asignarTexto('[data-product-detail-price]', `S/ ${Number(row.dataset.precio || 0).toFixed(2)}`);
    asignarTexto('[data-product-detail-warehouse]', row.dataset.almacen);
    asignarTexto('[data-product-detail-unit-name]', row.dataset.presentacion);
    asignarTexto('[data-product-detail-stock]', row.dataset.stock);
    asignarTexto('[data-product-detail-unit]', `${row.dataset.presentacion} disponibles`);
    asignarTexto('[data-product-detail-min]', `${row.dataset.stockMinimo} ${row.dataset.presentacion}`);
    asignarTexto('[data-product-detail-limit]', `${row.dataset.stockLimite} ${row.dataset.presentacion}`);
    asignarTexto('[data-product-detail-status]', row.dataset.estado);

    const image = document.querySelector('[data-product-detail-image]');
    if (image) {
        image.src = row.dataset.imagen;
        image.alt = row.dataset.nombre;
    }

    abrirModal('#productDetailModal');
}

function abrirDetalleStock(row) {
    if (!row) {
        return;
    }

    const estado = row.dataset.stockState ? {
        key: row.dataset.stockState,
        label: row.dataset.estado,
        className: claseEstado(row.dataset.stockState)
    } : calcularEstadoStock(row);
    const unidad = row.dataset.presentacion || row.dataset.unidad || '';
    const stock = numero(row.dataset.stock);
    const minimo = numero(row.dataset.stockMinimo);
    const limite = numero(row.dataset.stockLimite);

    asignarTexto('[data-stock-code]', row.dataset.codigo);
    asignarTexto('[data-stock-name]', row.dataset.nombre);
    asignarTexto('[data-stock-type]', row.dataset.tipo);
    asignarTexto('[data-stock-unit]', unidad);
    asignarTexto('[data-stock-current]', `${stock} ${unidad}`);
    asignarTexto('[data-stock-min]', `${minimo} ${unidad}`);
    asignarTexto('[data-stock-limit]', `${limite} ${unidad}`);

    const status = document.querySelector('[data-stock-detail-status]');
    if (status) {
        status.textContent = estado.label;
        status.className = `status-pill ${estado.className}`;
    }

    const message = document.querySelector('[data-stock-message]');
    if (message) {
        message.className = `stock-message ${estado.className}`;
        message.innerHTML = mensajeStock(estado.key, stock, minimo, limite, unidad);
    }

    abrirModal('#stockDetailModal');
}

function abrirDetalleInsumo(row) {
    if (!row) {
        return;
    }

    asignarTexto('[data-insumo-detail-code]', row.dataset.codigo);
    asignarTexto('[data-insumo-detail-name]', row.dataset.nombre);
    asignarTexto('[data-insumo-detail-status]', row.dataset.estado);
    asignarTexto('[data-insumo-detail-unit]', row.dataset.unidad);
    asignarTexto('[data-insumo-detail-warehouse]', row.dataset.almacen);
    asignarTexto('[data-insumo-detail-stock]', `${row.dataset.stock} ${row.dataset.unidad}`);
    asignarTexto('[data-insumo-detail-min]', `${row.dataset.stockMinimo} ${row.dataset.unidad}`);
    asignarTexto('[data-insumo-detail-limit]', `${row.dataset.stockLimite} ${row.dataset.unidad}`);
    abrirModal('#insumoDetailModal');
}

function prepararCrearProducto() {
    const form = document.querySelector('#productInventoryForm');
    form.action = '/admin/productos';
    form.reset();
    document.querySelector('[data-product-form-title]').textContent = 'Registrar producto';
    document.querySelector('[data-product-form-subtitle]').textContent =
        'Ingrese los datos necesarios para registrar un producto en el inventario.';
    document.querySelector('[data-product-form-submit]').textContent = 'Guardar producto';
    document.querySelector('#productImageText').textContent = 'Cargar imagen del producto';
    abrirModal('#productFormModal');
}

function prepararEditarProducto(row) {
    if (!row) {
        return;
    }

    const form = document.querySelector('#productInventoryForm');
    form.action = `/admin/productos/${row.dataset.id}/editar`;
    form.reset();

    document.querySelector('[data-product-form-title]').textContent = 'Editar producto';
    document.querySelector('[data-product-form-subtitle]').textContent =
        'Modifique los datos necesarios del producto seleccionado.';
    document.querySelector('[data-product-form-submit]').textContent = 'Guardar cambios';
    document.querySelector('#productFormName').value = row.dataset.nombre;
    document.querySelector('#productFormPrice').value = row.dataset.precio;
    document.querySelector('#productFormCategory').value = row.dataset.categoriaId;
    document.querySelector('#productFormPresentation').value = row.dataset.presentacionId;
    document.querySelector('#productFormStock').value = row.dataset.stock;
    document.querySelector('#productFormMin').value = row.dataset.stockMinimo;
    document.querySelector('#productFormLimit').value = row.dataset.stockLimite;
    document.querySelector('#productFormDescription').value = row.dataset.descripcion;
    document.querySelector('#productImageText').textContent = 'Cambiar imagen del producto';
    abrirModal('#productFormModal');
}

function prepararCrearInsumo() {
    const form = document.querySelector('#insumoInventoryForm');
    form.action = '/admin/inventario/insumos';
    form.reset();
    document.querySelector('[data-insumo-form-title]').textContent = 'Registrar insumo';
    document.querySelector('[data-insumo-form-subtitle]').textContent =
        'Ingrese los datos necesarios para registrar un insumo en el inventario.';
    document.querySelector('[data-insumo-form-submit]').textContent = 'Guardar insumo';
    abrirModal('#insumoFormModal');
}

function prepararEditarInsumo(row) {
    if (!row) {
        return;
    }

    const form = document.querySelector('#insumoInventoryForm');
    form.action = `/admin/inventario/insumos/${row.dataset.id}/editar`;
    form.reset();

    document.querySelector('[data-insumo-form-title]').textContent = 'Editar insumo';
    document.querySelector('[data-insumo-form-subtitle]').textContent =
        'Modifique los datos necesarios del insumo seleccionado.';
    document.querySelector('[data-insumo-form-submit]').textContent = 'Guardar cambios';
    document.querySelector('#insumoFormName').value = row.dataset.nombre;
    document.querySelector('#insumoFormUnit').value = row.dataset.unidadId;
    document.querySelector('#insumoFormWarehouse').value = row.dataset.almacenId;
    document.querySelector('#insumoFormStock').value = row.dataset.stock;
    document.querySelector('#insumoFormMin').value = row.dataset.stockMinimo;
    document.querySelector('#insumoFormLimit').value = row.dataset.stockLimite;
    abrirModal('#insumoFormModal');
}

function prepararEliminar(row, tipo) {
    if (!row) {
        return;
    }

    const form = document.querySelector('#deleteInventoryForm');
    const isProduct = tipo === 'producto';
    form.action = isProduct
        ? `/admin/productos/${row.dataset.id}/eliminar`
        : `/admin/inventario/insumos/${row.dataset.id}/eliminar`;

    document.querySelector('#deleteReturnTo').disabled = !isProduct;
    document.querySelector('[data-delete-title]').textContent = isProduct ? 'Eliminar producto' : 'Eliminar insumo';
    document.querySelector('[data-delete-message]').textContent =
        `Confirme la eliminacion del ${tipo} seleccionado.`;
    document.querySelector('[data-delete-submit]').textContent = isProduct ? 'Eliminar producto' : 'Eliminar insumo';
    asignarTexto('[data-delete-code]', row.dataset.codigo);
    asignarTexto('[data-delete-name]', row.dataset.nombre);
    asignarTexto('[data-delete-stock]', `${row.dataset.stock} ${isProduct ? row.dataset.presentacion : row.dataset.unidad}`);
    asignarTexto('[data-delete-status]', row.dataset.estado);
    abrirModal('#deleteModal');
}

function prepararSolicitudCompra(row) {
    if (!row) {
        return;
    }

    const stock = numero(row.dataset.stock);
    const minimo = numero(row.dataset.stockMinimo);
    const limite = numero(row.dataset.stockLimite);
    const cantidadSugerida = Math.max(minimo - stock, 1);

    document.querySelector('#purchaseInsumoId').value = row.dataset.id;
    document.querySelector('#purchaseInsumoName').value = row.dataset.nombre;
    document.querySelector('#purchaseUnit').value = row.dataset.unidad;
    document.querySelector('#purchaseStock').value = stock;
    document.querySelector('#purchaseMin').value = minimo;
    document.querySelector('#purchaseLimit').value = limite;
    document.querySelector('#purchaseQuantity').value = cantidadSugerida;
    actualizarResumenSolicitud();
    abrirModal('#purchaseRequestModal');
}

function actualizarResumenSolicitud() {
    const nombre = document.querySelector('#purchaseInsumoName')?.value || '';
    const unidad = document.querySelector('#purchaseUnit')?.value || '';
    const cantidad = document.querySelector('#purchaseQuantity')?.value || '0';
    const summary = document.querySelector('#purchaseSummary');

    if (summary) {
        summary.textContent = `Se solicitara la compra de ${cantidad} ${unidad} de ${nombre}.`;
    }
}

function calcularEstadoStock(row) {
    const stock = numero(row.dataset.stock);
    const minimo = numero(row.dataset.stockMinimo);
    const limite = numero(row.dataset.stockLimite);

    if (stock < minimo) {
        return { key: 'stock-bajo', label: 'Stock bajo', className: 'low' };
    }

    if (stock > limite) {
        return { key: 'sobrestock', label: 'Sobrestock', className: 'over' };
    }

    return { key: 'normal', label: 'Normal', className: 'normal' };
}

function claseEstado(key) {
    if (key === 'stock-bajo') {
        return 'low';
    }

    if (key === 'sobrestock') {
        return 'over';
    }

    if (key === 'normal') {
        return 'normal';
    }

    return 'pending';
}

function mensajeStock(key, stock, minimo, limite, unidad) {
    if (key === 'stock-bajo') {
        return `<strong>Alerta de stock:</strong> El stock actual esta por debajo del stock minimo configurado.<br>
                Faltan ${minimo - stock} ${unidad} para alcanzar el stock minimo.`;
    }

    if (key === 'sobrestock') {
        return `<strong>Alerta de stock:</strong> El stock actual supera el stock limite configurado.<br>
                Exceso de ${stock - limite} ${unidad} sobre el limite establecido.`;
    }

    return `<strong>Stock normal:</strong> El stock actual se encuentra dentro de los limites configurados.`;
}

function numero(valor) {
    return Number.parseInt(valor || '0', 10);
}

function asignarTexto(selector, valor) {
    const element = document.querySelector(selector);
    if (element) {
        element.textContent = valor || '';
    }
}

function asignarValor(selector, valor) {
    const element = document.querySelector(selector);
    if (element) {
        element.value = valor || '';
    }
}
