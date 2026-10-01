package Proyecto_Final_bdd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PacienteDAO {

    // CREATE
    public void crear(Paciente paciente) {
        String sql = """
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
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

    try (
        Connection conexion = ConexionSQL.conectar();
        PreparedStatement ps = conexion.prepareStatement(sql)
    ) {

        ps.setString(1, paciente.getNomPac());
        ps.setString(2, paciente.getApPatPac());
        ps.setString(3, paciente.getApMatPac());
        ps.setDate(4, java.sql.Date.valueOf(paciente.getFecNacim()));
        ps.setString(5, paciente.getGenero());
        ps.setString(6, paciente.getDirePac());
        ps.setString(7, paciente.getTelPac());
        ps.setString(8, paciente.getCorreoPac());
        ps.setString(9, paciente.getContEmerTel());
        ps.setString(10, paciente.getTSangre());
        ps.setString(11, paciente.getAlergias());

        ps.executeUpdate();

        System.out.println("Paciente registrado correctamente.");

    } catch (SQLException e) {

        System.out.println("Error al registrar paciente:");
        e.printStackTrace();
    }
    }

    // READ
    public void listar() {
        String sql = """
        SELECT
            Id_Pac,
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
        FROM Pacientes
        """;

    try (
        Connection conexion = ConexionSQL.conectar();
        PreparedStatement ps = conexion.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()
    ) {

        while (rs.next()) {

            System.out.println("------------------------------");

            System.out.println("ID: " + rs.getInt("Id_Pac"));
            System.out.println("Nombre: " + rs.getString("Nom_Pac"));
            System.out.println("Apellido paterno: " + rs.getString("Ap_Pat_Pac"));
            System.out.println("Apellido materno: " + rs.getString("Ap_Mat_Pac"));
            System.out.println("Fecha de nacimiento: " + rs.getDate("Fec_nacim"));
            System.out.println("Genero: " + rs.getString("Genero"));
            System.out.println("Direccion: " + rs.getString("Dire_Pac"));
            System.out.println("Telefono: " + rs.getString("Tel_Pac"));
            System.out.println("Correo: " + rs.getString("Correo_Pac"));
            System.out.println("Contacto emergencia: " + rs.getString("Cont_Emer_Tel"));
            System.out.println("Tipo de sangre: " + rs.getString("T_Sangre"));
            System.out.println("Alergias: " + rs.getString("Alergias"));
        }

    } catch (SQLException e) {

        System.out.println("Error al listar pacientes:");
        e.printStackTrace();
    }
    }

    public boolean mostrarPorId(int id) {
        String sql = """
        SELECT
            Id_Pac,
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
        FROM Pacientes
        WHERE Id_Pac = ?
        """;

        try (
            Connection conexion = ConexionSQL.conectar();
            PreparedStatement ps = conexion.prepareStatement(sql)
        ) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("------------------------------");
                    System.out.println("Paciente seleccionado:");
                    System.out.println("ID: " + rs.getInt("Id_Pac"));
                    System.out.println("Nombre: " + rs.getString("Nom_Pac"));
                    System.out.println("Apellido paterno: " + rs.getString("Ap_Pat_Pac"));
                    System.out.println("Apellido materno: " + rs.getString("Ap_Mat_Pac"));
                    System.out.println("Fecha de nacimiento: " + rs.getDate("Fec_nacim"));
                    System.out.println("Genero: " + rs.getString("Genero"));
                    System.out.println("Direccion: " + rs.getString("Dire_Pac"));
                    System.out.println("Telefono: " + rs.getString("Tel_Pac"));
                    System.out.println("Correo: " + rs.getString("Correo_Pac"));
                    System.out.println("Contacto emergencia: " + rs.getString("Cont_Emer_Tel"));
                    System.out.println("Tipo de sangre: " + rs.getString("T_Sangre"));
                    System.out.println("Alergias: " + rs.getString("Alergias"));
                    System.out.println("------------------------------");
                    return true;
                }
            }

            System.out.println("No existe un paciente con ese ID.");
        } catch (SQLException e) {
            System.out.println("Error al consultar paciente:");
            e.printStackTrace();
        }

        return false;
    }

    // UPDATE
    public void actualizar(Paciente paciente) {
        String sql = """
        UPDATE Pacientes
        SET
            Nom_Pac = ?,
            Ap_Pat_Pac = ?,
            Ap_Mat_Pac = ?,
            Fec_nacim = ?,
            Genero = ?,
            Dire_Pac = ?,
            Tel_Pac = ?,
            Correo_Pac = ?,
            Cont_Emer_Tel = ?,
            T_Sangre = ?,
            Alergias = ?
        WHERE Id_Pac = ?
        """;

    try (
        Connection conexion = ConexionSQL.conectar();
        PreparedStatement ps = conexion.prepareStatement(sql)
    ) {

        ps.setString(1, paciente.getNomPac());
        ps.setString(2, paciente.getApPatPac());
        ps.setString(3, paciente.getApMatPac());
        ps.setDate(4, java.sql.Date.valueOf(paciente.getFecNacim()));
        ps.setString(5, paciente.getGenero());
        ps.setString(6, paciente.getDirePac());
        ps.setString(7, paciente.getTelPac());
        ps.setString(8, paciente.getCorreoPac());
        ps.setString(9, paciente.getContEmerTel());
        ps.setString(10, paciente.getTSangre());
        ps.setString(11, paciente.getAlergias());

        // El ID determina qué paciente vamos a modificar
        ps.setInt(12, paciente.getIdPac());

        int filasActualizadas = ps.executeUpdate();

        if (filasActualizadas > 0) {
            System.out.println("Paciente actualizado correctamente.");
        } else {
            System.out.println("No existe un paciente con ese ID.");
        }

    } catch (SQLException e) {

        System.out.println("Error al actualizar paciente:");
        e.printStackTrace();
    }
    }

    // DELETE
    public void eliminar(int id) {
        String sqlEliminarPagos = """
        DELETE pf
        FROM Pagos_fac pf
        INNER JOIN Citas c ON c.Id_Cita = pf.Id_Cita
        WHERE c.Id_Pac = ?
        """;

        String sqlEliminarCitas = """
        DELETE FROM Citas
        WHERE Id_Pac = ?
        """;

        String sqlEliminarPaciente = """
        DELETE FROM Pacientes
        WHERE Id_Pac = ?
        """;

    try (
        Connection conexion = ConexionSQL.conectar();
        PreparedStatement psPagos = conexion.prepareStatement(sqlEliminarPagos);
        PreparedStatement psCitas = conexion.prepareStatement(sqlEliminarCitas);
        PreparedStatement psPaciente = conexion.prepareStatement(sqlEliminarPaciente)
    ) {
        try {
            conexion.setAutoCommit(false);

            psPagos.setInt(1, id);
            psPagos.executeUpdate();

            psCitas.setInt(1, id);
            psCitas.executeUpdate();

            psPaciente.setInt(1, id);
            int filasEliminadas = psPaciente.executeUpdate();

            if (filasEliminadas > 0) {
                conexion.commit();
                System.out.println("Paciente eliminado correctamente.");
            } else {
                conexion.rollback();
                System.out.println("No existe un paciente con ese ID.");
            }
        } catch (SQLException e) {
            conexion.rollback();
            throw e;
        }

    } catch (SQLException e) {

        System.out.println("Error al eliminar paciente:");
        e.printStackTrace();
    }
    }
}
