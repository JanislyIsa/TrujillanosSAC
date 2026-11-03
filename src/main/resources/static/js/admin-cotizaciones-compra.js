document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('[data-quote-detail]').forEach((button) => {
        button.addEventListener('click', () => abrirDetalleCotizacion(button.closest('tr')));
    });

    document.querySelectorAll('[data-quote-respond]').forEach((button) => {
        button.addEventListener('click', () => abrirRespuestaCotizacion(button.closest('tr')));
    });

    document.querySelector('[data-generate-form]')?.addEventListener('submit', (event) => {
        if (!confirm('Deseas generar la orden de compra con esta cotizacion? La cotizacion pasara a estado Aceptada.')) {
            event.preventDefault();
        }
    });

    document.querySelector('[data-reject-form]')?.addEventListener('submit', (event) => {
        if (!confirm('Deseas rechazar esta cotizacion? Cambiara a estado Rechazada.')) {
            event.preventDefault();
        }
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
});

function abrirDetalleCotizacion(row) {
    if (!row) {
        return;
    }

    asignarTexto('[data-quote-code]', row.dataset.code);
    asignarTexto('[data-quote-status]', row.dataset.status);
    asignarTexto('[data-quote-request]', row.dataset.request);
    asignarTexto('[data-quote-provider]', row.dataset.provider);
    asignarTexto('[data-quote-sent]', row.dataset.sent);
    asignarTexto('[data-quote-delivery]', row.dataset.delivery);
    asignarTexto('[data-quote-total]', `S/ ${row.dataset.total}`);
    renderDetalles(row.dataset.details || '');
    abrirModal(document.querySelector('#quoteDetailModal'));
}

function abrirRespuestaCotizacion(row) {
    if (!row || row.dataset.canRespond !== 'true') {
        return;
    }

    asignarTexto('[data-response-code]', row.dataset.code);
    asignarTexto('[data-response-provider]', row.dataset.provider);
    asignarTexto('[data-response-total]', `S/ ${row.dataset.total}`);
    asignarTexto('[data-response-status]', row.dataset.status);

    const generateForm = document.querySelector('[data-generate-form]');
    const rejectForm = document.querySelector('[data-reject-form]');
    if (generateForm) {
        generateForm.action = row.dataset.generateUrl;
    }
    if (rejectForm) {
        rejectForm.action = row.dataset.rejectUrl;
    }

    abrirModal(document.querySelector('#quoteResponseModal'));
}

function renderDetalles(rawDetails) {
    const tbody = document.querySelector('[data-quote-detail-items]');
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
