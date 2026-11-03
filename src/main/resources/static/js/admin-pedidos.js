document.addEventListener('DOMContentLoaded', () => {
    const search = document.querySelector('#adminOrderSearch');

    search?.addEventListener('input', () => {
        const query = search.value.trim().toLowerCase();

        document.querySelectorAll('[data-order-search]').forEach((row) => {
            row.style.display = row.dataset.orderSearch.includes(query) ? '' : 'none';
        });
    });

    document.querySelectorAll('[data-save-order-state]').forEach((button) => {
        button.addEventListener('click', () => {
            button.closest('tr')?.querySelector('.order-status-form')?.submit();
        });
    });
});
