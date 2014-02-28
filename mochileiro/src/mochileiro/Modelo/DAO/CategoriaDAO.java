package mochileiro.Modelo.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import mochileiro.Modelo.Categoria;
import mochileiro.Util.Conexao;

/**
 * 
 * @author Thiago
 * @projeto mochileiro
 * @Data 30/10/2009 @Hora 17:53:49
 * @Descrição: Classe responsável pela persistência da entidade Categoria
 */
public class CategoriaDAO {

	private static Connection con          = null;
	private static PreparedStatement pStmt = null;
	private static ResultSet rs            = null;


	/**
	 * Método responsável por resgatar os tipos de categorias cadastrados no sistema
	 * retorna um ArrayList de objetos Categoria
	 * @return ArrayList
	 * @throws Exception 
	 */
	public static HashMap<Integer, Categoria> selecionar() throws Exception{

		HashMap<Integer, Categoria> cat = new HashMap<Integer, Categoria>();

		try{
			
			con = Conexao.getConexao();
			
			String sql="SELECT * FROM TB_CATEGORIA";
			pStmt = con.prepareStatement(sql);
			rs = pStmt.executeQuery();

			//caso exista valor de retorno na consulta...
			while(rs.next()){

				int codigo       = rs.getInt   ("cod_categoria");
				String categoria = rs.getString("nome_categoria");
				String descricao = rs.getString("descricao_categoria");
				String tipo		 = rs.getString("tipo_categoria");
				
				//cria uma lista de objetos Categoria com as informações da query
				cat.put(codigo, new Categoria(codigo, categoria, descricao, tipo));			
			}

		}finally {		
			Conexao.finalizaOperacaoBD(pStmt, rs);
		}
		return cat; 
	}	
	
	/**
	 * Método responsável pela consulta de categorias mais adicionadas na lista de favoritos
	 * 
	 * @return
	 * @throws Exception
	 */
	public static HashMap<Integer, Categoria> selecionarRelatorio() throws Exception{

		HashMap<Integer, Categoria> cat = new HashMap<Integer, Categoria>();

		try{
			
			con = Conexao.getConexao();
			
			String sql= " select cat.cod_categoria, nome_categoria, descricao_categoria, " +
					    " tipo_categoria, count(atv.cod_atividade) as Adicionada from tb_atividade atv "+
					    " inner join tb_favoritos on atv.cod_atividade = cod_atividade_fk "+
					    " inner join tb_categoria cat on atv.cod_categoria = cat.cod_categoria "+ 
					    " group by cat.cod_categoria, nome_categoria, descricao_categoria, tipo_categoria "+
					    " order by Adicionada desc ";
			
			
			pStmt = con.prepareStatement(sql);
			rs = pStmt.executeQuery();

			//caso exista valor de retorno na consulta...
			while(rs.next()){

				int codigo       = rs.getInt   ("cod_categoria");
				String categoria = rs.getString("nome_categoria");
				String descricao = rs.getString("descricao_categoria");
				String tipo		 = rs.getString("tipo_categoria");
				int	adicao       = rs.getInt   ("adicionada");
				
				//cria uma lista de objetos Categoria com as informações da query
				cat.put(codigo, new Categoria(codigo, categoria, descricao, tipo, adicao));			
			}

		}finally {		
			Conexao.finalizaOperacaoBD(pStmt, rs);
		}
		return cat; 
	}		
	
}
