document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('[data-request-detail]').forEach((button) => {
        button.addEventListener('click', () => abrirDetalleSolicitud(button.closest('tr')));
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

function abrirDetalleSolicitud(row) {
    if (!row) {
        return;
    }

    asignarTexto('[data-request-code]', row.dataset.code);
    asignarTexto('[data-request-date]', row.dataset.date);
    asignarTexto('[data-request-provider]', row.dataset.provider);
    asignarTexto('[data-request-status]', row.dataset.status);
    actualizarClaseEstado('[data-request-status]', row.dataset.status);
    renderInsumosSolicitud(row.dataset.details || '');
    abrirModal(document.querySelector('#requestDetailModal'));
}

function actualizarClaseEstado(selector, status) {
    const element = document.querySelector(selector);
    if (!element) {
        return;
    }
    const normalized = String(status || '').toLowerCase();
    element.className = 'status-pill';
    if (normalized === 'cotizado' || normalized === 'aprobada') {
        element.classList.add('normal');
    } else if (normalized === 'rechazada') {
        element.classList.add('low');
    } else {
        element.classList.add('pending');
    }
}

function renderInsumosSolicitud(rawDetails) {
    const tbody = document.querySelector('[data-request-detail-items]');
    if (!tbody) {
        return;
    }

    tbody.innerHTML = '';
    rawDetails.split(';;')
        .filter(Boolean)
        .forEach((item) => {
            const [insumo, cantidad, unidad] = item.split('|');
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${escapeHtml(insumo)}</td>
                <td><strong>${escapeHtml(cantidad)}</strong></td>
                <td>${escapeHtml(unidad)}</td>
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
