# Gestión de productos con Java y JDBC

Proyecto práctico de un curso de **Java intermedio**. Consiste en una aplicación de consola para administrar productos y sus categorías, con persistencia en PostgreSQL mediante JDBC.

El objetivo es integrar programación orientada a objetos, colecciones, interfaces, manejo de excepciones y acceso a bases de datos en una aplicación organizada por responsabilidades.

## Funcionalidades

- Registrar productos con nombre, precio, stock y categoría.
- Mostrar la lista de productos.
- Buscar un producto por su identificador.
- Eliminar un producto por su identificador.
- Modificar el nombre, precio, stock o categoría de un producto, individualmente o en conjunto.
- Guardar los datos en PostgreSQL y cargarlos al iniciar la aplicación.
- Gestionar las categorías desde las operaciones de productos, sin un menú independiente.

## Conceptos del curso aplicados

| Concepto | Aplicación en el proyecto |
| --- | --- |
| Programación orientada a objetos | Modelos `Product` y `Category`, encapsulación y composición. |
| Interfaces | Contrato `ProductRepository` para las operaciones del repositorio. |
| Organización por capas | Vista, controlador, servicio, repositorio y DAO. |
| Colecciones y genéricos | Uso de `List<Product>`, `ArrayList` y métodos genéricos en `Validates`. |
| Lambdas y Streams | Búsquedas, filtros, recorridos y eliminación en la lista de productos. |
| `Optional` | Representación de búsquedas que pueden no encontrar un resultado. |
| Excepciones | `InvalidProductDataException`, `ProductNotFoundException` y `SQLException`. |
| JDBC | Conexiones, consultas parametrizadas con `PreparedStatement`, `ResultSet` y claves generadas. |
| Transacciones | Uso de `setAutoCommit(false)`, `commit()` y `rollback()` en el servicio. |
| Gestión de recursos | Cierre de recursos JDBC con `try-with-resources` y bloques `finally`. |
| Maven y Lombok | Gestión de dependencias y generación de getters, setters y constructores. |

## Tecnologías

Las versiones corresponden a la configuración actual del [pom.xml](pom.xml).

| Tecnología | Versión / uso |
| --- | --- |
| Java | Nivel de código fuente y destino: 23. Compilación comprobada con JDK 27. |
| Maven | Gestión de dependencias y compilación. |
| PostgreSQL | Base de datos relacional; conexión configurada para Neon. |
| PostgreSQL JDBC Driver | 42.7.1 |
| HikariCP | 6.3.0; pool de conexiones. |
| Lombok | 1.18.48 |
| SLF4J Simple | 1.7.36; salida de logs en consola. |
| Maven Compiler Plugin | 3.14.1; procesamiento de anotaciones de Lombok. |

## Estructura del proyecto

```text
java-jdbc-main/
├── pom.xml
├── README.md
├── database/
│   ├── README.md
│   └── schema.sql
└── src/main/java/com/maydan/proyecto/
    ├── Main.java
    ├── db/
    │   └── ConnectionPool.java
    ├── category/
    │   ├── model/Category.java
    │   └── persistence/CategoryDao.java
    └── product/
        ├── controller/
        ├── exception/
        ├── interfaces/
        ├── model/
        ├── persistence/
        ├── repository/
        ├── service/
        ├── utils/
        └── view/
```

### Responsabilidades

`Main` construye los componentes de la aplicación y abre el menú. `ProductView` lee los datos del usuario y presenta los resultados. `ProductController` recibe las solicitudes de la vista y realiza comprobaciones básicas.

`ProductService` coordina las operaciones y las transacciones. `ProductRepositoryServices` implementa el contrato del repositorio y mantiene una lista de productos en memoria. `ProductDAO` y `CategoryDao` ejecutan las consultas SQL. `ConnectionPool` proporciona las conexiones mediante HikariCP.

La lista se carga desde la base al crear el repositorio. El listado y la búsqueda habitual por ID utilizan esa lista; otras operaciones consultan directamente la base de datos. Los cambios realizados por otro cliente no se reflejan automáticamente en la lista en memoria.

## Modelo de datos

Una categoría puede estar asociada con varios productos. Cada producto pertenece a una categoría mediante `category_id`.

| Tabla | Campos |
| --- | --- |
| `categories` | `id`, `name` |
| `products` | `id`, `name`, `price`, `stock`, `category_id` |

El [script SQL](database/schema.sql) crea ambas tablas en el esquema `public`, con identificadores autogenerados y una clave foránea desde `products` hacia `categories`. También establece que el precio sea mayor que cero y que el stock no sea negativo.

## Requisitos

- JDK 27 para reproducir el entorno de compilación comprobado.
- IntelliJ IDEA u otro IDE con soporte para proyectos Maven.
- Maven disponible en la terminal si se desea compilar por comandos; también puede utilizarse el Maven integrado de IntelliJ.
- Una base PostgreSQL accesible, local o alojada en Neon, y credenciales con permisos sobre las tablas.
- Acceso a Internet para descargar las dependencias y, si corresponde, conectarse a Neon.

## Configuración y ejecución

### 1. Abrir el proyecto

Abre `pom.xml` como proyecto Maven en IntelliJ IDEA. Selecciona el JDK en **File → Project Structure → Project SDK** y recarga las dependencias desde el panel Maven.

### 2. Configurar PostgreSQL

La conexión se define en [ConnectionPool.java](src/main/java/com/maydan/proyecto/db/ConnectionPool.java), mediante `setJdbcUrl`, `setUsername` y `setPassword`. Ajusta esos valores a tu entorno. Por ejemplo, para una base local:

```java
config.setJdbcUrl("jdbc:postgresql://localhost:5432/productos_db");
config.setUsername("TU_USUARIO");
config.setPassword("TU_PASSWORD");
```

Para Neon, utiliza la URL JDBC de tu propia base y conserva los parámetros SSL requeridos por la conexión. El nombre de base de la URL debe coincidir con el que utilizas al ejecutar el script SQL.

Actualmente la configuración se encuentra directamente en el código; no se cargan variables de entorno ni un archivo `.env`. Los valores del ejemplo son marcadores, no credenciales de acceso.

### 3. Preparar las tablas

Ejecuta [database/schema.sql](database/schema.sql) en la base configurada, utilizando el editor SQL de Neon o un cliente PostgreSQL.

El script debe ejecutarse manualmente: la aplicación no crea las tablas al arrancar. Si ya hay datos en otro esquema o base, revisa primero las [instrucciones de base de datos](database/README.md). Una instalación nueva comienza sin productos.

### 4. Compilar

Desde la carpeta que contiene `pom.xml`:

```bash
mvn clean compile
```

Comprueba con `mvn -version` qué JDK utiliza Maven. En IntelliJ también puedes ejecutar **Build → Rebuild Project** después de recargar el proyecto Maven.

### 5. Iniciar

Ejecuta el método `main` de la clase `com.maydan.proyecto.Main` desde el IDE. La aplicación necesita conectarse a PostgreSQL antes de mostrar el menú.

```text
Seleccione una opción:
1. Agregar Producto
2. Mostrar Productos
3. Buscar Producto por ID
4. Eliminar Producto por ID
5. Modificar Producto por ID
6. Salir
```

Para probar el flujo, registra un producto, por ejemplo `Teclado`, con precio `450.50`, stock `10` y categoría `Accesorios`. Después lista los productos, consulta el ID generado y prueba la modificación. Utiliza punto para los decimales y opciones numéricas en el menú.

## Validaciones y manejo de errores

La vista solicita nombres de al menos tres caracteres y valida varias entradas numéricas. Al registrar un producto, `ProductValidator` exige un precio mayor que cero y stock no negativo. El controlador comprueba valores nulos mediante `Validates`.

Las excepciones personalizadas comunican problemas de datos o productos inexistentes. El servicio incluye manejo de transacciones para las operaciones de escritura, y los DAO utilizan parámetros JDBC para los valores de las consultas.

## Problemas frecuentes

| Mensaje o síntoma | Qué revisar |
| --- | --- |
| IntelliJ no encuentra los archivos del proyecto | Cerrar el proyecto anterior y abrir el `pom.xml` desde su ubicación actual. |
| Error de Lombok con `TypeTag` o `ExceptionInInitializerError` | Recargar Maven para utilizar la versión declarada y comprobar el JDK del proyecto y del compilador. |
| `relation "products" does not exist` | Verificar base, rama de Neon y esquema; preparar las tablas con `database/schema.sql` si faltan. |
| `Failed to load class "org.slf4j.impl.StaticLoggerBinder"` | Recargar Maven y comprobar que `slf4j-simple` esté en las dependencias de ejecución. |
| Fallo de conexión o autenticación | Revisar URL JDBC, usuario, contraseña, disponibilidad del servidor y parámetros SSL. |
| `La lista esta vacía` | Registrar el primer producto; este mensaje es esperado si aún no hay registros. |

## Alcance y posibles mejoras

Este proyecto tiene un propósito educativo y permite practicar la integración de los temas del curso. Algunas áreas para continuar desarrollándolo son:

- Externalizar la configuración de conexión y las credenciales.
- Incorporar pruebas unitarias y pruebas de integración con PostgreSQL.
- Unificar las consultas a la base y la lista en memoria, especialmente ante errores y cambios externos.
- Completar la búsqueda por categoría: existe código relacionado, pero no está conectado al menú y mezcla `Category` con el enum `ProductCategory`.
- Revisar la reutilización de categorías al registrar productos: actualmente la búsqueda utiliza el nombre del producto.
- Aplicar las mismas validaciones al crear y modificar, y manejar entradas no numéricas en todas las opciones del menú.
- Utilizar `BigDecimal` para representar importes monetarios en Java.
- Integrar el cierre explícito del pool al terminar la aplicación.

Estas mejoras forman parte de la evolución del ejercicio y no están implementadas en su totalidad.
