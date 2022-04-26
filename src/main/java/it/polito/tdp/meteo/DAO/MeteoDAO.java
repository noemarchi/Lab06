package it.polito.tdp.meteo.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import it.polito.tdp.meteo.model.Citta;
import it.polito.tdp.meteo.model.Rilevamento;

public class MeteoDAO {
	
	public List<Rilevamento> getAllRilevamenti() 
	{

		final String sql = "SELECT Localita, Data, Umidita FROM situazione ORDER BY data ASC";

		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>();

		try 
		{
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			while (rs.next()) 
			{
				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				rilevamenti.add(r);
			}

			conn.close();
			return rilevamenti;

		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	
	/**
	 * Cerca i rilevamenti di una determinata città in un determinato mese
	 * @param mese
	 * @param citta
	 * @return
	 */
	public List<Rilevamento> getAllRilevamentiLocalitaMese(int mese, Citta citta) 
	{
		final String sql = "SELECT localita, data, umidita "
				+ "FROM situazione "
				+ "WHERE localita = ? "
				+ "AND MONTH(data) = ? "
				+ "ORDER BY data ASC";
		
		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>();
		
		try 
		{
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			
			st.setString(1, citta.getNome());
			st.setString(2, Integer.toString(mese));

			ResultSet rs = st.executeQuery();

			while (rs.next()) 
			{
				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				rilevamenti.add(r);
			}

			conn.close();
			return rilevamenti;

		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Elenco di rilevamenti di tutte le città nel messe passato come parametro
	 * @param mese
	 * @return
	 */
	public Map<String, Double> getUmiditaMediaCitta(int mese)
	{
		Map<String, Double> ret = new TreeMap<String, Double>();
		
		final String sql = "SELECT Localita, AVG(Umidita) AS avg_umidita "
				+ "FROM situazione "
				+ "WHERE MONTH(Data) = ? "
				+ "GROUP BY Localita";
		
		try 
		{
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			
			st.setInt(1, mese);
			
			ResultSet rs = st.executeQuery();
			
			while(rs.next())
			{
				String citta = rs.getString("Localita");
				double avg = rs.getDouble("avg_umidita");
				
				ret.put(citta, avg);
			}
			
			rs.close();
			st.close();
			conn.close();
			
			return ret;
		}
		catch(SQLException e)
		{
			System.out.println("ERRORE nel DAO");
			e.printStackTrace();
			
			return null;
		}
	}

	/**
	 * Elenco di tutte le città presenti nel database
	 * @return
	 */
	public List<Citta> getAllCitta() {

		final String sql = "SELECT DISTINCT localita FROM situazione ORDER BY localita";

		List<Citta> result = new ArrayList<Citta>();

		try 
		{
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			while (rs.next()) 
			{
				Citta c = new Citta(rs.getString("localita"));
				result.add(c);
			}

			conn.close();
			return result;
			
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Dato un mese ed una città estrarre dal DB l'umidità media relativa a tale mese e tale città
	 * (tutti i calcoli sono delegati al database)
	 * @param mese
	 * @param citta
	 * @return
	 */
	public Double getUmiditaMedia(int mese, Citta citta) {

		final String sql = "SELECT AVG(Umidita) AS U FROM situazione " +
						   "WHERE localita=? AND MONTH(data)=? ";

		try 
		{
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			
			st.setString(1, citta.getNome());
			//st.setString(2, mese.getValue()); se fosse un oggetto month
			st.setString(2, Integer.toString(mese)); 

			ResultSet rs = st.executeQuery();

			rs.next(); // si posiziona sulla prima (ed unica) riga
			Double u = rs.getDouble("U");

			conn.close();
			return u;
			
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}
}
