# Preparar PostgreSQL

La aplicacion consulta `products` y `categories` al iniciar. El archivo
`schema.sql` se ejecuta manualmente; JDBC no lo ejecuta automaticamente.

1. En el editor SQL de Neon, selecciona la misma rama y base de datos que
   aparecen en la URL JDBC de `ConnectionPool.java` (actualmente `neondb`).
2. Comprueba la base y las tablas existentes:

```sql
SELECT current_database(), current_user, current_schema();
SHOW search_path;
SELECT table_schema, table_name
FROM information_schema.tables
WHERE table_name IN ('products', 'categories');
```

3. Si las tablas ya existen en otro esquema, ajusta el esquema de la conexion
   JDBC mediante `currentSchema=nombre_del_esquema` en su URL. Si seleccionaste
   otra base o rama, corrige la conexion. No crees tablas vacias para sustituir
   datos existentes en otro lugar.
4. Si esta es la base correcta y faltan las tablas, ejecuta `schema.sql`.
   El script utiliza el esquema `public`, que debe estar en el `search_path`
   de la conexion de la aplicacion. No borra datos ni inserta ejemplos.
   `IF NOT EXISTS` no valida ni migra tablas preexistentes.
5. Recarga el proyecto Maven en IntelliJ y vuelve a ejecutar `Main`.

Una base recien preparada tendra la lista de productos vacia. Puedes agregar
productos desde el menu de la aplicacion.
