document.addEventListener('DOMContentLoaded', () => {
    const search = document.querySelector('#orderSearch');

    search?.addEventListener('input', () => {
        const query = search.value.trim().toLowerCase();
        document.querySelectorAll('[data-order-search]').forEach((row) => {
            row.style.display = row.dataset.orderSearch.includes(query) ? '' : 'none';
        });
    });

    document.querySelectorAll('[data-order-detail]').forEach((button) => {
        button.addEventListener('click', () => abrirDetalleOrden(button.closest('tr')));
    });

    document.querySelectorAll('[data-order-pay]').forEach((button) => {
        button.addEventListener('click', () => abrirPagoOrden(button.closest('tr')));
    });

    document.querySelectorAll('input[name="metodoPago"]').forEach((radio) => {
        radio.addEventListener('change', actualizarMetodoPagoCompra);
    });
    document.querySelector('[data-purchase-payment-form]')?.addEventListener('submit', validarPagoCompra);

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
});

function abrirDetalleOrden(row) {
    if (!row) {
        return;
    }

    asignarTexto('[data-order-code]', row.dataset.code);
    asignarTexto('[data-order-status]', row.dataset.status);
    actualizarClaseEstadoOrden(row.dataset.status);
    asignarTexto('[data-order-provider]', row.dataset.provider);
    asignarTexto('[data-order-date]', row.dataset.date);
    asignarTexto('[data-order-total]', `S/ ${row.dataset.total}`);
    asignarTexto('[data-order-description]', row.dataset.description || 'Sin descripcion');
    renderDetalles(row.dataset.details || '');
    renderQrProveedor(row.dataset.qr || '');
    abrirModal(document.querySelector('#orderDetailModal'));
}

function actualizarClaseEstadoOrden(status) {
    const element = document.querySelector('[data-order-status]');
    if (!element) {
        return;
    }
    element.className = 'status-pill';
    element.classList.add(String(status || '').toLowerCase() === 'pagado' ? 'normal' : 'pending');
}

function abrirPagoOrden(row) {
    if (!row || row.dataset.paid === 'true') {
        return;
    }

    asignarTexto('[data-payment-order-code]', row.dataset.code);
    asignarTexto('[data-payment-provider]', row.dataset.provider);
    asignarTexto('[data-payment-total]', `S/ ${row.dataset.total}`);

    const form = document.querySelector('[data-purchase-payment-form]');
    if (form) {
        form.action = row.dataset.payUrl;
        form.reset();
    }

    const qrImg = document.querySelector('[data-payment-qr-img]');
    const qrEmpty = document.querySelector('[data-payment-qr-empty]');
    if (row.dataset.qr) {
        qrImg.src = row.dataset.qr;
        qrImg.hidden = false;
        qrEmpty.hidden = true;
    } else {
        qrImg.removeAttribute('src');
        qrImg.hidden = true;
        qrEmpty.hidden = false;
    }

    const error = document.querySelector('[data-payment-error]');
    if (error) {
        error.hidden = true;
        error.textContent = '';
    }
    actualizarMetodoPagoCompra();
    abrirModal(document.querySelector('#purchasePaymentModal'));
}

function actualizarMetodoPagoCompra() {
    const metodo = document.querySelector('input[name="metodoPago"]:checked')?.value || 'Yape';
    const yapeBox = document.querySelector('[data-payment-yape-box]');
    const cashNote = document.querySelector('[data-payment-cash-note]');
    yapeBox?.classList.toggle('is-hidden', metodo !== 'Yape');
    cashNote?.classList.toggle('is-visible', metodo === 'Efectivo');
}

function validarPagoCompra(event) {
    const metodo = document.querySelector('input[name="metodoPago"]:checked')?.value || 'Yape';
    const qrImg = document.querySelector('[data-payment-qr-img]');
    const error = document.querySelector('[data-payment-error]');
    if (metodo === 'Yape' && !qrImg?.getAttribute('src')) {
        event.preventDefault();
        if (error) {
            error.textContent = 'El proveedor debe enviar su QR de Yape antes de registrar el pago por Yape.';
            error.hidden = false;
        }
        return;
    }
    if (!confirm('Deseas registrar el pago de esta orden de compra?')) {
        event.preventDefault();
    }
}

function renderQrProveedor(qrUrl) {
    const image = document.querySelector('[data-admin-order-qr-img]');
    const empty = document.querySelector('[data-admin-order-qr-empty]');
    if (!image || !empty) {
        return;
    }
    if (qrUrl) {
        image.src = qrUrl;
        image.hidden = false;
        empty.hidden = true;
    } else {
        image.removeAttribute('src');
        image.hidden = true;
        empty.hidden = false;
    }
}

function renderDetalles(rawDetails) {
    const tbody = document.querySelector('[data-order-detail-items]');
    if (!tbody) {
        return;
    }

    tbody.innerHTML = '';
    rawDetails.split(';;')
        .filter(Boolean)
        .forEach((item) => {
            const [insumo, cantidad, unidad, precio, subtotal] = item.split('|');
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${escapeHtml(insumo)}</td>
                <td>${escapeHtml(cantidad)} ${escapeHtml(unidad)}</td>
                <td>S/ ${escapeHtml(precio)}</td>
                <td><strong>S/ ${escapeHtml(subtotal)}</strong></td>
            `;
            tbody.appendChild(row);
        });
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

function escapeHtml(value) {
    return String(value || '')
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#039;');
}
