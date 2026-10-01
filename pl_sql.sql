/* PUNTO 2 - PROCEDIMIENTO: VENTAS DIARIAS */

/* =========================================================
   PROCEDIMIENTOS ALMACENADOS
   MariaDB con modo de compatibilidad Oracle
   ========================================================= */

SET SQL_MODE = 'ORACLE';

USE aplicacion_medica;


/* =========================================================
   1. VENTAS DIARIAS
   ========================================================= */

DELIMITER //

CREATE OR REPLACE PROCEDURE VentasDiarias(
    fecha_busqueda DATE
)
AS
BEGIN

    /* -----------------------------------------
       Desglose de ventas del día
       ----------------------------------------- */

    SELECT
        pf.Id_Pago,
        pf.Id_Cita,
        pf.Monto_pagar,
        pf.Desg_gastos,
        pf.Met_pago,
        pf.Est_pago,
        pf.Fecha_pago
    FROM Pagos_fac pf
    WHERE DATE(pf.Fecha_pago) = fecha_busqueda;


    /* -----------------------------------------
       Total de ventas del día
       ----------------------------------------- */

    SELECT
        fecha_busqueda AS Fecha,
        COALESCE(SUM(pf.Monto_pagar), 0) AS Total_Ventas
    FROM Pagos_fac pf
    WHERE DATE(pf.Fecha_pago) = fecha_busqueda;

END VentasDiarias;
//

DELIMITER ;


/* =========================================================
   EJECUCIÓN DE EJEMPLO
   ========================================================= */

CALL VentasDiarias('2026-09-30');


/* =========================================================
   2. CLIENTES VIGENTES
   ========================================================= */

DELIMITER //

CREATE OR REPLACE PROCEDURE ClientesVigentes(
    anio INT
)
AS
BEGIN

    SELECT DISTINCT
        p.Id_Pac,
        p.Nom_Pac,
        p.Ap_Pat_Pac,
        p.Ap_Mat_Pac,
        p.Tel_Pac,
        p.Correo_Pac
    FROM Pacientes p
    INNER JOIN Citas c
        ON p.Id_Pac = c.Id_Pac
    WHERE c.Fecha_cita >= CONCAT(anio, '-01-01')
      AND c.Fecha_cita < CONCAT(anio, '-04-01')
    ORDER BY
        p.Ap_Pat_Pac ASC,
        p.Nom_Pac ASC;

END ClientesVigentes;
//

DELIMITER ;


/* =========================================================
   EJECUCIÓN DE EJEMPLO
   ========================================================= */

CALL ClientesVigentes(2026);


/* =========================================================
   3. AGREGAR PACIENTE
      MANEJO DE EXCEPCIÓN POR CORREO DUPLICADO
   ========================================================= */

DELIMITER //

CREATE OR REPLACE PROCEDURE AgregarPaciente(
    p_nom          VARCHAR(100),
    p_ap_pat       VARCHAR(100),
    p_ap_mat       VARCHAR(100),
    p_fecha        DATE,
    p_genero       VARCHAR(30),
    p_direccion    VARCHAR(255),
    p_telefono     VARCHAR(30),
    p_correo       VARCHAR(150),
    p_emergencia   VARCHAR(30),
    p_sangre       VARCHAR(10),
    p_alergias     TEXT
)
AS
BEGIN

    /* -----------------------------------------
       Inserción del paciente
       ----------------------------------------- */

    INSERT INTO Pacientes (
        Nom_Pac,
        Ap_Pat_Pac,
        Ap_Mat_Pac,
        Fec_nacim,
        Genero,
        Dire_Pac,
        Tel_Pac,
        Correo_Pac,
        Cont_Emer_Tel,
        T_Sangre,
        Alergias
    )
    VALUES (
        p_nom,
        p_ap_pat,
        p_ap_mat,
        p_fecha,
        p_genero,
        p_direccion,
        p_telefono,
        p_correo,
        p_emergencia,
        p_sangre,
        p_alergias
    );


    /* -----------------------------------------
       Mensaje de operación exitosa
       ----------------------------------------- */

    SELECT
        'Paciente agregado correctamente.' AS Mensaje;


EXCEPTION

    /* -----------------------------------------
       Excepción por restricción UNIQUE
       ----------------------------------------- */

    WHEN DUP_VAL_ON_INDEX THEN

        SELECT
            CONCAT(
                'ERROR: El correo ',
                p_correo,
                ' ya existe.'
            ) AS Mensaje;

END AgregarPaciente;
//

DELIMITER ;


/* =========================================================
   PRUEBA DEL PROCEDIMIENTO AgregarPaciente
   ========================================================= */

/*
   Este correo ya existe en la tabla Pacientes:

   juan.perez@gmail.com

   Por lo tanto, esta prueba debe activar
   la excepción DUP_VAL_ON_INDEX.
*/

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


/* =========================================================
   VERIFICACIÓN DE PROCEDIMIENTOS
   ========================================================= */

SHOW PROCEDURE STATUS
WHERE Db = 'aplicacion_medica';
