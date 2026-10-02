package Proyecto_Final_bdd;

import java.time.LocalDate;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        PacienteDAO dao = new PacienteDAO();

        int opcion;

        do {

            System.out.println();
            System.out.println("================================");
            System.out.println("           APP MEDICA");
            System.out.println("================================");
            System.out.println("1. Registrar paciente");
            System.out.println("2. Listar pacientes");
            System.out.println("3. Actualizar paciente");
            System.out.println("4. Eliminar paciente");
            System.out.println("5. Reporte de ventas diarias");
            System.out.println("6. Reporte de clientes vigentes");
            System.out.println("7. Salir");
            System.out.println("================================");
            System.out.println("Seleccione una opcion:");

            try {
                opcion = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Debe introducir un numero.");
                opcion = 0;
                continue;
            }

            switch (opcion) {

                case 1:
                    registrarPaciente(scanner, dao);
                    break;

                case 2:
                    dao.listar();
                    break;

                case 3:
                    actualizarPaciente(scanner, dao);
                    break;

                case 4:
                    eliminarPaciente(scanner, dao);
                    break;

                case 5:
                    reporteVentas(scanner, dao);
                    break;

                case 6:
                    reporteClientesVigentes(scanner, dao);
                    break;

                case 7:
                    System.out.println("Saliendo del programa...");
                    break;

                default:
                    System.out.println("Opcion no valida.");
            }

        } while (opcion != 7);

        scanner.close();
    }

    // =========================
    // COMPROBAR CANCELACION
    // =========================

    public static boolean cancelar(String entrada) {
        return entrada.trim().equalsIgnoreCase("cancelar");
    }

    // =========================
    // REGISTRAR PACIENTE
    // =========================

    public static void registrarPaciente(
            Scanner scanner,
            PacienteDAO dao) {

        System.out.println();
        System.out.println("----- REGISTRAR PACIENTE -----");
        System.out.println("Escriba 'cancelar' en cualquier momento para regresar al menu.");

        System.out.println("Nombre:");
        String nombre = scanner.nextLine();

        if (cancelar(nombre)) {
            System.out.println("Operacion cancelada.");
            return;
        }

        System.out.println("Apellido paterno:");
        String apellidoPaterno = scanner.nextLine();

        if (cancelar(apellidoPaterno)) {
            System.out.println("Operacion cancelada.");
            return;
        }

        System.out.println("Apellido materno:");
        String apellidoMaterno = scanner.nextLine();

        if (cancelar(apellidoMaterno)) {
            System.out.println("Operacion cancelada.");
            return;
        }

        System.out.println("Fecha de nacimiento (YYYY-MM-DD):");
        String fechaTexto = scanner.nextLine();

        if (cancelar(fechaTexto)) {
            System.out.println("Operacion cancelada.");
            return;
        }

        LocalDate fechaNacimiento;

        try {
            fechaNacimiento = LocalDate.parse(fechaTexto);
        } catch (Exception e) {
            System.out.println("Formato de fecha invalido.");
            System.out.println("Use el formato YYYY-MM-DD.");
            return;
        }

        System.out.println("Genero (Masculino, Femenino, Otro):");
        String genero = scanner.nextLine();

        if (cancelar(genero)) {
            System.out.println("Operacion cancelada.");
            return;
        }

        System.out.println("Direccion:");
        String direccion = scanner.nextLine();

        if (cancelar(direccion)) {
            System.out.println("Operacion cancelada.");
            return;
        }

        System.out.println("Telefono (10 digitos):");
        String telefono = scanner.nextLine();

        if (cancelar(telefono)) {
            System.out.println("Operacion cancelada.");
            return;
        }

        System.out.println("Correo:");
        String correo = scanner.nextLine();

        if (cancelar(correo)) {
            System.out.println("Operacion cancelada.");
            return;
        }

        System.out.println("Contacto de emergencia (10 digitos):");
        String contactoEmergencia = scanner.nextLine();

        if (cancelar(contactoEmergencia)) {
            System.out.println("Operacion cancelada.");
            return;
        }

        System.out.println("Tipo de sangre (A+, A-, B+, B-, AB+, AB-, O+, O-):");
        String tipoSangre = scanner.nextLine();

        if (cancelar(tipoSangre)) {
            System.out.println("Operacion cancelada.");
            return;
        }

        if (!tipoSangreValido(tipoSangre)) {
            System.out.println("Tipo de sangre invalido.");
            System.out.println(
                "Debe utilizar uno de estos formatos: " +
                "A+, A-, B+, B-, AB+, AB-, O+, O-"
            );
            return;
        }

        System.out.println("Alergias:");
        String alergias = scanner.nextLine();

        if (cancelar(alergias)) {
            System.out.println("Operacion cancelada.");
            return;
        }

        Paciente paciente = new Paciente(
            nombre,
            apellidoPaterno,
            apellidoMaterno,
            fechaNacimiento,
            genero,
            direccion,
            telefono,
            correo,
            contactoEmergencia,
            tipoSangre,
            alergias
        );

        dao.crear(paciente);
    }

    // =========================
    // VALIDAR TIPO DE SANGRE
    // =========================

    public static boolean tipoSangreValido(String tipoSangre) {

        return tipoSangre.equals("A+")
            || tipoSangre.equals("A-")
            || tipoSangre.equals("B+")
            || tipoSangre.equals("B-")
            || tipoSangre.equals("AB+")
            || tipoSangre.equals("AB-")
            || tipoSangre.equals("O+")
            || tipoSangre.equals("O-");
    }

    // =========================
    // ACTUALIZAR PACIENTE
    // =========================

    public static void actualizarPaciente(
            Scanner scanner,
            PacienteDAO dao) {

        System.out.println();
        System.out.println("----- ACTUALIZAR PACIENTE -----");
        System.out.println("Escriba 'cancelar' en cualquier momento para regresar al menu.");

        System.out.println("ID del paciente:");
        String idTexto = scanner.nextLine();

        if (cancelar(idTexto)) {
            System.out.println("Operacion cancelada.");
            return;
        }

        int id;

        try {
            id = Integer.parseInt(idTexto);
        } catch (NumberFormatException e) {
            System.out.println("El ID debe ser un numero.");
            return;
        }

        if (!dao.mostrarPorId(id)) {
            return;
        }

        System.out.println("Nuevo nombre:");
        String nombre = scanner.nextLine();

        if (cancelar(nombre)) {
            System.out.println("Operacion cancelada.");
            return;
        }

        System.out.println("Nuevo apellido paterno:");
        String apellidoPaterno = scanner.nextLine();

        if (cancelar(apellidoPaterno)) {
            System.out.println("Operacion cancelada.");
            return;
        }

        System.out.println("Nuevo apellido materno:");
        String apellidoMaterno = scanner.nextLine();

        if (cancelar(apellidoMaterno)) {
            System.out.println("Operacion cancelada.");
            return;
        }

        System.out.println("Nueva fecha de nacimiento (YYYY-MM-DD):");
        String fechaTexto = scanner.nextLine();

        if (cancelar(fechaTexto)) {
            System.out.println("Operacion cancelada.");
            return;
        }

        LocalDate fechaNacimiento;

        try {
            fechaNacimiento = LocalDate.parse(fechaTexto);
        } catch (Exception e) {
            System.out.println("Formato de fecha invalido.");
            System.out.println("Use el formato YYYY-MM-DD.");
            return;
        }

        System.out.println("Nuevo genero (Masculino, Femenino, Otro):");
        String genero = scanner.nextLine();

        if (cancelar(genero)) {
            System.out.println("Operacion cancelada.");
            return;
        }

        System.out.println("Nueva direccion:");
        String direccion = scanner.nextLine();

        if (cancelar(direccion)) {
            System.out.println("Operacion cancelada.");
            return;
        }

        System.out.println("Nuevo telefono (10 digitos):");
        String telefono = scanner.nextLine();

        if (cancelar(telefono)) {
            System.out.println("Operacion cancelada.");
            return;
        }

        System.out.println("Nuevo correo:");
        String correo = scanner.nextLine();

        if (cancelar(correo)) {
            System.out.println("Operacion cancelada.");
            return;
        }

        System.out.println("Nuevo contacto de emergencia (10 digitos):");
        String contactoEmergencia = scanner.nextLine();

        if (cancelar(contactoEmergencia)) {
            System.out.println("Operacion cancelada.");
            return;
        }

        System.out.println(
            "Nuevo tipo de sangre (A+, A-, B+, B-, AB+, AB-, O+, O-):"
        );

        String tipoSangre = scanner.nextLine();

        if (cancelar(tipoSangre)) {
            System.out.println("Operacion cancelada.");
            return;
        }

        if (!tipoSangreValido(tipoSangre)) {
            System.out.println("Tipo de sangre invalido.");
            System.out.println(
                "Debe utilizar uno de estos formatos: " +
                "A+, A-, B+, B-, AB+, AB-, O+, O-"
            );
            return;
        }

        System.out.println("Nuevas alergias:");
        String alergias = scanner.nextLine();

        if (cancelar(alergias)) {
            System.out.println("Operacion cancelada.");
            return;
        }

        Paciente paciente = new Paciente(
            nombre,
            apellidoPaterno,
            apellidoMaterno,
            fechaNacimiento,
            genero,
            direccion,
            telefono,
            correo,
            contactoEmergencia,
            tipoSangre,
            alergias
        );

        paciente.setIdPac(id);

        dao.actualizar(paciente);
    }

    // =========================
    // ELIMINAR PACIENTE
    // =========================

    public static void eliminarPaciente(
            Scanner scanner,
            PacienteDAO dao) {

        System.out.println();
        System.out.println("----- ELIMINAR PACIENTE -----");
        System.out.println("Escriba 'cancelar' para regresar al menu.");

        System.out.println("ID del paciente:");
        String idTexto = scanner.nextLine();

        if (cancelar(idTexto)) {
            System.out.println("Operacion cancelada.");
            return;
        }

        int id;

        try {
            id = Integer.parseInt(idTexto);
        } catch (NumberFormatException e) {
            System.out.println("El ID debe ser un numero.");
            return;
        }

        if (!dao.mostrarPorId(id)) {
            return;
        }

        System.out.println("¿Esta seguro de eliminar este paciente? (s/n/cancelar):");

        String confirmacion = scanner.nextLine();

        if (cancelar(confirmacion)) {
            System.out.println("Operacion cancelada.");
            return;
        }

        if (confirmacion.equalsIgnoreCase("s")) {
            dao.eliminar(id);
        } else {
            System.out.println("Operacion cancelada.");
        }
    }

    public static void reporteVentas(
            Scanner scanner,
            PacienteDAO dao) {
        System.out.println();
        System.out.println("----- VENTAS DIARIAS -----");
        System.out.println("Fecha (YYYY-MM-DD):");
        String fechaTexto = scanner.nextLine();

        if (cancelar(fechaTexto)) {
            System.out.println("Operacion cancelada.");
            return;
        }

        try {
            dao.ventasDiarias(LocalDate.parse(fechaTexto));
        } catch (Exception e) {
            System.out.println("Formato de fecha invalido. Use YYYY-MM-DD.");
        }
    }

    public static void reporteClientesVigentes(
            Scanner scanner,
            PacienteDAO dao) {
        System.out.println();
        System.out.println("----- CLIENTES VIGENTES -----");
        System.out.println("Anio:");
        String anioTexto = scanner.nextLine();

        if (cancelar(anioTexto)) {
            System.out.println("Operacion cancelada.");
            return;
        }

        try {
            dao.clientesVigentes(Integer.parseInt(anioTexto));
        } catch (NumberFormatException e) {
            System.out.println("El anio debe ser un numero.");
        }
    }
}