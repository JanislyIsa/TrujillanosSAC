-- SOLO SQL*Plus/SQLcl o SQL Developer F5; conectado como ELTRUJILLANO.
-- Usa rutas relativas a este archivo. Detiene la ejecucion al primer error.
WHENEVER OSERROR EXIT FAILURE ROLLBACK
WHENEVER SQLERROR EXIT SQL.SQLCODE ROLLBACK
SET DEFINE OFF
@@01_tablas.sql
@@02_catalogos.sql
@@03_datos_demo.sql
@@04_verificar.sql
PROMPT Instalacion de desarrollo completada.
