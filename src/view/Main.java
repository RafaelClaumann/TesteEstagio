package view;
import dao.AluguelDAO;
import dao.ClienteDAO;
import dao.FilmeDAO;
import dao.jdbc.AluguelDAOImpl;
import dao.jdbc.ClienteDAOImpl;
import dao.jdbc.FilmeDAOImpl;
import entidades.Aluguel;
import entidades.Cliente;
import entidades.Filme;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		Connection conn = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://localhost/paripassu", "postgres", "111");
			conn.setAutoCommit(false);

			// Demonstrar o funcionamento aqui
			ClienteDAO clienteDAO = new ClienteDAOImpl();
			FilmeDAO filmeDAO = new FilmeDAOImpl();
			AluguelDAO aluguelDAO = new AluguelDAOImpl();

			menuPrincipal(conn, clienteDAO, aluguelDAO, filmeDAO);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	static void menuPrincipal(Connection conn, ClienteDAO clienteDAO, AluguelDAO aluguelDAO, FilmeDAO filmeDAO)
			throws Exception {
		System.out.printf("1 Aluguel \t2 Cliente \n3 Filme \t0 sair ");
		int menuInput = new Scanner(System.in).nextInt();

		switch (menuInput) {
		case (1):
			AluguelView.menuAluguel(conn, aluguelDAO);
			main(null);
			break;
		case (2):
			ClientView.menuCliente(conn, clienteDAO);
			main(null);
			break;
		case (3):
			FilmeView.menuFilme(conn, filmeDAO);
			main(null);
			break;
		default:
			break;
		}
	}

}