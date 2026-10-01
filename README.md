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
- `PacienteDAO`: contiene las consultas SQL del CRUD.
- `appHospital.sql`: crea la base de datos, sus tablas y registros de ejemplo.

## Requisitos previos

1. Tener instalado un JDK compatible con el proyecto.
2. Tener instalado y ejecutándose MariaDB.
3. Tener disponible el usuario de MariaDB que utilizará la aplicación.
4. Tener Gradle disponible o usar los scripts `gradlew.bat` incluidos.

## Preparación de la base de datos

El script [`appHospital.sql`](./appHospital.sql) crea la base de datos
`aplicacion_medica` y las tablas relacionadas. Para prepararla:

1. Iniciar el servidor de MariaDB.
2. Abrir el cliente de MariaDB o una herramienta como HeidiSQL, DBeaver o
   phpMyAdmin.
3. Ejecutar el contenido de `appHospital.sql`.
4. Confirmar que exista la base de datos `aplicacion_medica` y que la tabla
   `Pacientes` tenga los registros iniciales.

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
5. Salir
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
5. El DAO crea un `PreparedStatement`.
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
| Create | 1 | `crear(Paciente)` | `INSERT INTO Pacientes` |
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
`PacienteDAO.crear(...)`. El DAO ejecuta un `INSERT` con once parámetros y
muestra **“Paciente registrado correctamente.”** si la inserción termina con
éxito.

La base de datos valida además:

- Campos obligatorios (`NOT NULL`).
- Teléfono y contacto de emergencia de diez dígitos.
- Teléfonos y correos no repetidos (`UNIQUE`).
- Formato del correo.
- Género permitido.
- Tipo de sangre permitido.

Si se escribe `cancelar` en cualquier campo, la operación termina sin
realizar el `INSERT`.

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

