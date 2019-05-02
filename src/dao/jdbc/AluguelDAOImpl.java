package dao.jdbc;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import dao.AluguelDAO;
import entidades.Aluguel;
import entidades.Filme;

public class AluguelDAOImpl implements AluguelDAO {

	@Override
	public void insert(Connection conn, Aluguel aluguel) throws Exception {
		PreparedStatement myStmt = conn.prepareStatement(
				"INSERT INTO en_aluguel " + "(id_aluguel, id_cliente, data_aluguel, valor) VALUES (?, ?, ?, ?)");

		Integer idAluguel = this.getNextId(conn);

		myStmt.setInt(1, idAluguel);
		myStmt.setInt(2, aluguel.getCliente().getIdCliente());
		myStmt.setDate(3, (Date) aluguel.getDataAluguel());
		myStmt.setDouble(4, aluguel.getValor());
		myStmt.execute();

		myStmt = conn
				.prepareStatement("INSERT INTO public.re_aluguel_filme (id_filme, id_aluguel) " + " VALUES (?, ?)");

		List<Filme> filmes = aluguel.getFilmes();
		for (Filme filme : filmes) {
			myStmt.setInt(1, filme.getIdFilme());
			myStmt.setInt(2, idAluguel);
			myStmt.execute();
		}

		conn.commit();

		aluguel.setIdAluguel(idAluguel);

	}

	@Override
	public Integer getNextId(Connection conn) throws Exception {
		PreparedStatement myStmt = conn.prepareStatement("SELECT nextval('seq_en_aluguel')");
		ResultSet rs = myStmt.executeQuery();
		rs.next();
		return rs.getInt(1);
	}

	@Override
	public void edit(Connection conn, Aluguel aluguel) throws Exception {
		PreparedStatement myStmt = conn.prepareStatement(
				"UPDATE en_aluguel SET id_cliente = (?), data_aluguel = (?), valor = (?) WHERE id_aluguel = (?)");

		myStmt.setInt(1, aluguel.getCliente().getIdCliente());
		myStmt.setDate(2, (Date) aluguel.getDataAluguel());
		myStmt.setDouble(3, aluguel.getValor());
		myStmt.setInt(4, aluguel.getIdAluguel());
		myStmt.execute();

		myStmt = conn.prepareStatement(
				"DELETE FROM re_aluguel_filme USING en_aluguel " + " WHERE re_aluguel_filme.id_aluguel = (?)");

		myStmt.setInt(1, aluguel.getIdAluguel());
		myStmt.execute();

		myStmt = conn
				.prepareStatement("INSERT INTO public.re_aluguel_filme (id_filme, id_aluguel) " + " VALUES (?, ?)");

		List<Filme> filmes = aluguel.getFilmes();
		for (Filme filme : filmes) {
			myStmt.setInt(1, filme.getIdFilme());
			myStmt.setInt(2, aluguel.getIdAluguel());
			myStmt.execute();
		}

		if (filmes.isEmpty()) {
			myStmt = conn.prepareStatement("DELETE FROM en_aluguel WHERE id_aluguel = (?)");
			myStmt.setInt(1, aluguel.getIdAluguel());
			myStmt.execute();
		}

		conn.commit();
	}

	@Override
	public void delete(Connection conn, Aluguel aluguel) throws Exception {

		PreparedStatement myStmt = conn.prepareStatement(
				"DELETE FROM re_aluguel_filme USING en_aluguel " + " WHERE re_aluguel_filme.id_aluguel = (?)");

		myStmt.setInt(1, aluguel.getIdAluguel());
		myStmt.execute();

		myStmt = conn.prepareStatement("DELETE FROM en_aluguel WHERE id_aluguel = (?)");

		myStmt.setInt(1, aluguel.getIdAluguel());
		myStmt.execute();

		conn.commit();
	}

	@Override
	public Aluguel find(Connection conn, Integer idAluguel) throws Exception {
		PreparedStatement myStmt = conn.prepareStatement("SELECT * FROM en_aluguel WHERE id_aluguel = (?)");

		myStmt.setInt(1, idAluguel);
		ResultSet myRs = myStmt.executeQuery();

		if (!myRs.next()) {
			return null;
		}

		Date dataAluguel = myRs.getDate("data_aluguel");
		float valorAluguel = myRs.getFloat("valor");
		int idCliente = myRs.getInt("id_cliente");

		myStmt = conn.prepareStatement("SELECT * FROM re_aluguel_filme WHERE id_aluguel = (?)");
		myStmt.setInt(1, idAluguel);
		myRs = myStmt.executeQuery();

		// preenchendo a lista de filmes referente a um aluguel especifico.
		List<Filme> filmes = new ArrayList<Filme>();
		while (myRs.next()) {
			filmes.add(new FilmeDAOImpl().find(conn, myRs.getInt("id_filme")));
		}

		return new Aluguel(idAluguel, filmes, new ClienteDAOImpl().find(conn, idCliente), dataAluguel, valorAluguel);
	}

	@Override
	public Collection<Aluguel> list(Connection conn) throws Exception {
		PreparedStatement myStmt = conn.prepareStatement("SELECT * FROM en_aluguel ORDER by id_cliente");
		ResultSet myRs = myStmt.executeQuery();

		Collection<Aluguel> items = new ArrayList<>();

		while (myRs.next()) {
			Integer idAluguel = myRs.getInt("id_aluguel");
			Date dataAluguel = myRs.getDate("data_aluguel");
			float valorAluguel = myRs.getFloat("valor");

			items.add(new Aluguel(idAluguel, find(conn, idAluguel).getFilmes(), find(conn, idAluguel).getCliente(),
					dataAluguel, valorAluguel));
		}
		return items;
	}

}
