-- Ejecutar como SYSTEM (o administrador con CREATE USER y GRANT) EN LA PDB.
-- Ejemplo de conexion: localhost:1522/FREEPDB1, no CDB$ROOT.
-- REEMPLAZAR la clave de ejemplo antes de ejecutar. No reutilizar la del login web.
DECLARE
BEGIN
    IF SYS_CONTEXT('USERENV', 'CON_NAME') = 'CDB$ROOT' THEN
        RAISE_APPLICATION_ERROR(-20001, 'Conectese a FREEPDB1/XEPDB1 antes de crear el usuario local.');
    END IF;
END;
/

CREATE USER ELTRUJILLANO IDENTIFIED BY "1234567"
    DEFAULT TABLESPACE USERS
    TEMPORARY TABLESPACE TEMP
    QUOTA 200M ON USERS;

GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE TO ELTRUJILLANO;

-- El propietario puede consultar/insertar/actualizar/eliminar en sus propias tablas.
-- No necesita DBA, RESOURCE, privilegios ANY ni UNLIMITED TABLESPACE.
-- No se borra ni se cambia la clave de un usuario que ya exista.
