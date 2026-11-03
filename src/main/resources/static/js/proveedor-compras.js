document.addEventListener('DOMContentLoaded', () => {
    const search = document.querySelector('#providerSearch');
    search?.addEventListener('input', () => {
        const query = search.value.trim().toLowerCase();
        document.querySelectorAll('[data-provider-search]').forEach((row) => {
            row.style.display = row.dataset.providerSearch.includes(query) ? '' : 'none';
        });
    });

    document.querySelectorAll('[data-provider-request-detail]').forEach((button) => {
        button.addEventListener('click', () => abrirDetalleSolicitud(button.closest('tr')));
    });

    document.querySelectorAll('[data-provider-quote-detail]').forEach((button) => {
        button.addEventListener('click', () => abrirDetalleCotizacion(button.closest('tr')));
    });

    document.querySelectorAll('[data-payment-upload]').forEach((button) => {
        button.addEventListener('click', () => abrirSubidaComprobante(button.closest('tr')));
    });

    const paymentForm = document.querySelector('[data-payment-form]');
    const paymentInput = paymentForm?.querySelector('input[type="file"][name="qrYape"]');
    paymentInput?.addEventListener('change', () => {
        asignarTexto('[data-payment-file-label]', paymentInput.files?.[0]?.name || 'Subir QR de Yape');
    });
    paymentForm?.addEventListener('submit', (event) => {
        if (!paymentForm.action || paymentForm.action.endsWith('/proveedor/cotizaciones')) {
            event.preventDefault();
            alert('No se encontro la cotizacion para enviar el comprobante. Vuelve a abrir la ventana desde Subir comprobante.');
            return;
        }
        if (!paymentInput?.files?.length) {
            event.preventDefault();
            alert('Selecciona una imagen del QR de Yape.');
        }
    });

    document.querySelectorAll('[data-quote-price]').forEach((input) => {
        input.addEventListener('input', recalcularCotizacion);
    });
    recalcularCotizacion();

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

function abrirDetalleSolicitud(row) {
    if (!row) {
        return;
    }
    asignarTexto('[data-request-code]', row.dataset.code);
    asignarTexto('[data-request-date]', row.dataset.date);
    asignarTexto('[data-request-status]', row.dataset.status);
    actualizarClaseEstado('[data-request-status]', row.dataset.status);
    const tbody = document.querySelector('[data-request-items]');
    if (tbody) {
        tbody.innerHTML = '';
        (row.dataset.details || '').split(';;').filter(Boolean).forEach((detail) => {
            const [insumo, cantidad, unidad] = detail.split('|');
            const tr = document.createElement('tr');
            tr.innerHTML = `<td>${escapeHtml(insumo)}</td><td><strong>${escapeHtml(cantidad)} ${escapeHtml(unidad)}</strong></td>`;
            tbody.appendChild(tr);
        });
    }
    abrirModal(document.querySelector('#providerRequestModal'));
}

function abrirDetalleCotizacion(row) {
    if (!row) {
        return;
    }
    asignarTexto('[data-quote-code]', row.dataset.code);
    asignarTexto('[data-quote-order]', row.dataset.orderCode || 'Sin orden generada');
    asignarTexto('[data-quote-date]', row.dataset.date);
    asignarTexto('[data-quote-delivery]', row.dataset.delivery);
    asignarTexto('[data-quote-total-modal]', `S/ ${row.dataset.total}`);
    asignarTexto('[data-quote-status]', row.dataset.status);
    actualizarClaseEstado('[data-quote-status]', row.dataset.status);

    const qrImg = document.querySelector('[data-quote-qr-img]');
    const qrEmpty = document.querySelector('[data-quote-qr-empty]');
    if (row.dataset.qr) {
        qrImg.src = row.dataset.qr;
        qrImg.hidden = false;
        qrEmpty.hidden = true;
    } else {
        qrImg.removeAttribute('src');
        qrImg.hidden = true;
        qrEmpty.hidden = false;
    }
    abrirModal(document.querySelector('#providerQuoteModal'));
}

function actualizarClaseEstado(selector, status) {
    const element = document.querySelector(selector);
    if (!element) {
        return;
    }
    const normalized = String(status || '').toLowerCase();
    element.className = 'status-pill';
    if (normalized === 'pagado' || normalized === 'aceptada') {
        element.classList.add('normal');
    } else if (normalized === 'rechazada') {
        element.classList.add('low');
    } else {
        element.classList.add('pending');
    }
}

function abrirSubidaComprobante(row) {
    if (!row || !row.dataset.quoteId) {
        return;
    }
    const form = document.querySelector('[data-payment-form]');
    if (form) {
        form.action = `/proveedor/cotizaciones/${row.dataset.quoteId}/comprobante`;
        form.reset();
    }
    asignarTexto('[data-payment-file-label]', 'Subir QR de Yape');
    abrirModal(document.querySelector('#paymentUploadModal'));
}

function recalcularCotizacion() {
    let total = 0;
    document.querySelectorAll('[data-quote-price]').forEach((input) => {
        const row = input.closest('tr');
        const qty = Number(row?.querySelector('[data-quote-qty]')?.dataset.quoteQty || 0);
        const price = Number(input.value || 0);
        const subtotal = qty * price;
        total += subtotal;
        const subtotalElement = row?.querySelector('[data-quote-subtotal]');
        if (subtotalElement) {
            subtotalElement.textContent = `S/ ${subtotal.toFixed(2)}`;
        }
    });
    const totalElement = document.querySelector('[data-quote-total]');
    if (totalElement) {
        totalElement.textContent = `S/ ${total.toFixed(2)}`;
    }
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
