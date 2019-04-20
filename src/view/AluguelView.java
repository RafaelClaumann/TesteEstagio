package view;
import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import dao.AluguelDAO;
import dao.jdbc.AluguelDAOImpl;
import dao.jdbc.ClienteDAOImpl;
import dao.jdbc.FilmeDAOImpl;
import entidades.Aluguel;
import entidades.Cliente;
import entidades.Filme;

public class AluguelView {
	static void menuAluguel(Connection conn, AluguelDAO aluguelDAO) throws Exception {
		System.out.printf("\n1 inserir aluguel \t2 editar aluguel \n3 deletar aluguel \t4 listar alugueis"
				+ " \n5 buscar aluguel \t0 voltar  ");
		int aluguelInput = new Scanner(System.in).nextInt();
		System.out.println();

		switch (aluguelInput) {
		case 1:
			inserirAluguel(conn, aluguelDAO);
			break;
		case 2:
			editarAluguel(conn, aluguelDAO);
			break;
		case 3:
			deletarAluguel(conn, aluguelDAO);
			break;
		case 4:
			listarAlugueis(conn);
			break;
		case 5:
			buscarAluguel(conn);
			break;
		default:
			break;
		}
	}

	static void inserirAluguel(Connection conn, AluguelDAO aluguelDAO) {
		Scanner scanner = new Scanner(System.in);

		try {
			ClientView.listarClientes(conn);

			System.out.printf("Insira o ID do cliente: "); // cliente responsavel pelo aluguel
			int scannerInt = scanner.nextInt();
			Cliente cliente = new ClienteDAOImpl().find(conn, scannerInt);

			FilmeView.listarFilmes(conn);
			System.out.printf("Insira o ID do filme:  "); // filme a ser alugado
			scannerInt = scanner.nextInt();

			List<Filme> filmes = new ArrayList<Filme>(); // filmes pertencentes ao aluguel
			filmes.add(new FilmeDAOImpl().find(conn, scannerInt));

			System.out.printf("Insira o valor do aluguel:  "); // valorAluguel
			float valor = scanner.nextFloat();

			Date data = new Date(System.currentTimeMillis()); // data

			aluguelDAO.insert(conn, new Aluguel(0, filmes, cliente, data, valor)); // inserção
		} catch (Exception e) {
			System.out.println("Ops, algo deu errado!");
		}
	}

	static void editarAluguel(Connection conn, AluguelDAO aluguelDAO) {
		Scanner scanner = new Scanner(System.in);
		try {
			listarAlugueis(conn);

			System.out.printf("insira o ID do aluguel: ");
			Aluguel aluguel = aluguelDAO.find(conn, scanner.nextInt());

			aluguel.setDataAluguel(new Date(System.currentTimeMillis())); // data atual

			ArrayList<Filme> filmes = (ArrayList<Filme>) aluguel.getFilmes(); // filmes do aluguel

			FilmeView.listarFilmes(conn);

			if (!filmes.isEmpty()) {
				System.out.println("\n-------- FILMES REGISTRADOS NESTE ALUGUEL --------");
				for (Filme filme : filmes) {
					System.out.println(filme.toString());
				}
				System.out.println("----------------------------------------------\n");
			}

			System.out.printf("Deseja Inserir/remover um filme? (1)inserir (2)remover (3)NA");
			int scannerInt = scanner.nextInt();

			if (scannerInt == 1) {
				System.out.printf("insira o ID do filme a ser alugado: ");
				filmes.add(new FilmeDAOImpl().find(conn, scanner.nextInt())); // adiciona filme ao aluguel
				aluguel.setFilmes((List<Filme>) filmes); // atualiza a lista de filmes do aluguel

				System.out.printf("Insira o valor do aluguel:  "); // valorAluguel
				float valor = scanner.nextFloat();

				aluguel.setValor(aluguel.getValor() + valor);

			} else if (scannerInt == 2) {
				System.out.printf("insira o ID do filme a ser removido: ");
				String daoImpl = new FilmeDAOImpl().find(conn, scanner.nextInt()).getNome();
				for (int i = 0; i < filmes.size(); i++) {
					if (filmes.get(i).getNome().equalsIgnoreCase(daoImpl)) {
						filmes.remove(i);
					}
				}
				filmes.trimToSize();
			} else {
				System.out.println("operação cancelada!");
			}

			aluguelDAO.edit(conn, aluguel);
		} catch (Exception e) {
			System.out.println("Ops, algo deu errado!");
		}
	}

	static void deletarAluguel(Connection conn, AluguelDAO aluguelDAO) {
		try {
			listarAlugueis(conn);

			System.out.println("Insira o ID do aluguel a ser removido: ");
			int idAluguel = new Scanner(System.in).nextInt();

			aluguelDAO.delete(conn, aluguelDAO.find(conn, idAluguel));
		} catch (Exception e) {
			System.out.println("Ops, algo deu errado!");
		}
	}

	static void buscarAluguel(Connection conn) {
		System.out.println("Insira o ID do aluguel: ");
		int idAluguel = new Scanner(System.in).nextInt();

		Aluguel aluguel;
		try {
			aluguel = new AluguelDAOImpl().find(conn, idAluguel);
			System.out.println(aluguel.toString());

			List<Filme> filmes = aluguel.getFilmes();
			if (!filmes.isEmpty()) {
				for (Filme filme : filmes) {
					System.out.println("\t" + filme.toString());
				}
			}
		} catch (Exception e) {
			System.out.println("Ops, algo deu errado!");
		}
	}

	static void listarAlugueis(Connection conn) {
		System.out.println("\n------------ ALUGUEIS CADASTRADOS --------------");
		Collection<Aluguel> alugueis;
		try {
			alugueis = new AluguelDAOImpl().list(conn);
			for (Aluguel aluguel : alugueis) {
				System.out.println(aluguel.toString());
				List<Filme> filmes = aluguel.getFilmes();
				for (Filme filme : filmes) {
					System.out.println("\tidFilme: " + filme.getIdFilme() + ", " + filme.getNome());
				}
				System.out.println("");
			}
			System.out.println("------------------------------------------------\n");
		} catch (Exception e) {
			System.out.println("Ops, algo deu errado!");
		}
	}
}
