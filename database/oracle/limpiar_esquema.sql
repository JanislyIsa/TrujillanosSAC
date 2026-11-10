-- BORRA TODAS LAS TABLAS (y sus datos) DEL USUARIO CONECTADO, para reinstalar desde cero.
-- Solo para bases de desarrollo. Ejecutar conectado como el usuario de la aplicacion,
-- nunca como SYS o SYSTEM. Despues ejecutar instalar_esquema.sql (o 01, 02 y 04).
SELECT USER AS USUARIO_QUE_SE_VA_A_VACIAR FROM DUAL;
BEGIN
    IF SYS_CONTEXT('USERENV','SESSION_USER') IN ('SYS','SYSTEM') THEN
        RAISE_APPLICATION_ERROR(-20001, 'No ejecutar como SYS o SYSTEM.');
    END IF;
    FOR t IN (SELECT TABLE_NAME FROM USER_TABLES) LOOP
        EXECUTE IMMEDIATE 'DROP TABLE "' || t.TABLE_NAME || '" CASCADE CONSTRAINTS PURGE';
    END LOOP;
END;
/
SELECT COUNT(*) AS TABLAS_RESTANTES FROM USER_TABLES;
