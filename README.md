# Proyecto Final BDD - App Médica

Aplicación de consola desarrollada en Java para administrar pacientes de una
base de datos médica. El programa utiliza JDBC y el controlador de MariaDB
para ejecutar operaciones CRUD sobre la tabla `Pacientes`.

Aunque el script SQL crea también las tablas `Medicos`, `Citas` y
`Pagos_fac`, el menú actual de Java trabaja directamente con pacientes. Las
relaciones con citas y pagos se consideran principalmente al eliminar un
paciente.

## Tecnologías

- Java.
- Gradle 9.7 mediante el Gradle Wrapper.
- MariaDB.
- JDBC con `org.mariadb.jdbc:mariadb-java-client:3.5.6`.
- Aplicación de consola con `Scanner`.

## Estructura principal

```text
Proyecto_Final_bdd/
├── appHospital.sql
├── pl_sql.sql
├── build.gradle
├── gradlew
├── gradlew.bat
└── src/main/java/Proyecto_Final_bdd/
    ├── ConexionSQL.java
    ├── Main.java
    ├── Paciente.java
    └── PacienteDAO.java
```

- `Main`: muestra el menú, recibe los datos del usuario y coordina las
  operaciones.
- `ConexionSQL`: centraliza los datos de conexión y abre conexiones JDBC.
- `Paciente`: modelo Java que representa un paciente.
- `PacienteDAO`: contiene las consultas SQL del CRUD y las llamadas JDBC a los
  procedimientos almacenados.
- `appHospital.sql`: crea la base de datos, sus tablas, registros de ejemplo y
  consultas SQL de demostración.
- `pl_sql.sql`: configura el modo Oracle de MariaDB y crea procedimientos
  almacenados para el alta de pacientes y los reportes disponibles desde el
  menú de Java.

## Requisitos previos

1. Tener instalado un JDK compatible con el proyecto.
2. Tener instalado y ejecutándose MariaDB.
3. Tener disponible el usuario de MariaDB que utilizará la aplicación.
4. Tener Gradle disponible o usar los scripts `gradlew.bat` incluidos.

## Preparación de la base de datos

El script [`appHospital.sql`](./appHospital.sql) crea la base de datos
`aplicacion_medica`, las tablas relacionadas y los registros iniciales. Para
prepararla:

1. Iniciar el servidor de MariaDB.
2. Abrir el cliente de MariaDB o una herramienta como HeidiSQL, DBeaver o
   phpMyAdmin.
3. Ejecutar el contenido de `appHospital.sql`.
4. Confirmar que exista la base de datos `aplicacion_medica` y que la tabla
   `Pacientes` tenga los registros iniciales.
5. Ejecutar [`pl_sql.sql`](./pl_sql.sql) después de crear la base de datos y
   sus tablas. Este paso es necesario para registrar pacientes desde Java y
   utilizar los reportes almacenados.

Los scripts tienen responsabilidades diferentes:

| Archivo | Responsabilidad | Cómo debe ejecutarse |
| --- | --- | --- |
| [`appHospital.sql`](./appHospital.sql) | Crea el esquema `aplicacion_medica`, las tablas, las claves foráneas, las restricciones, los registros iniciales y consultas SQL de demostración. | Primero, como script de instalación de la base de datos. |
| [`pl_sql.sql`](./pl_sql.sql) | Activa `SQL_MODE = 'ORACLE'` y crea procedimientos almacenados para consultar ventas, consultar clientes vigentes y agregar pacientes con manejo de duplicados. | Después de `appHospital.sql`, cuando las tablas ya existen. |
| Java/JDBC | Ejecuta el CRUD de `Pacientes` y llama a los procedimientos almacenados desde el menú de consola mediante `PacienteDAO`. | Después de preparar la base de datos, ejecutar `pl_sql.sql` y configurar la conexión. |

`pl_sql.sql` no reemplaza a `appHospital.sql`: no crea la base de datos ni las
tablas. El alta de pacientes y los reportes del menú Java utilizan los
procedimientos almacenados mediante `CallableStatement`; la actualización y
eliminación siguen usando SQL transaccional porque no existe un procedimiento
equivalente para esas operaciones.

## Procedimientos almacenados de `pl_sql.sql`

El archivo [`pl_sql.sql`](./pl_sql.sql) contiene una capa de consultas y
operaciones almacenadas en MariaDB. Su sintaxis se aproxima a PL/SQL mediante
el modo de compatibilidad Oracle:

```sql
SET SQL_MODE = 'ORACLE';
USE aplicacion_medica;
```

El archivo utiliza `DELIMITER //` mientras define cada procedimiento, porque
el cuerpo de un procedimiento contiene varias instrucciones terminadas con
`;`. Al finalizar cada definición restaura `DELIMITER ;`.

### 1. `VentasDiarias`

```sql
CALL VentasDiarias('2026-09-30');
```

Devuelve dos resultados:

1. El desglose de pagos registrados en `Pagos_fac` para la fecha indicada.
2. El total vendido ese día mediante `SUM(Monto_pagar)`.

### 2. `ClientesVigentes`

```sql
CALL ClientesVigentes(2026);
```

Lista pacientes que tienen una cita durante el primer trimestre del año
recibido. Relaciona `Pacientes` con `Citas` y ordena los resultados por
apellido paterno y nombre. La condición usa el inicio de enero y el inicio de
abril para incluir correctamente todo el primer trimestre, incluso si la fecha
de la cita incluye hora.

### 3. `AgregarPaciente`

```sql
CALL AgregarPaciente(
    'Rigoberto',
    'Godoy',
    'Flores',
    '2000-04-10',
    'Masculino',
    'Av. Reforma 123',
    '8145678901',
    'rigoberpro426@gmail.com',
    '8144444444',
    'A+',
    'Ninguna'
);
```

Inserta un paciente en `Pacientes`. Si la inserción es correcta, devuelve un
mensaje de confirmación. Si se viola una restricción única, maneja
`DUP_VAL_ON_INDEX` y devuelve un mensaje indicando que el correo ya existe.
Los tipos de los parámetros se declaran explícitamente para mantener
compatibilidad con las versiones de MariaDB que soportan el modo Oracle.

### Orden recomendado de ejecución

1. Ejecutar [`appHospital.sql`](./appHospital.sql).
2. Verificar que la base de datos seleccionada sea `aplicacion_medica`.
3. Ejecutar [`pl_sql.sql`](./pl_sql.sql) como script completo, incluyendo sus
   instrucciones `DELIMITER`.
4. Revisar que los procedimientos existan:

```sql
SHOW PROCEDURE STATUS
WHERE Db = 'aplicacion_medica';
```

5. Ejecutar los `CALL` de prueba incluidos en `pl_sql.sql` o invocar cada
   procedimiento por separado.
6. Ejecutar la aplicación Java con `gradlew.bat run`.

`DELIMITER` es una directiva del cliente de MariaDB, no una instrucción
almacenada en el servidor. Por ello, algunos editores requieren utilizar
**Ejecutar script** en lugar de **Ejecutar selección**. Si el cliente no
reconoce `DELIMITER`, se debe ejecutar cada definición desde el monitor de
MariaDB o configurar el editor para procesar scripts con delimitadores.

El modo Oracle de MariaDB proporciona compatibilidad parcial con PL/SQL; no
convierte MariaDB en un servidor Oracle. La sintaxis y las funciones
compatibles pueden variar según la versión instalada. La aplicación Java usa
JDBC de MariaDB: `CallableStatement` para `AgregarPaciente`, `VentasDiarias`
y `ClientesVigentes`, y `PreparedStatement` para listar, consultar, actualizar
y eliminar pacientes. Por esta razón, `pl_sql.sql` debe ejecutarse antes de
usar el alta y los reportes desde el programa.

El script define estas relaciones:

```text
Pacientes 1 ─── N Citas 1 ─── 1 Pagos_fac
Medicos   1 ─── N Citas
```

`Citas.Id_Pac` referencia a `Pacientes.Id_Pac` y
`Pagos_fac.Id_Cita` referencia a `Citas.Id_Cita`. Por ello el borrado desde
Java debe tomar en cuenta los registros dependientes.

## Configuración de la conexión

La conexión se encuentra en
[`ConexionSQL.java`](./src/main/java/Proyecto_Final_bdd/ConexionSQL.java).
Actualmente utiliza:

```java
jdbc:mariadb://localhost:3306/aplicacion_medica
```

Los valores configurados son:

| Parámetro | Valor actual | Descripción |
| --- | --- | --- |
| Motor | MariaDB | Servidor utilizado por el controlador JDBC |
| Host | `localhost` | La base de datos se ejecuta en el equipo local |
| Puerto | `3306` | Puerto estándar de MariaDB |
| Base de datos | `aplicacion_medica` | Esquema creado por `appHospital.sql` |
| Usuario | `root` | Usuario utilizado por la aplicación |
| Contraseña | vacía | Debe coincidir con la configuración local |

Si el usuario, contraseña, host o puerto son diferentes, modificar las
constantes `URL`, `USUARIO` y `PASSWORD` antes de ejecutar el programa. En un
entorno real no se recomienda guardar contraseñas directamente en el código;
se deberían obtener mediante variables de entorno o un archivo de
configuración fuera del control de versiones.

### Cómo se abre una conexión

El método `ConexionSQL.conectar()` llama a
`DriverManager.getConnection(URL, USUARIO, PASSWORD)` y devuelve un objeto
`Connection`. Cada operación del DAO solicita su propia conexión mediante
este método.

El proyecto declara el controlador MariaDB en
[`build.gradle`](./build.gradle):

```groovy
implementation 'org.mariadb.jdbc:mariadb-java-client:3.5.6'
```

Gradle descarga esta dependencia cuando se compila o ejecuta el proyecto.
Las clases del DAO usan `try-with-resources`, por lo que las conexiones,
sentencias preparadas y resultados se cierran automáticamente al terminar cada
operación, incluso cuando ocurre una excepción SQL.

## Ejecución

Desde la raíz del proyecto, en Windows:

```powershell
.\gradlew.bat build
.\gradlew.bat run
```

También puede ejecutarse el JAR generado después de construirlo, aunque la
forma recomendada durante el desarrollo es `run`, porque
[`build.gradle`](./build.gradle) conecta la entrada estándar al programa:

```groovy
run {
    standardInput = System.in
}
```

Al iniciar aparece el menú:

```text
1. Registrar paciente
2. Listar pacientes
3. Actualizar paciente
4. Eliminar paciente
5. Reporte de ventas diarias
6. Reporte de clientes vigentes
7. Salir
```

Si se introduce un valor que no es un número, el programa muestra un mensaje
de validación y vuelve a mostrar el menú.

# Conexión

## Flujo de conexión durante una operación

1. `Main` crea una instancia de `PacienteDAO`.
2. El usuario selecciona una opción del menú.
3. El DAO llama a `ConexionSQL.conectar()`.
4. `DriverManager` intenta conectarse a MariaDB usando la URL y credenciales
   configuradas.
5. El DAO crea un `PreparedStatement` o un `CallableStatement`, según la
   operación.
6. Los valores del modelo `Paciente` o del ID recibido se asignan a los
   parámetros `?`.
7. Se ejecuta la consulta.
8. La conexión y los recursos JDBC se cierran automáticamente.
9. Si MariaDB devuelve un error, el DAO muestra el mensaje de operación y la
   excepción.

El uso de `PreparedStatement` evita concatenar directamente los valores
introducidos por el usuario en las consultas SQL y permite reutilizar la
estructura de cada sentencia.

## Errores habituales de conexión

- **La base de datos no existe:** ejecutar `appHospital.sql`.
- **No se puede conectar a `localhost:3306`:** verificar que el servicio de
  MariaDB esté iniciado y que el puerto coincida con `URL`.
- **Access denied:** revisar `USUARIO`, `PASSWORD` y los permisos del usuario
  en MariaDB.
- **No se encuentra el controlador:** ejecutar el proyecto con Gradle para que
  se descargue la dependencia declarada en `build.gradle`.
- **Error por restricciones de la tabla:** revisar los mensajes de MariaDB,
  ya que existen campos obligatorios, campos únicos y restricciones `CHECK`.

# CRUD

El CRUD se implementa en
[`PacienteDAO.java`](./src/main/java/Proyecto_Final_bdd/PacienteDAO.java) y
corresponde a:

| Operación | Menú | Método DAO | SQL |
| --- | ---: | --- | --- |
| Create | 1 | `crear(Paciente)` | `CALL AgregarPaciente(...)` |
| Read | 2 | `listar()` | `SELECT ... FROM Pacientes` |
| Read por ID | 3 y 4 | `mostrarPorId(int)` | `SELECT ... WHERE Id_Pac = ?` |
| Update | 3 | `actualizar(Paciente)` | `UPDATE Pacientes ... WHERE Id_Pac = ?` |
| Delete | 4 | `eliminar(int)` | `DELETE` en pagos, citas y paciente |

## Create: registrar paciente

La opción **1. Registrar paciente** se procesa en
`Main.registrarPaciente(...)`.

El programa solicita:

1. Nombre.
2. Apellido paterno.
3. Apellido materno.
4. Fecha de nacimiento.
5. Género.
6. Dirección.
7. Teléfono.
8. Correo.
9. Contacto de emergencia.
10. Tipo de sangre.
11. Alergias.

La fecha debe escribirse con el formato `YYYY-MM-DD`; se convierte de texto a
`LocalDate` mediante `LocalDate.parse`. El tipo de sangre se valida contra
`A+`, `A-`, `B+`, `B-`, `AB+`, `AB-`, `O+` y `O-`.

Después se construye un objeto `Paciente` y se envía a
`PacienteDAO.crear(...)`. El DAO llama a `AgregarPaciente` mediante
`CallableStatement` y muestra el mensaje devuelto por el procedimiento. Si el
correo ya existe, el procedimiento devuelve el mensaje de error controlado
por `DUP_VAL_ON_INDEX`.

La base de datos valida además:

- Campos obligatorios (`NOT NULL`).
- Teléfono y contacto de emergencia de diez dígitos.
- Teléfonos y correos no repetidos (`UNIQUE`).
- Formato del correo.
- Género permitido.
- Tipo de sangre permitido.

Si se escribe `cancelar` en cualquier campo, la operación termina sin llamar
al procedimiento.

## Reportes almacenados

Las opciones **5. Reporte de ventas diarias** y **6. Reporte de clientes
vigentes** llaman, respectivamente, a `PacienteDAO.ventasDiarias(...)` y
`PacienteDAO.clientesVigentes(...)`. Ambos métodos ejecutan los procedimientos
de [`pl_sql.sql`](./pl_sql.sql) mediante `CallableStatement` y muestran sus
resultados en consola. Para que estas opciones y el alta funcionen, se debe
ejecutar `pl_sql.sql` después de `appHospital.sql`.

## Read: listar pacientes

La opción **2. Listar pacientes** llama a `PacienteDAO.listar()`.

El método ejecuta un `SELECT` sobre todos los campos de `Pacientes`, recorre
el `ResultSet` con `while (rs.next())` y muestra cada registro en consola,
incluyendo ID, datos personales, contacto, tipo de sangre y alergias.

No se aplica un `ORDER BY`, por lo que el orden mostrado es el que entregue
MariaDB para esa consulta y no debe considerarse un orden fijo.

## Read por ID: seleccionar un paciente

Antes de actualizar o eliminar, el programa solicita el ID del paciente y
llama a `PacienteDAO.mostrarPorId(id)`.

- Si el ID existe, se imprimen los datos del paciente y el método devuelve
  `true`.
- Si no existe, se muestra **“No existe un paciente con ese ID.”** y devuelve
  `false`.
- Si ocurre un error de conexión o SQL, se informa el error y devuelve
  `false`.

Esta consulta previa evita continuar con la actualización o el borrado cuando
el paciente no existe.

## Update: actualizar paciente

La opción **3. Actualizar paciente** sigue este flujo:

1. Solicita el ID.
2. Comprueba que el ID sea un número entero.
3. Consulta y muestra el paciente con `mostrarPorId`.
4. Solicita nuevamente los once datos del paciente.
5. Valida la fecha y el tipo de sangre.
6. Crea un nuevo objeto `Paciente`.
7. Asigna el ID original con `paciente.setIdPac(id)`.
8. Ejecuta `PacienteDAO.actualizar(...)`.

El `UPDATE` modifica todos los datos excepto `Id_Pac`, que se utiliza en la
cláusula `WHERE` para identificar el registro. Si `executeUpdate()` devuelve
una cantidad de filas mayor que cero, se muestra **“Paciente actualizado
correctamente.”**; de lo contrario, se informa que no existe el ID.

Es una actualización completa: no conserva automáticamente los valores
anteriores. Por tanto, el usuario debe capturar todos los campos otra vez.
Escribir `cancelar` en cualquier punto detiene la operación antes de ejecutar
el `UPDATE`.

## Delete: eliminar paciente

La opción **4. Eliminar paciente** solicita el ID, muestra primero el registro
y pide confirmación (`s/n/cancelar`). Solo una respuesta `s`, sin importar
mayúsculas o minúsculas, continúa con el borrado.

El método `PacienteDAO.eliminar(id)` usa una transacción manual:

1. Desactiva el autocommit con `conexion.setAutoCommit(false)`.
2. Elimina los registros de `Pagos_fac` relacionados con citas del paciente.
3. Elimina las citas del paciente de `Citas`.
4. Elimina el paciente de `Pacientes`.
5. Ejecuta `commit()` si se eliminó una fila.
6. Ejecuta `rollback()` si no existe el paciente o si ocurre una excepción.

Este orden evita conflictos con las claves foráneas. Aunque el script define
`ON DELETE CASCADE` para algunas relaciones, el DAO elimina explícitamente
pagos y citas para controlar la operación y mantener todos los pasos dentro
de una sola transacción. Así se evita dejar datos relacionados a medias si
alguna consulta falla.

## Cancelación y validaciones del menú

- `cancelar(...)` reconoce la palabra `cancelar` sin importar mayúsculas,
  espacios iniciales o espacios finales.
- Un ID no numérico no se procesa.
- Una fecha inválida detiene el registro o actualización.
- Un tipo de sangre fuera del catálogo permitido detiene el registro o
  actualización.
- La confirmación de borrado requiere `s`; cualquier otra respuesta se
  interpreta como cancelación.

## Consideraciones actuales

- El CRUD implementado desde Java es únicamente el de `Pacientes`.
- `Medicos`, `Citas` y `Pagos_fac` se crean y se pueblan desde SQL, pero no
  tienen opciones propias en el menú.
- La aplicación imprime `printStackTrace()` cuando ocurre un error SQL; esto
  es útil durante el desarrollo, pero conviene reemplazarlo por un registro
  controlado en una versión de producción.
- La contraseña vacía del usuario `root` es una configuración local de
  desarrollo y debe cambiarse en instalaciones que requieran autenticación.
