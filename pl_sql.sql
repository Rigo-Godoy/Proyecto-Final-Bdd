/*PROCEDIMIENTOS ALMACENADOS EN MODO ORACLE*/

SET SQL_MODE = 'ORACLE';

USE aplicacion_medica;


/*PROCEDIMIENTO 1: VENTAS DIARIAS*/

DELIMITER //

CREATE OR REPLACE PROCEDURE VentasDiarias(
    fecha_busqueda DATE
)
AS
BEGIN

    /*DESGLOSE DE VENTAS DEL DIA*/

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


    /*TOTAL DE VENTAS DEL DIA*/

    SELECT
        fecha_busqueda AS Fecha,
        COALESCE(SUM(pf.Monto_pagar), 0) AS Total_Ventas
    FROM Pagos_fac pf
    WHERE DATE(pf.Fecha_pago) = fecha_busqueda;

END VentasDiarias;
//

DELIMITER ;


/*EJECUCION DE EJEMPLO*/

CALL VentasDiarias('2026-09-30');


/*PROCEDIMIENTO 2: CLIENTES VIGENTES*/

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


/*EJECUCION DE EJEMPLO*/

CALL ClientesVigentes(2026);


/*PROCEDIMIENTO 3: AGREGAR PACIENTE*/
/*MANEJO DE EXCEPCION POR CORREO DUPLICADO*/

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

    /*INSERTAR PACIENTE*/

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


    /*MENSAJE DE OPERACION EXITOSA*/

    SELECT
        'Paciente agregado correctamente.' AS Mensaje;


EXCEPTION

    /*EXCEPCION POR RESTRICCION UNIQUE*/

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


/*PRUEBA DEL PROCEDIMIENTO AGREGAR PACIENTE*/

/*
   ESTE CORREO YA EXISTE EN LA TABLA PACIENTES:

   juan.perez@gmail.com

   POR LO TANTO, ESTA PRUEBA DEBE ACTIVAR
   LA EXCEPCION DUP_VAL_ON_INDEX.
*/

CALL AgregarPaciente(
    'Rigoberto',
    'Godoy',
    'Flores',
    '2000-04-10',
    'Masculino',
    'Av. Reforma 123',
    '8145678901',
    'juan.perez@gmail.com',
    '8144444444',
    'A+',
    'Ninguna'
);


/*VERIFICACION DE PROCEDIMIENTOS*/

SHOW PROCEDURE STATUS
WHERE Db = 'aplicacion_medica';
