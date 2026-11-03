document.addEventListener('DOMContentLoaded', () => {
    const carrito = obtenerCarrito();
    const payButton = document.querySelector('#payButton');

    renderizarResumen(carrito);
    configurarMetodoPago();

    if (carrito.length === 0) {
        mostrarError('Tu carrito esta vacio.');
        payButton.disabled = true;
    }

    payButton?.addEventListener('click', () => confirmarPedido(carrito));
});

function obtenerCarrito() {
    return JSON.parse(localStorage.getItem(obtenerCarritoKey())) || [];
}

function obtenerCarritoKey() {
    return document.body.dataset.cartKey || 'carrito:invitado';
}

function renderizarResumen(carrito) {
    const checkoutItems = document.querySelector('#checkoutItems');
    const checkoutTotal = document.querySelector('#checkoutTotal');
    const total = carrito.reduce((suma, item) => suma + item.precio * item.cantidad, 0);

    checkoutItems.innerHTML = '';

    carrito.forEach((item) => {
        const row = document.createElement('div');
        row.className = 'summary-row';
        row.innerHTML = `
            <span>${item.nombre} ${item.presentacion ?? 'Caja'} x${item.cantidad}</span>
            <strong>S/ ${(item.precio * item.cantidad).toFixed(2)}</strong>
        `;
        checkoutItems.appendChild(row);
    });

    checkoutTotal.textContent = `S/ ${total.toFixed(2)}`;
}

function configurarMetodoPago() {
    const yapeBox = document.querySelector('#yapeBox');
    const cashMessage = document.querySelector('#cashMessage');

    document.querySelectorAll('input[name="metodoPago"]').forEach((radio) => {
        radio.addEventListener('change', () => {
            const efectivo = radio.value === 'EFECTIVO' && radio.checked;
            yapeBox.classList.toggle('is-hidden', efectivo);
            cashMessage.classList.toggle('is-visible', efectivo);
        });
    });
}

async function confirmarPedido(carrito) {
    const payButton = document.querySelector('#payButton');
    const metodoPago = document.querySelector('input[name="metodoPago"]:checked')?.value ?? 'YAPE';
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

    payButton.disabled = true;
    mostrarError('');

    try {
        const response = await fetch('/ventas/checkout/confirmar', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                ...(csrfToken && csrfHeader ? {[csrfHeader]: csrfToken} : {})
            },
            body: JSON.stringify({
                metodoPago,
                items: carrito.map((item) => ({
                    idProducto: Number(item.id),
                    cantidad: Number(item.cantidad)
                }))
            })
        });

        const result = await response.json().catch(() => ({}));

        if (!response.ok) {
            throw new Error(result.message || 'No se pudo registrar el pedido.');
        }

        localStorage.removeItem(obtenerCarritoKey());
        window.location.href = result.redirectUrl;
    } catch (error) {
        mostrarError(error.message);
        payButton.disabled = false;
    }
}

function mostrarError(message) {
    const checkoutError = document.querySelector('#checkoutError');
    checkoutError.textContent = message;
}
