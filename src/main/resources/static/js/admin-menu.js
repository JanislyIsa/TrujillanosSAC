document.addEventListener('DOMContentLoaded', () => {
    const logoButton = document.querySelector('[data-admin-menu-toggle]');
    const drawer = document.querySelector('#adminDrawer');
    const closeButton = document.querySelector('[data-admin-menu-close]');
    const backdrop = document.querySelector('[data-admin-menu-backdrop]');

    if (!logoButton || !drawer) {
        return;
    }

    const openDrawer = () => {
        drawer.classList.add('is-open');
        drawer.setAttribute('aria-hidden', 'false');
    };

    const closeDrawer = () => {
        drawer.classList.remove('is-open');
        drawer.setAttribute('aria-hidden', 'true');
    };

    logoButton.addEventListener('click', openDrawer);
    closeButton?.addEventListener('click', closeDrawer);
    backdrop?.addEventListener('click', closeDrawer);

    document.addEventListener('keydown', (event) => {
        if (event.key === 'Escape') {
            closeDrawer();
        }
    });
});
