document.addEventListener('DOMContentLoaded', () => {
    const toggle = document.querySelector('.password-toggle');
    const password = document.querySelector('#password');

    if (!toggle || !password) {
        return;
    }

    toggle.addEventListener('click', () => {
        const isHidden = password.type === 'password';
        const icon = toggle.querySelector('i');

        password.type = isHidden ? 'text' : 'password';
        toggle.setAttribute('aria-label', isHidden ? 'Ocultar contrasena' : 'Mostrar contrasena');
        icon?.classList.toggle('fa-eye');
        icon?.classList.toggle('fa-eye-slash');
    });
});
