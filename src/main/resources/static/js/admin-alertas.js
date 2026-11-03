document.addEventListener('DOMContentLoaded', () => {
    const search = document.querySelector('#alertSearch');
    const typeFilter = document.querySelector('#alertTypeFilter');
    const itemFilter = document.querySelector('#alertItemFilter');

    search?.addEventListener('input', filtrarAlertas);
    typeFilter?.addEventListener('change', filtrarAlertas);
    itemFilter?.addEventListener('change', filtrarAlertas);

    document.querySelectorAll('[data-alert-detail]').forEach((button) => {
        button.addEventListener('click', () => abrirDetalle(button.closest('tr')));
    });
    document.querySelectorAll('[data-alert-notify]').forEach((button) => {
        button.addEventListener('click', () => notificarAlerta(button.closest('tr')));
    });
    document.querySelector('[data-notify-from-detail]')?.addEventListener('click', () => {
        const code = document.querySelector('[data-alert-code]')?.textContent;
        const row = Array.from(document.querySelectorAll('[data-alert-search]'))
                .find((item) => item.dataset.code === code);
        notificarAlerta(row);
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

function filtrarAlertas() {
    const query = document.querySelector('#alertSearch')?.value.trim().toLowerCase() || '';
    const type = document.querySelector('#alertTypeFilter')?.value || '';
    const itemType = document.querySelector('#alertItemFilter')?.value || '';

    document.querySelectorAll('[data-alert-search]').forEach((row) => {
        const matchesSearch = row.dataset.alertSearch.includes(query);
        const matchesType = !type || row.dataset.alertType === type;
        const matchesItem = !itemType || row.dataset.itemType === itemType;
        row.style.display = matchesSearch && matchesType && matchesItem ? '' : 'none';
    });
}

function abrirDetalle(row) {
    if (!row) {
        return;
    }

    llenarDetalle('[data-alert-code]', row.dataset.code);
    llenarDetalle('[data-alert-date]', `${row.dataset.date} - ${row.dataset.time}`);
    llenarDetalle('[data-alert-type]', row.dataset.type);
    llenarDetalle('[data-alert-item-type]', row.dataset.itemTypeLabel);
    llenarDetalle('[data-alert-name]', row.dataset.name);
    llenarDetalle('[data-alert-stock]', `${row.dataset.stock} ${row.dataset.unit}`);
    llenarDetalle('[data-alert-min]', `${row.dataset.min} ${row.dataset.unit}`);
    llenarDetalle('[data-alert-limit]', `${row.dataset.limit} ${row.dataset.unit}`);

    const state = document.querySelector('[data-alert-state]');
    if (state) {
        state.textContent = row.dataset.status || 'Pendiente';
        state.className = row.dataset.status === 'Notificada' ? 'status-pill normal' : 'status-pill pending';
    }

    const subtitle = document.querySelector('[data-alert-detail-subtitle]');
    if (subtitle) {
        subtitle.textContent = `Informacion de la alerta generada por ${row.dataset.alertType}.`;
    }

    const message = document.querySelector('[data-alert-message]');
    if (message) {
        const data = datosAlerta(row);
        message.className = `stock-message ${data.className}`;
        message.innerHTML = data.message;
    }

    abrirModal(document.querySelector('#alertDetailModal'));
}

function notificarAlerta(row) {
    if (!row) {
        return;
    }

    row.dataset.status = 'Notificada';
    const status = row.querySelector('[data-alert-status]');
    if (status) {
        status.textContent = 'Notificada';
        status.className = 'status-pill normal';
    }

    llenarDetalle('[data-notified-code]', row.dataset.code);
    llenarDetalle('[data-notified-type]', row.dataset.type);
    llenarDetalle('[data-notified-item-type]', row.dataset.itemTypeLabel);
    llenarDetalle('[data-notified-name]', row.dataset.name);
    llenarDetalle('[data-notified-stock]', `${row.dataset.stock} ${row.dataset.unit}`);
    llenarDetalle('[data-notified-min]', `${row.dataset.min} ${row.dataset.unit}`);
    llenarDetalle('[data-notified-limit]', `${row.dataset.limit} ${row.dataset.unit}`);
    llenarDetalle('[data-notified-date]', new Date().toLocaleString('es-PE', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    }));

    cerrarModal(document.querySelector('#alertDetailModal'));
    abrirModal(document.querySelector('#alertNotifiedModal'));
}

function datosAlerta(row) {
    const stock = Number.parseInt(row.dataset.stock || '0', 10);
    const min = Number.parseInt(row.dataset.min || '0', 10);
    const limit = Number.parseInt(row.dataset.limit || '0', 10);
    const unit = row.dataset.unit || '';

    if (row.dataset.alertType === 'stock bajo') {
        return {
            className: 'low',
            message: `<strong>El stock actual esta por debajo del stock minimo configurado.</strong><br>
                      Faltan ${min - stock} ${unit} para alcanzar el stock minimo.`
        };
    }

    return {
        className: 'over',
        message: `<strong>El stock actual supera el stock limite configurado.</strong><br>
                  Hay un exceso de ${stock - limit} ${unit} sobre el limite permitido.`
    };
}

function abrirModal(modal) {
    modal?.classList.add('is-open');
    modal?.setAttribute('aria-hidden', 'false');
}

function cerrarModal(modal) {
    modal?.classList.remove('is-open');
    modal?.setAttribute('aria-hidden', 'true');
}

function llenarDetalle(selector, value) {
    const element = document.querySelector(selector);
    if (element) {
        element.textContent = value || '';
    }
}
