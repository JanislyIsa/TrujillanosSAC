document.addEventListener('DOMContentLoaded', () => {
    const search = document.querySelector('#movementSearch');
    const movementFilter = document.querySelector('#movementTypeFilter');
    const itemFilter = document.querySelector('#movementItemFilter');
    const formModal = document.querySelector('#movementFormModal');
    const voucherModal = document.querySelector('#movementVoucherModal');
    const movementTypeInput = document.querySelector('#movementTypeInput');
    const itemTypeInput = document.querySelector('#movementItemTypeInput');
    const itemInput = document.querySelector('#movementItemInput');
    const quantityInput = document.querySelector('#movementQuantityInput');

    document.querySelector('[data-open-movement-modal]')?.addEventListener('click', () => {
        actualizarOpcionesItem();
        recalcularMovimiento();
        abrirModal(formModal);
    });

    search?.addEventListener('input', filtrarMovimientos);
    movementFilter?.addEventListener('change', filtrarMovimientos);
    itemFilter?.addEventListener('change', filtrarMovimientos);

    movementTypeInput?.addEventListener('change', recalcularMovimiento);
    itemTypeInput?.addEventListener('change', () => {
        actualizarOpcionesItem();
        recalcularMovimiento();
    });
    itemInput?.addEventListener('change', recalcularMovimiento);
    quantityInput?.addEventListener('input', recalcularMovimiento);

    document.querySelectorAll('[data-open-voucher]').forEach((button) => {
        button.addEventListener('click', () => abrirComprobante(button.closest('tr')));
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

    actualizarOpcionesItem();
    filtrarMovimientos();
});

function filtrarMovimientos() {
    const query = document.querySelector('#movementSearch')?.value.trim().toLowerCase() || '';
    const movementType = document.querySelector('#movementTypeFilter')?.value || '';
    const itemType = document.querySelector('#movementItemFilter')?.value || '';

    document.querySelectorAll('[data-movement-search]').forEach((row) => {
        const matchesSearch = row.dataset.movementSearch.includes(query);
        const matchesMovement = !movementType || row.dataset.movementType === movementType;
        const matchesItem = !itemType || row.dataset.itemType === itemType;
        row.style.display = matchesSearch && matchesMovement && matchesItem ? '' : 'none';
    });
}

function actualizarOpcionesItem() {
    const itemType = document.querySelector('#movementItemTypeInput')?.value || 'Producto';
    const itemInput = document.querySelector('#movementItemInput');
    let firstVisible = null;

    itemInput?.querySelectorAll('option').forEach((option) => {
        const visible = option.dataset.type === itemType;
        option.hidden = !visible;
        option.disabled = !visible;

        if (visible && !firstVisible) {
            firstVisible = option;
        }
    });

    if (firstVisible && itemInput?.selectedOptions[0]?.dataset.type !== itemType) {
        itemInput.value = firstVisible.value;
    }
}

function recalcularMovimiento() {
    const type = document.querySelector('#movementTypeInput')?.value || 'Ingreso';
    const isSalida = type.toLowerCase() === 'salida';
    const option = document.querySelector('#movementItemInput')?.selectedOptions[0];
    const quantity = Number.parseInt(document.querySelector('#movementQuantityInput')?.value || '0', 10);
    const stockOld = Number.parseInt(option?.dataset.stock || '0', 10);
    const unit = option?.dataset.unit || '';
    const name = option?.dataset.name || '';
    const stockNew = isSalida ? stockOld - quantity : stockOld + quantity;

    asignarValor('#movementUnitInput', unit);
    asignarValor('#movementOldStockInput', stockOld);
    asignarValor('#movementNewStockInput', quantity > 0 ? stockNew : stockOld);

    const summary = document.querySelector('#movementSummary');
    if (summary) {
        if (quantity <= 0 || !name) {
            summary.textContent = 'Seleccione un item e ingrese una cantidad.';
            summary.classList.remove('is-error');
        } else if (stockNew < 0) {
            summary.textContent = `La salida supera el stock disponible de ${stockOld} ${unit}.`;
            summary.classList.add('is-error');
        } else {
            summary.textContent = `${isSalida ? 'Salen' : 'Ingresan'} ${quantity} ${unit} de ${name}.`;
            summary.classList.remove('is-error');
        }
    }
}

function abrirComprobante(row) {
    if (!row) {
        return;
    }

    asignarTexto('[data-voucher-code]', row.dataset.voucher);
    asignarTexto('[data-voucher-movement]', row.dataset.code);
    asignarTexto('[data-voucher-type]', row.dataset.type);
    asignarTexto('[data-voucher-item-type]', row.dataset.itemTypeLabel);
    asignarTexto('[data-voucher-name]', row.dataset.name);
    const sign = row.dataset.type?.toLowerCase() === 'salida' ? '-' : '+';
    asignarTexto('[data-voucher-quantity]', `${sign}${row.dataset.quantity} ${row.dataset.unit}`);
    asignarTexto('[data-voucher-old]', `${row.dataset.stockOld} ${row.dataset.unit}`);
    asignarTexto('[data-voucher-new]', `${row.dataset.stockNew} ${row.dataset.unit}`);
    asignarTexto('[data-voucher-date]', `${row.dataset.date} - ${row.dataset.time}`);
    asignarTexto('[data-voucher-warehouse]', row.dataset.warehouse);
    asignarTexto(
        '[data-voucher-summary]',
        `${row.dataset.type} de ${sign}${row.dataset.quantity} ${row.dataset.unit} de ${row.dataset.name}. ` +
        `El stock fue actualizado de ${row.dataset.stockOld} a ${row.dataset.stockNew} ${row.dataset.unit}.`
    );
    abrirModal(document.querySelector('#movementVoucherModal'));
}

function abrirModal(modal) {
    modal?.classList.add('is-open');
    modal?.setAttribute('aria-hidden', 'false');
}

function cerrarModal(modal) {
    modal?.classList.remove('is-open');
    modal?.setAttribute('aria-hidden', 'true');
}

function asignarTexto(selector, value) {
    const element = document.querySelector(selector);
    if (element) {
        element.textContent = value || '';
    }
}

function asignarValor(selector, value) {
    const element = document.querySelector(selector);
    if (element) {
        element.value = value ?? '';
    }
}
