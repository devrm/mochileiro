package mochileiro.Modelo.DAO;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import mochileiro.Modelo.Rota;
import mochileiro.Modelo.Usuario;
import mochileiro.Modelo.Viajante;
import mochileiro.Util.Conexao;

/**
 * @Autor David
 * @Projeto mochileiro
 * @Data 12/06/2009
 * @Hora 21:39:28
 */
public class ViajanteDAO {

	private static Connection 		 con   = null;
	private static PreparedStatement pStmt = null;
	private static ResultSet         rs    = null;

	/**
	 * Persiste a entidade viajante
	 * @param viajante
	 * @throws Exception
	 */
	public static void inserir(Viajante viajante) throws Exception {

		CallableStatement cStmt = null;

		try{
			//invoca método de conexão
			con = Conexao.getConexao();

			cStmt = con.prepareCall("{call INSERE_VIAJANTE(?,?,?,?,?,?,?,?,?,?,?,?,?)}");

			cStmt.setString(1,  viajante.getNome());
			cStmt.setString(2,  viajante.getEndereco().getLogradouro());
			cStmt.setString(3,  viajante.getEndereco().getNumero());
			cStmt.setString(4,  viajante.getEndereco().getComplemento());
			cStmt.setString(5,  viajante.getEndereco().getBairro());
			cStmt.setString(6,  viajante.getEndereco().getCidade());
			cStmt.setInt   (7,  viajante.getEndereco().getUf());
			cStmt.setString(8,  viajante.getEndereco().getCep());
			cStmt.setString(9,  viajante.getDDD() + viajante.getTelefone());
			cStmt.setString(10, viajante.getCpf());
			cStmt.setString(11, viajante.getEmail());
			cStmt.setString(12, viajante.getUsuario().getNickName());
			cStmt.setString(13, viajante.getUsuario().getSenha());

			cStmt.execute();
		}   
		finally	{
			//FECHO A CONEXÃO
			Conexao.finalizaOperacaoBD(cStmt, rs); 
		}
	}

	/**
	 * Atualiza a entidade viajante
	 * 
	 * @return Controle de erro da operação
	 */
	public static void atualizar(Viajante viajante) throws Exception {

		CallableStatement cStmt = null;

		try{

			con = Conexao.getConexao();

			cStmt = con.prepareCall("{call atualiza_viajante(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

			cStmt.setString(1,  viajante.getNome());
			cStmt.setString(2,  viajante.getEndereco().getLogradouro());
			cStmt.setString(3,  viajante.getEndereco().getNumero());
			cStmt.setString(4,  viajante.getEndereco().getComplemento());
			cStmt.setString(5,  viajante.getEndereco().getBairro());
			cStmt.setString(6,  viajante.getEndereco().getCidade());
			cStmt.setInt   (7,  viajante.getEndereco().getUf());
			cStmt.setString(8,  viajante.getEndereco().getCep());
			cStmt.setString(9,  viajante.getDDD() + viajante.getTelefone());
			cStmt.setString(10, viajante.getCpf());
			cStmt.setString(11, viajante.getEmail());
			cStmt.setString(12, viajante.getUsuario().getNickName());
			cStmt.setString(13, viajante.getUsuario().getSenha());
			cStmt.setInt   (14, viajante.getUsuario().getCodigo());		
			cStmt.setString(15, viajante.getUsuario().getTipo());

			cStmt.execute();

		}finally{

			Conexao.finalizaOperacaoBD(cStmt);
		}
	}

	/**
	 * Seleciona a empresa utilizando os filtros desejados, caso algum dos parametros
	 * possua o valor null, o mesmo sera ignorado na busca e nao sera usado como filtro.
	 */
	public static Viajante selecionar(Integer id, String cpf) throws Exception {

		List<Object> parametros = new ArrayList<Object>();
		Viajante     viajante 	= null;
		String   	 where 		= "";

		try {

			String query  = "SELECT * FROM TB_VIAJANTE " +
			"INNER JOIN TB_USUARIO " +
			"ON COD_VIAJANTE = COD_USUARIO " +
			"WHERE UPPER(TIPO_USUARIO) = UPPER('"+Usuario.TIPO_USUARIO_VIAJANTE+"') AND ";

			con = Conexao.getConexao();

			if (id != null) {
				parametros.add(id);
				where = "COD_VIAJANTE = ? AND ";
				query = query.concat(where);
			}

			if (cpf != null) {
				parametros.add(cpf);
				where = "CPF = ? AND ";
				query = query.concat(where);
			}

			if (! where.equals("")){
				query = query.substring(0, query.lastIndexOf("AND"));
			}

			pStmt = con.prepareStatement(query);

			for (int i = 0; i < parametros.size(); i++) {
				pStmt.setObject(i+1, parametros.get(i));
			}

			rs = pStmt.executeQuery();

			if (rs.next()) {

				viajante = new Viajante();

				viajante.setNome                    	(rs.getString("nome"));
				viajante.getEndereco().setLogradouro	(rs.getString("logradouro"));
				viajante.getEndereco().setNumero    	(rs.getString("numero"));
				viajante.getEndereco().setComplemento   (rs.getString("complemento"));
				viajante.getEndereco().setBairro        (rs.getString("bairro"));
				viajante.getEndereco().setCidade        (rs.getString("cidade"));
				viajante.getEndereco().setUf            (rs.getInt   ("estado"));
				viajante.getEndereco().setCep        	(rs.getString("cep"));
				viajante.setTelefone   					(rs.getString("telefone"));
				viajante.setCpf        			 		(rs.getString("cpf"));
				viajante.setEmail      			 		(rs.getString("e_mail"));
				viajante.getUsuario().setCodigo			(rs.getInt	 ("cod_usuario"));
				viajante.getUsuario().setNickName		(rs.getString("usuario"));
				viajante.getUsuario().setSenha   		(rs.getString("senha"));
				viajante.getUsuario().setTipo			(rs.getString("tipo_usuario"));
				viajante.getDataCadastro().setDataBR	(rs.getString("data_cadastro"));
			}

		}catch(Exception e){

			e.printStackTrace();

		} finally {
			Conexao.finalizaOperacaoBD(pStmt, rs);
		}
		return viajante;
	}

	public static void insereFav(int codigoAtividade, int codigoViajante) throws Exception {

		pStmt = null;

		String query = "INSERT INTO TB_FAVORITOS(COD_ATIVIDADE_FK, COD_VIAJANTE_FK)	VALUES (?, ?)";

		try{
			//invoca método de conexão
			con = Conexao.getConexao();

			//prepara query para execução
			pStmt = con.prepareStatement(query);

			//define parâmetros que serão enviados ao BD
			pStmt.setInt   (1,  codigoAtividade);
			pStmt.setInt   (2,  codigoViajante);

			//executa instrução
			pStmt.execute();
		}   
		finally	{
			Conexao.finalizaOperacaoBD(pStmt, rs); 
		}
	}

	public static void excluiFav(int codigoAtividade, int codigoViajante) throws Exception {

		pStmt = null;

		String query = "DELETE FROM TB_FAVORITOS WHERE COD_ATIVIDADE_FK = ? AND COD_VIAJANTE_FK = ?";

		try{
			//invoca método de conexão
			con = Conexao.getConexao();

			//prepara query para execução
			pStmt = con.prepareStatement(query);

			//define parâmetros que serão enviados ao BD
			pStmt.setInt   (1,  codigoAtividade);
			pStmt.setInt   (2,  codigoViajante);

			//executa instrução
			pStmt.execute();
		}   
		finally	{
			Conexao.finalizaOperacaoBD(pStmt, rs); 
		}
	}
	
	
	public static void salvaRota(Integer codigoViajante, Integer codigoOrigem, Integer codigoDestino) throws Exception{
		
		try {

			pStmt = null;
			//define tabelas alvo
			String query = "INSERT INTO TB_ROTAS (COD_VIAJANTE, COD_ATV_ORIGEM, COD_ATV_DESTINO) "+
							" VALUES (?, ?, ?) ";

				
			//invoca método de conexão
			con = Conexao.getConexao();

			//prepara query para execução
			pStmt = con.prepareStatement(query);

			//seta filtro
			pStmt.setInt   (1,  codigoViajante);
			pStmt.setInt   (2,  codigoOrigem);
			pStmt.setInt   (3,  codigoDestino);			

			//executa instrução
			pStmt.execute();

		} finally	{
			Conexao.finalizaOperacaoBD(pStmt); 
		}		
	}
	
	/**
	 * Seleciona rotas cadastradas pelo viajante
	 * @param codigoViajante
	 * @return
	 * @throws Exception
	 */
	public static Rota selecionaRotas(Integer codigoViajante) throws Exception {

		Rota rota = null;

		try {

			pStmt = null;
			//define tabelas alvo
			String query = "SELECT * FROM TB_ROTAS WHERE COD_VIAJANTE =? ORDER BY DATA_CRIACAO DESC"; 	
				
			//invoca método de conexão
			con = Conexao.getConexao();

			//prepara query para execução
			pStmt = con.prepareStatement(query);

			//seta filtro
			pStmt.setInt   (1,  codigoViajante);

			//executa instrução
			rs = pStmt.executeQuery();

			//caso exista retorno...
			if (rs.next()) {				
				
				rota = new Rota();
				
				//seta relacionamento em objeto rota
				rota.setCodigoRota					(rs.getInt     ("COD_ROTAS"));
				rota.setCodigoOrigem				(rs.getInt     ("COD_ATV_ORIGEM"));
				rota.setCodigoDestino				(rs.getInt     ("COD_ATV_DESTINO"));
				rota.getDataCadastroRota().setDataBR(rs.getString  ("DATA_CRIACAO"));
				
			}

		} finally	{
			Conexao.finalizaOperacaoBD(pStmt, rs); 
		}

		return rota;
	}




}
