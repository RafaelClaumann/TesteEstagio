package view;
import java.sql.Connection;
import java.sql.Date;
import java.util.Collection;
import java.util.Scanner;

import dao.FilmeDAO;
import dao.jdbc.FilmeDAOImpl;
import entidades.Filme;

public class FilmeView {
	static void menuFilme(Connection conn, FilmeDAO filmeDAO) throws Exception {
		System.out.printf("\n1 inserir filme \t2 editar filme " + "\n3 deletar filme \t4 listar filmes\n0 voltar   ");
		int filmeInput = new Scanner(System.in).nextInt();
		System.out.println();

		switch (filmeInput) {
		case 1:
			inserirFilme(conn, filmeDAO);
			break;
		case 2:
			editarFilme(conn, filmeDAO);
			break;
		case 3:
			deletarFilme(conn, filmeDAO);
			break;
		case 4:
			listarFilmes(conn);
			break;
		case 5:
			buscarFilme(conn, filmeDAO);
			break;
		default:
			break;
		}
	}

	private static void inserirFilme(Connection conn, FilmeDAO filmeDAO) {
		Scanner scanner = new Scanner(System.in);

		System.out.printf("Insira o nome do filme: ");
		String nomeFilme = scanner.nextLine();

		System.out.printf("Insira a descrição do filme: ");
		String descFilme = scanner.nextLine();

		try {
			filmeDAO.insert(conn, new Filme(0, getDate(), nomeFilme, descFilme));
		} catch (Exception e) {
			System.out.println("Ops, algo deu errado!");
		}
	}

	private static void editarFilme(Connection conn, FilmeDAO filmeDAO) {
		listarFilmes(conn);
		Filme filme;
		Scanner scanner = new Scanner(System.in);

		try {
			System.out.printf("Insira o ID do filme: ");
			int idFilme = new Scanner(System.in).nextInt();

			filme = filmeDAO.find(conn, idFilme);

			filme.setDataLancamento(getDate());

			System.out.printf("Insira o nome do filme: ");
			String nomeFilme = scanner.nextLine();
			filme.setNome(nomeFilme);

			System.out.printf("Insira a descrição do filme: ");
			String descFilme = scanner.nextLine();
			filme.setDescricao(descFilme);

			filmeDAO.edit(conn, filme);
		} catch (Exception e) {
			System.out.println("Ops, algo deu errado!");
		}
	}

	private static void deletarFilme(Connection conn, FilmeDAO filmeDAO) {
		listarFilmes(conn);

		System.out.printf("Insira o ID do filme: ");
		try {
			filmeDAO.delete(conn, new Scanner(System.in).nextInt());
		} catch (Exception e) {
			System.out.println("Filme não existe ou está alugado, impossível apagar!");
		}
	}

	private static void buscarFilme(Connection conn, FilmeDAO filmeDAO) {
		listarFilmes(conn);

		try {
			System.out.printf("Insira o ID do filme: ");
			Filme filme = filmeDAO.find(conn, new Scanner(System.in).nextInt());

			System.out.println(filme.toString());
		} catch (Exception e) {
			System.out.println("Ops, algo deu errado!\n");
		}
	}

	@SuppressWarnings("deprecation")
	private static Date getDate() {
		System.out.println("A data é dividida em YYY/MM/DD");

		Scanner scanner = new Scanner(System.in);
		System.out.printf("Insira o ano de lançamento: ");
		int anoLancamento = scanner.nextInt();

		System.out.printf("Insira o mes de lançamento: ");
		int mesLancamento = scanner.nextInt();

		System.out.printf("Insira o dia de lançamento: ");
		int diaLancamento = scanner.nextInt();

		return new Date(anoLancamento, mesLancamento, diaLancamento);
	}

	static void listarFilmes(Connection conn) {
		System.out.println("\n------------ FILMES DISPONIVEIS --------------");
		Collection<Filme> filmes;
		try {
			filmes = new FilmeDAOImpl().list(conn);
			for (Filme filme : filmes) {
				System.out.println(filme.toString());
			}
			System.out.println("------------------------------------------------");
		} catch (Exception e) {
			System.out.println("Ops, algo deu errado!");
		}
	}

}
