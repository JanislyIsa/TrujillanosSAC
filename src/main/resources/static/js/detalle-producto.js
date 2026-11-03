document.addEventListener('DOMContentLoaded', () => {
    const authenticated = document.body.dataset.authenticated === 'true';
    const addButton = document.querySelector('.detail-add-button');
    const optionButtons = document.querySelectorAll('.option-buttons button');
    const selectedPresentation = document.querySelector('#selectedPresentation');
    const selectedPrice = document.querySelector('#selectedPrice');
    const stock = document.querySelector('#productStock');
    const modal = document.querySelector('#loginModal');
    const closeModal = document.querySelector('.modal-close');

    optionButtons.forEach((button) => {
        button.addEventListener('click', () => {
            optionButtons.forEach((option) => option.classList.remove('is-selected'));
            button.classList.add('is-selected');

            const precio = Number(button.dataset.precio);

            if (addButton) {
                addButton.dataset.id = button.dataset.id;
                addButton.dataset.presentacion = button.dataset.presentacion;
                addButton.dataset.precio = String(precio);
                addButton.disabled = button.dataset.available !== 'true';
                addButton.textContent = addButton.disabled
                    ? 'Sin stock'
                    : `Agregar ${button.dataset.presentacion.toLowerCase()}`;
            }

            if (selectedPresentation) {
                selectedPresentation.textContent = button.dataset.presentacion;
            }

            if (selectedPrice) {
                selectedPrice.textContent = formatearPrecio(precio);
            }

            if (stock) {
                stock.textContent = button.dataset.stock;
            }
        });
    });

    addButton?.addEventListener('click', () => {
        if (!authenticated) {
            modal?.classList.add('is-open');
            modal?.setAttribute('aria-hidden', 'false');
            return;
        }

        agregarAlCarrito(addButton);
        mostrarMensajeCarrito();
    });

    closeModal?.addEventListener('click', () => {
        modal?.classList.remove('is-open');
        modal?.setAttribute('aria-hidden', 'true');
    });

    modal?.addEventListener('click', (event) => {
        if (event.target === modal) {
            modal.classList.remove('is-open');
            modal.setAttribute('aria-hidden', 'true');
        }
    });
});

function formatearPrecio(precio) {
    return precio.toLocaleString('es-PE', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });
}

function agregarAlCarrito(button) {
    const producto = {
        id: button.dataset.id,
        nombre: button.dataset.nombre,
        precio: Number(button.dataset.precio),
        presentacion: button.dataset.presentacion,
        imagen: button.dataset.imagen,
        cantidad: 1
    };

    const carrito = obtenerCarrito();
    const existente = carrito.find((item) => item.id === producto.id
        && item.presentacion === producto.presentacion);

    if (existente) {
        existente.cantidad += 1;
    } else {
        carrito.push(producto);
    }

    guardarCarrito(carrito);
}

function obtenerCarrito() {
    return JSON.parse(localStorage.getItem(obtenerCarritoKey())) || [];
}

function guardarCarrito(carrito) {
    localStorage.setItem(obtenerCarritoKey(), JSON.stringify(carrito));
}

function obtenerCarritoKey() {
    return document.body.dataset.cartKey || 'carrito:invitado';
}

function mostrarMensajeCarrito() {
    const toast = document.querySelector('#cartToast');

    if (!toast) {
        return;
    }

    toast.classList.add('is-visible');

    setTimeout(() => {
        toast.classList.remove('is-visible');
    }, 4000);
}
