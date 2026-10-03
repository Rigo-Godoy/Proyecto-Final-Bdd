/*BASE DE DATOS: APP MEDICA*/

CREATE DATABASE IF NOT EXISTS aplicacion_medica;
USE aplicacion_medica;

/*TABLA: PACIENTES*/

CREATE TABLE Pacientes (
    Id_Pac INT AUTO_INCREMENT PRIMARY KEY,
    Nom_Pac VARCHAR(50) NOT NULL,
    Ap_Pat_Pac VARCHAR(50) NOT NULL,
    Ap_Mat_Pac VARCHAR(50) NOT NULL,
    Fec_nacim DATE NOT NULL,
    Genero VARCHAR(10) NOT NULL,
    Dire_Pac VARCHAR(150),
    Tel_Pac VARCHAR(10) NOT NULL UNIQUE,
    Correo_Pac VARCHAR(100) NOT NULL UNIQUE,
    Cont_Emer_Tel VARCHAR(10) NOT NULL,
    T_Sangre VARCHAR(3),
    Alergias VARCHAR(255),

    CHECK (Genero IN ('Masculino', 'Femenino', 'Otro')),
    CHECK (Tel_Pac REGEXP '^[0-9]{10}$'),
    CHECK (Cont_Emer_Tel REGEXP '^[0-9]{10}$'),
    CHECK (Correo_Pac REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$'),
    CHECK (T_Sangre IN (
        'A+', 'A-', 'B+', 'B-',
        'AB+', 'AB-', 'O+', 'O-'
    ))
);

/*TABLA: MEDICOS*/

CREATE TABLE Medicos (
    Id_Doc INT AUTO_INCREMENT PRIMARY KEY,
    Nom_doc VARCHAR(50) NOT NULL,
    Ap_Pat_Doc VARCHAR(50) NOT NULL,
    Ap_Mat_Doc VARCHAR(50) NOT NULL,
    Tel_Doc VARCHAR(10) NOT NULL UNIQUE,
    Especializacion VARCHAR(100) NOT NULL,
    Cedula_prof VARCHAR(20) NOT NULL UNIQUE,
    Horario_inicio TIME NOT NULL,
    Horario_fin TIME NOT NULL,
    Turno_trab VARCHAR(20) NOT NULL,
    Cargo VARCHAR(50),

    CHECK (Tel_Doc REGEXP '^[0-9]{10}$'),
    CHECK (Turno_trab IN (
        'Matutino',
        'Vespertino',
        'Nocturno',
        'Mixto'
    )),
    CHECK (Horario_fin > Horario_inicio)
    );

/*TABLA: CITAS*/

CREATE TABLE Citas (
    Id_Cita INT AUTO_INCREMENT PRIMARY KEY,
    Id_Pac INT NOT NULL,
    Id_Doc INT NOT NULL,
    Fecha_cita DATE NOT NULL,
    H_cita TIME NOT NULL,
    Cons_Asig VARCHAR(20) NOT NULL,
    Estado_cita VARCHAR(20) NOT NULL DEFAULT 'Pendiente',
    Motivo_Cons VARCHAR(255) NOT NULL,

    FOREIGN KEY (Id_Pac) REFERENCES Pacientes(Id_Pac) ON DELETE CASCADE,
    FOREIGN KEY (Id_Doc) REFERENCES Medicos(Id_Doc),

    CHECK (Estado_cita IN (
        'Pendiente',
        'Confirmado',
        'En proceso',
        'Finalizada',
        'Cancelada'
    )),
    UNIQUE (Id_Doc, Fecha_cita, H_cita)
);

/*TABLA: PAGOS_FAC*/

CREATE TABLE Pagos_fac (
    Id_Pago INT AUTO_INCREMENT PRIMARY KEY,
    Id_Cita INT NOT NULL UNIQUE,
    Monto_pagar DECIMAL(10,2) NOT NULL,
    Desg_gastos VARCHAR(255),
    Met_pago VARCHAR(30) NOT NULL,
    Est_pago VARCHAR(20) NOT NULL,
    Fecha_pago DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (Id_Cita) REFERENCES Citas(Id_Cita) ON DELETE CASCADE,

    CHECK (Monto_pagar > 0),
    CHECK (Met_pago IN (
        'Tarjeta de credito',
        'Tarjeta de debito',
        'Transferencia',
        'Efectivo'
    )),
    CHECK (Est_pago IN (
        'Pendiente',
        'Aprobado',
        'Rechazado',
        'Reembolsado'
    ))
);

/*INSERTAR REGISTROS EN PACIENTES*/

INSERT INTO Pacientes
(
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
VALUES
(
    'Juan',
    'Perez',
    'Gomez',
    '1999-05-15',
    'Masculino',
    'Av. Universidad 123, Monterrey',
    '8112345678',
    'juan.perez@gmail.com',
    '8111111111',
    'O+',
    'Ninguna'
),
(
    'Maria',
    'Lopez',
    'Ramirez',
    '2001-08-20',
    'Femenino',
    'Calle Reforma 456, Monterrey',
    '8123456789',
    'maria.lopez@gmail.com',
    '8122222222',
    'A+',
    'Penicilina'
),
(
    'Carlos',
    'Hernandez',
    'Torres',
    '1995-12-10',
    'Masculino',
    'Calle Juarez 789, Monterrey',
    '8134567890',
    'carlos.hernandez@gmail.com',
    '8133333333',
    'B+',
    NULL
);

/*INSERTAR REGISTROS EN MEDICOS*/

INSERT INTO Medicos
(
    Nom_doc,
    Ap_Pat_Doc,
    Ap_Mat_Doc,
    Tel_Doc,
    Especializacion,
    Cedula_prof,
    Horario_inicio,
    Horario_fin,
    Turno_trab,
    Cargo
)
VALUES
(
    'Roberto',
    'Garcia',
    'Martinez',
    '8114567890',
    'Cardiologia',
    'CED123456',
    '08:00:00',
    '14:00:00',
    'Matutino',
    'Especialista'
),
(
    'Ana',
    'Martinez',
    'Lopez',
    '8125678901',
    'Dermatologia',
    'CED234567',
    '14:00:00',
    '20:00:00',
    'Vespertino',
    'Especialista'
),
(
    'Luis',
    'Ramirez',
    'Hernandez',
    '8136789012',
    'Pediatria',
    'CED345678',
    '18:00:00',
    '23:00:00',
    'Nocturno',
    'Especialista'
);

/*INSERTAR REGISTROS EN CITAS*/

INSERT INTO Citas
(
    Id_Pac,
    Id_Doc,
    Fecha_cita,
    H_cita,
    Cons_Asig,
    Estado_cita,
    Motivo_Cons
)
VALUES
(
    1,
    1,
    '2026-10-10',
    '09:00:00',
    'C101',
    'Confirmado',
    'Dolor en el pecho'
),
(
    1,
    1,
    '2026-02-15',
    '10:00:00',
    'C104',
    'Finalizada',
    'Consulta de seguimiento'
),
(
    2,
    2,
    '2026-10-11',
    '15:00:00',
    'C102',
    'Pendiente',
    'Revision de la piel'
),
(
    3,
    3,
    '2026-10-12',
    '19:00:00',
    'C103',
    'Pendiente',
    'Consulta general'
);

/*INSERTAR REGISTROS EN PAGOS_FAC*/

INSERT INTO Pagos_fac
(
    Id_Cita,
    Monto_pagar,
    Desg_gastos,
    Met_pago,
    Est_pago
)
VALUES
(
    1,
    850.00,
    'Consulta de cardiologia',
    'Tarjeta de credito',
    'Aprobado'
),
(
    2,
    700.00,
    'Consulta de dermatologia',
    'Transferencia',
    'Pendiente'
),
(
    3,
    600.00,
    'Consulta de pediatria',
    'Efectivo',
    'Aprobado'
);

/*CONSULTAS PARA VERIFICAR LOS REGISTROS*/

SELECT * FROM Pacientes;
SELECT * FROM Medicos;
SELECT * FROM Citas;
SELECT * FROM Pagos_fac;

/*PROYECTO FINAL*/

/*ORDER BY por fechas: Citas más recientes primero*/
/*mayor a menor*/
SELECT Id_Cita, Id_Pac, Id_Doc, Fecha_cita, H_cita, Motivo_Cons, Estado_cita
FROM Citas
ORDER BY Fecha_cita DESC, H_cita DESC;

/*De menor a mayor: Pacientes ordenados por fecha de nacimiento, del más viejo al mas joven*/
SELECT Id_Pac, Nom_Pac, Ap_Pat_Pac, Ap_Mat_Pac, Fec_nacim, Genero
FROM Pacientes
ORDER BY Fec_nacim ASC;

/*UNION: Agrupar datos de usuarios/contactos de dos tablas*/
SELECT 
    Nom_Pac AS Nombre, 
    Ap_Pat_Pac AS Apellido, 
    Tel_Pac AS Telefono, 
    'Paciente' AS Tipo_Persona
FROM Pacientes
UNION
SELECT 
    Nom_doc AS Nombre, 
    Ap_Pat_Doc AS Apellido, 
    Tel_Doc AS Telefono, 
    'Médico' AS Tipo_Persona
FROM Medicos;

/*JOIN: Conjugar información de dos o más tablas*/
SELECT 
    c.Id_Cita,
    c.Fecha_cita,
    c.H_cita,
    c.Motivo_Cons,
    c.Estado_cita,
    p.Nom_Pac,
    p.Ap_Pat_Pac,
    m.Nom_doc,
    m.Ap_Pat_Doc,
    m.Especializacion
FROM Citas c
INNER JOIN Pacientes p ON c.Id_Pac = p.Id_Pac
INNER JOIN Medicos m ON c.Id_Doc = m.Id_Doc
ORDER BY c.Fecha_cita ASC;

/* GROUP BY: Agrupar información por un tema en común */
SELECT 
    p.Id_Pac,
    p.Nom_Pac,
    p.Ap_Pat_Pac,
    COUNT(pf.Id_Pago) AS Total_Facturas,
    SUM(pf.Monto_pagar) AS Total_Pagado
FROM Pacientes p
INNER JOIN Citas c ON p.Id_Pac = c.Id_Pac
INNER JOIN Pagos_fac pf ON c.Id_Cita = pf.Id_Cita
WHERE pf.Est_pago = 'Aprobado'
GROUP BY p.Id_Pac, p.Nom_Pac, p.Ap_Pat_Pac
ORDER BY Total_Pagado DESC;

/*GROUP BY:Agrupar Citas por Médico y Especialidad*/
SELECT 
    m.Especializacion,
    m.Nom_doc,
    m.Ap_Pat_Doc,
    COUNT(c.Id_Cita) AS Total_Citas_Atendidas
FROM Medicos m
INNER JOIN Citas c ON m.Id_Doc = c.Id_Doc
GROUP BY m.Id_Doc, m.Especializacion, m.Nom_doc, m.Ap_Pat_Doc
HAVING COUNT(c.Id_Cita) > 0
ORDER BY Total_Citas_Atendidas DESC;


/*INSERTAR REGISTRO EN PAGOOS_FAC CON FECHA_PAGO */
INSERT INTO Pagos_fac (
    Id_Cita,
    Monto_pagar,
    Desg_gastos,
    Met_pago,
    Est_pago,
    Fecha_pago
)
VALUES (
    4,
    950.00,
    'Consulta general',
    'Efectivo',
    'Aprobado',
    '2026-10-02 10:30:00'
);