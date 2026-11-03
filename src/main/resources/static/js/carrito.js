document.addEventListener('DOMContentLoaded', () => {
    renderizarCarrito();

    document.querySelector('#checkoutButton')?.addEventListener('click', () => {
        if (obtenerCarrito().length === 0) {
            return;
        }

        window.location.href = '/ventas/checkout';
    });
});

function obtenerCarrito() {
    return JSON.parse(localStorage.getItem(obtenerCarritoKey())) || [];
}

function guardarCarrito(carrito) {
    localStorage.setItem(obtenerCarritoKey(), JSON.stringify(carrito));
}

function obtenerCarritoKey() {
    return document.body.dataset.cartKey || 'carrito:invitado';
}

function renderizarCarrito() {
    const cartList = document.querySelector('#cartList');
    const emptyCart = document.querySelector('#emptyCart');
    const carrito = obtenerCarrito();

    cartList.innerHTML = '';
    emptyCart.classList.toggle('is-visible', carrito.length === 0);

    carrito.forEach((item) => {
        const article = document.createElement('article');
        article.className = 'cart-item';
        article.innerHTML = `
            <img src="${item.imagen}" alt="${item.nombre}">
            <div>
                <h2>${item.nombre}</h2>
                <p class="item-presentation">${item.presentacion ?? 'Caja'}</p>
                <div class="item-actions">
                    <div class="quantity-control">
                        <button type="button" data-action="decrease" data-id="${item.id}" data-presentacion="${item.presentacion ?? 'Caja'}">-</button>
                        <span>${item.cantidad}</span>
                        <button type="button" data-action="increase" data-id="${item.id}" data-presentacion="${item.presentacion ?? 'Caja'}">+</button>
                    </div>
                    <button class="remove-button" type="button" data-action="remove" data-id="${item.id}" data-presentacion="${item.presentacion ?? 'Caja'}">
                        <i class="fa-regular fa-trash-can"></i>
                        Eliminar
                    </button>
                </div>
            </div>
            <strong class="item-total">S/ ${(item.precio * item.cantidad).toFixed(2)}</strong>
        `;

        cartList.appendChild(article);
    });

    actualizarResumen(carrito);
    activarBotones();
}

function activarBotones() {
    document.querySelectorAll('[data-action]').forEach((button) => {
        button.addEventListener('click', () => {
            const carrito = obtenerCarrito();
            const producto = carrito.find((item) => item.id === button.dataset.id
                && (item.presentacion ?? 'Caja') === button.dataset.presentacion);

            if (!producto) {
                return;
            }

            if (button.dataset.action === 'increase') {
                producto.cantidad += 1;
            }

            if (button.dataset.action === 'decrease') {
                producto.cantidad -= 1;
            }

            const actualizado = button.dataset.action === 'remove'
                ? carrito.filter((item) => item.id !== button.dataset.id
                    || (item.presentacion ?? 'Caja') !== button.dataset.presentacion)
                : carrito.filter((item) => item.cantidad > 0);

            guardarCarrito(actualizado);
            renderizarCarrito();
        });
    });
}

function actualizarResumen(carrito) {
    const summaryItems = document.querySelector('#summaryItems');
    const totalElement = document.querySelector('#total');
    const total = carrito.reduce((suma, item) => suma + item.precio * item.cantidad, 0);

    summaryItems.innerHTML = '';

    carrito.forEach((item) => {
        const subtotal = item.precio * item.cantidad;
        const row = document.createElement('div');
        row.className = 'summary-row';
        row.innerHTML = `
            <span>${item.nombre} ${item.presentacion ?? 'Caja'}</span>
            <strong>S/ ${subtotal.toFixed(2)}</strong>
        `;

        summaryItems.appendChild(row);
    });

    totalElement.textContent = `S/ ${total.toFixed(2)}`;
}
