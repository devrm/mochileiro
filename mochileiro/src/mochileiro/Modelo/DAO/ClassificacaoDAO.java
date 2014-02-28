package mochileiro.Modelo.DAO;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import mochileiro.Modelo.Classificacao;
import mochileiro.Util.Conexao;

/**
 * @author Thiago
 * @projeto mochileiro
 * @Data 06/02/2010 @Hora 10:12:54
 * @Descrição: Classe responsável pela persistência dos dados referentes a persistência das classificações da atividade
 */
public class ClassificacaoDAO {

	private static Connection con          = null;
	private static PreparedStatement pStmt = null;
	private static CallableStatement cStmt = null;	
	private static ResultSet rs            = null;


	/**
	 * Método responsável por persistir as classificações das atividades enviadas pelos viajantes 
	 * @param classificacao
	 * @return
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 */
	public static void insereClassificao(Classificacao classificacao) throws SQLException, ClassNotFoundException {

		try{
			//invoca método de conexão
			con = Conexao.getConexao();

			//invoca function para inserção
			cStmt = con.prepareCall("{call INSERE_CLASSIFICACAO(?, ?, ?, ?)}");

			//PARÂMETROS PARA INSERÇÃO
			cStmt.setInt    (1, classificacao.getCodAtividade()); 
			cStmt.setInt    (2, classificacao.getCodViajante()); 	
			cStmt.setBoolean(3, classificacao.getNota());
			cStmt.setString (4, classificacao.getComentarioViajante());
			
			cStmt.execute();
		}   
		finally	{
			//FECHO A CONEXÃO
			Conexao.finalizaOperacaoBD(cStmt, rs); 
		}
	}


	public static ArrayList<Classificacao> selecionaClassificao(int codAtividade) throws Exception {

		ArrayList<Classificacao> listaCla = new ArrayList<Classificacao>();

		try{
			//invoca método de conexão
			con = Conexao.getConexao();

			String sql= " SELECT  " +

			//CAMPOS TABELA CLASSFICA
			" TB_CLASSIFICA.COD_CLASSIFICA, TB_CLASSIFICA.COD_ATV_FK, TB_CLASSIFICA.COD_VIAJANTE, TB_CLASSIFICA.NOTA, TB_CLASSIFICA.COMENTARIO, " +
			" TB_CLASSIFICA.DATA_COMENTARIO , "+

			//CAMPOS TABELA VIAJANTE
			" TB_USUARIO.USUARIO " +

			//JUNÇÃO
			" FROM TB_CLASSIFICA INNER JOIN TB_USUARIO ON TB_CLASSIFICA.COD_VIAJANTE = TB_USUARIO.COD_USUARIO " +

			//FILTRO DE BUSCA
			" WHERE COD_ATV_FK = ? AND UPPER(TIPO_USUARIO) = UPPER('Viajante') " +
			
			//ORDENAÇÃO
			"ORDER BY DATA_COMENTARIO DESC";

			pStmt = con.prepareStatement(sql);

			pStmt.setInt(1, codAtividade);

			rs = pStmt.executeQuery();

			// Caso exista dados...
			while(rs.next()){

				Classificacao cla = new Classificacao();

				//Resgata registros e insere em objeto classificacao
				cla.setCodAtividade      			(rs.getInt    ("COD_ATV_FK"));
				cla.setCodClassificacao  			(rs.getInt	  ("COD_CLASSIFICA")); //serial tabela
				cla.setCodViajante 	     			(rs.getInt    ("COD_VIAJANTE"));
				cla.setNickViajante      			(rs.getString ("USUARIO"));
				cla.setComentarioViajante			(rs.getString ("COMENTARIO"));
				cla.getDataComentario().setDataBR 	(rs.getString ("DATA_COMENTARIO"));
				cla.setNota			     			(rs.getBoolean("NOTA"));
				
				//adiciono objeto na coleção
				listaCla.add(cla);
			}
		}   
		finally	{
			//fecho conexão
			Conexao.finalizaOperacaoBD(pStmt, rs); 
		}

		//retorno coleção de objetos classificação
		return listaCla;
	}



}
