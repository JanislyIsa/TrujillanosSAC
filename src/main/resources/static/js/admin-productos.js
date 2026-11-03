document.addEventListener('DOMContentLoaded', () => {
    const modal = document.querySelector('#productModal');
    const form = document.querySelector('.product-modal-card');
    const search = document.querySelector('#adminProductSearch');
    const imageInput = document.querySelector('#productImageInput');
    const imageUploadText = document.querySelector('#imageUploadText');
    const deleteModal = document.querySelector('#deleteModal');
    const deleteForm = document.querySelector('.delete-modal-card');

    document.querySelector('[data-open-product-modal]')?.addEventListener('click', () => {
        prepararModalCrear(form, imageInput, imageUploadText);
        abrirModal(modal);
    });

    document.querySelectorAll('[data-close-product-modal]').forEach((button) => {
        button.addEventListener('click', () => cerrarModal(modal));
    });

    modal?.addEventListener('click', (event) => {
        if (event.target === modal) {
            cerrarModal(modal);
        }
    });

    document.querySelectorAll('[data-edit-product]').forEach((button) => {
        button.addEventListener('click', () => {
            prepararModalEditar(button.closest('tr'), form, imageInput, imageUploadText);
            abrirModal(modal);
        });
    });

    document.querySelectorAll('[data-delete-product]').forEach((button) => {
        button.addEventListener('click', () => {
            const row = button.closest('tr');
            deleteForm.action = `/admin/productos/${row.dataset.id}/eliminar`;
            abrirModal(deleteModal);
        });
    });

    document.querySelector('[data-close-delete-modal]')?.addEventListener('click', () => {
        cerrarModal(deleteModal);
    });

    deleteModal?.addEventListener('click', (event) => {
        if (event.target === deleteModal) {
            cerrarModal(deleteModal);
        }
    });

    search?.addEventListener('input', () => {
        const query = search.value.trim().toLowerCase();

        document.querySelectorAll('[data-product-name]').forEach((row) => {
            row.style.display = row.dataset.productName.includes(query) ? '' : 'none';
        });
    });

    imageInput?.addEventListener('change', () => {
        imageUploadText.textContent = imageInput.files?.[0]?.name || 'Cargar imagen';
    });
});

function prepararModalCrear(form, imageInput, imageUploadText) {
    form.action = '/admin/productos';
    form.reset();
    imageInput.value = '';
    imageUploadText.textContent = 'Cargar imagen';
    document.querySelector('#productModalTitle').textContent = 'Añadir un nuevo producto';
    document.querySelector('#productModalSubtitle').textContent = 'Ingrese los datos requeridos';
    document.querySelector('#productSubmitButton').textContent = 'Agregar';
}

function prepararModalEditar(row, form, imageInput, imageUploadText) {
    form.action = `/admin/productos/${row.dataset.id}/editar`;
    form.reset();
    imageInput.value = '';
    imageUploadText.textContent = 'Cambiar imagen';

    document.querySelector('#productModalTitle').textContent = 'Editar producto';
    document.querySelector('#productModalSubtitle').textContent = 'Edite los datos requeridos';
    document.querySelector('#productSubmitButton').textContent = 'Guardar';
    document.querySelector('#productNameInput').value = row.dataset.nombre;
    document.querySelector('#productPriceInput').value = row.dataset.precio;
    document.querySelector('#productStockInput').value = row.dataset.stock;
    document.querySelector('#productDescriptionInput').value = row.dataset.descripcion;
    document.querySelector('#productStockMinInput').value = row.dataset.stockMinimo;
    document.querySelector('#productStockLimitInput').value = row.dataset.stockLimite;
    document.querySelector('#productCategoryInput').value = row.dataset.categoria;
    document.querySelector('#productPresentationInput').value = row.dataset.presentacion;
}

function abrirModal(modal) {
    modal?.classList.add('is-open');
    modal?.setAttribute('aria-hidden', 'false');
}

function cerrarModal(modal) {
    modal?.classList.remove('is-open');
    modal?.setAttribute('aria-hidden', 'true');
}
