document.addEventListener('DOMContentLoaded', () => {
    const authenticated = document.body.dataset.authenticated === 'true';
    const modal = document.querySelector('#loginModal');
    const closeModal = document.querySelector('.modal-close');
    const search = document.querySelector('#productSearch');
    const cards = document.querySelectorAll('.product-card');

    document.querySelectorAll('.add-button').forEach((button) => {
        button.addEventListener('click', () => {
            if (!authenticated) {
                modal?.classList.add('is-open');
                modal?.setAttribute('aria-hidden', 'false');
                return;
            }

            agregarAlCarrito(button);
            mostrarMensajeCarrito();
        });
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

    search?.addEventListener('input', () => {
        const query = search.value.trim().toLowerCase();

        cards.forEach((card) => {
            const productName = card.querySelector('h3')?.textContent.toLowerCase() ?? '';
            card.style.display = productName.includes(query) ? '' : 'none';
        });
    });
});

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
