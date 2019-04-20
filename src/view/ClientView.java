package view;
import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

import dao.ClienteDAO;
import dao.jdbc.ClienteDAOImpl;
import entidades.Cliente;

public class ClientView {
	static void menuCliente(Connection conn, ClienteDAO clienteDAO) throws Exception {
		System.out.printf(
				"\n1 inserir cliente \t2 editar cliente \n3 deletar cliente \t4 listar clientes" + "\n0 voltar  ");
		int clienteInput = new Scanner(System.in).nextInt();
		System.out.println();

		switch (clienteInput) {
		case 1:
			System.out.printf(" Insira o nome do cliente: ");
			clienteDAO.insert(conn, new Cliente(new Scanner(System.in).nextLine()));
			System.out.println("");
			break;
		case 2:
			editarCliente(conn, clienteDAO);
			break;
		case 3:
			deletarCliente(conn, clienteDAO);
			break;
		case 4:
			listarClientes(conn);
			break;
		default:
			break;
		}
	}

	private static void editarCliente(Connection conn, ClienteDAO clienteDAO) {
		Scanner scanner = new Scanner(System.in);
		try {
			listarClientes(conn);

			System.out.printf("Insira o ID do cliente: ");
			Cliente cliente = clienteDAO.find(conn, scanner.nextInt());

			System.out.printf("Insira o nome do cliente: ");
			cliente.setNome(scanner.nextLine());

			clienteDAO.edit(conn, cliente);
		} catch (Exception e) {
			System.out.println("Ops, algo deu errado!");
		}
	}

	private static void deletarCliente(Connection conn, ClienteDAO clienteDAO) {
		try {
			listarClientes(conn);

			System.out.printf("Insira o ID do cliente: ");
			clienteDAO.delete(conn, new Scanner(System.in).nextInt());
		} catch (Exception e) {
			System.out.println("Cliente não existe ou possui alugueis em aberto, impossível deletar!");
		}
	}

	static void listarClientes(Connection conn) {
		System.out.println("\n------------ CLIENTES CADASTRADOS --------------");
		List<Cliente> clientes;
		try {
			clientes = (List<Cliente>) new ClienteDAOImpl().list(conn);
			for (Cliente cliente : clientes) {
				System.out.println(cliente.toString());
			}
			System.out.println("------------------------------------------------");
		} catch (Exception e) {
			System.out.println("Ops, algo deu errado!");
		}
	}

}
