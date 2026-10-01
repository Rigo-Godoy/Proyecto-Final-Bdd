package Proyecto_Final_bdd;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionSQL {

    private static final String URL =
        "jdbc:mariadb://localhost:3306/aplicacion_medica";

    private static final String USUARIO = "root";
    private static final String PASSWORD = "";

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(
            URL,
            USUARIO,
            PASSWORD
        );
    }
}