package mochileiro.Modelo.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import mochileiro.Modelo.Usuario;
import mochileiro.Util.Conexao;


/**
 * @author Thiago
 * @projeto mochileiro
 * @Data 27/09/2009 @Hora 22:45:37
 * @Descrição: Classe que realiza persistência da entidade Usuário
 */

public class UsuarioDAO {

	private static Connection con          = null;
	private static PreparedStatement pStmt = null;
	private static ResultSet rs            = null;	

	/**
	 * Seleciona o usuario utilizando os filtros desejados, caso algum dos parametros
	 * possua o valor null, o mesmo sera ignorado na busca e nao sera usado como filtro.
	 */
	public static Usuario selecionar(Integer codigo, String nickName)throws Exception{

		List<Object> parametros = new ArrayList<Object>();
		Usuario 	 usuario	= null;
		String 		 where 		= "";
		

		try{
			con = Conexao.getConexao();

			String query = "SELECT * FROM TB_USUARIO WHERE ";
									
			if (codigo != null) {
				parametros.add(codigo);
				where = "COD_USUARIO = ? AND ";
				query = query.concat(where);
			}
			
			if (nickName != null) {
				parametros.add(nickName);
				where = "UPPER(USUARIO) = UPPER(?) AND ";
				query = query.concat(where);
			}
			
			if(! where.equals("")) {
				query = query.substring(0, query.lastIndexOf("AND "));
			}
		
			pStmt = con.prepareStatement(query);

			for (int i = 0; i < parametros.size(); i++) {
				pStmt.setObject(i+1, parametros.get(i));
			}
			
			
			rs = pStmt.executeQuery();
			
			if(rs.next()){	
				usuario = new Usuario();
				
				usuario.setCodigo	(rs.getInt   ("COD_USUARIO"));
				usuario.setNickName (rs.getString("USUARIO"));
				usuario.setSenha	(rs.getString("SENHA"));	   
				usuario.setTipo		(rs.getString("TIPO_USUARIO"));
			}
		}finally{
			Conexao.finalizaOperacaoBD(pStmt, rs); 
		}
		return usuario;
	}
}



