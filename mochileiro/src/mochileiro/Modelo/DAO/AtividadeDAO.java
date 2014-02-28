package mochileiro.Modelo.DAO;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mochileiro.Modelo.Atividade;
import mochileiro.Modelo.IAtividade;
import mochileiro.Util.Conexao;
import mochileiro.Util.Data;

/**
 * @author Thiago
 * @projeto mochileiro
 * @Data 17/11/2009 @Hora 21:44:58
 * @Descrição: classe de persistência da entidade  Atividade
 */
public class AtividadeDAO {


	private static Connection con          = null;
	private static PreparedStatement pStmt = null;
	private static ResultSet rs            = null;	


	/**
	 * Interface pública para busca de atividades pelo seu nome
	 * @param codigoAtividade
	 * @return
	 * @throws Exception
	 */
	public static HashMap<Integer,Atividade> selecionarPorNome(String nomeAtividade) throws Exception{

		//utilizo nome da atividade como parâmetro de busca e invoco método de seleção
		//extraindo atividade da coleção para objeto de retorno utilizo indice zero
		return selecionarAtividades(nomeAtividade, null, null, null);

	}	

	/**
	 * 
	 * @param codigoEmpresa
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static HashMap<Integer, Atividade> selecionarAtividadesResumo(Integer codigoEmpresa) throws Exception {

		HashMap<Integer, Atividade> listaAtividade = new HashMap<Integer, Atividade>();

		List<Object> parametros = new ArrayList<Object>();
		
		int limiteResultados = 20;

		pStmt = null;

		rs = null;

		String query =  " select distinct nome_atividade, cod_atividade, nome_categoria, codigo_parceiro, "+
		" (select count(nota) from tb_classifica inner join tb_atividade on tb_classifica.cod_atv_fk = atv.cod_atividade  "+ 
		" where cod_atividade = atv.cod_atividade and nota = true) as NotaPositiva,  "+
		" (select count(nota) from tb_classifica inner join tb_atividade on tb_classifica.cod_atv_fk = atv.cod_atividade  "+ 
		" where cod_atividade = atv.cod_atividade and nota = false) as NotaNegativa,  "+ 
		" (select count(*) from tb_favoritos inner join tb_atividade on tb_favoritos.cod_atividade_fk = atv.cod_atividade  "+ 
		" where cod_atividade = atv.cod_atividade) as Adicionada  "+
		" from tb_atividade atv  "+
		" inner join tb_favoritos fav on atv.cod_atividade = fav.cod_atividade_fk  "+ 
		" inner join tb_categoria cat on atv.cod_categoria = cat.cod_categoria  "+ 
		" inner join tb_classifica clas on atv.cod_atividade = clas.cod_atv_fk  ";

		try {

			con = Conexao.getConexao();		

			if(codigoEmpresa != null){

				//caso seleção seja por empresa específica, adiciona filtro a busca  
				query= query.concat(" where atv.codigo_empresa = ? ");

				parametros.add(codigoEmpresa);				
			}

			//ordernação
			query = query.concat(" order by adicionada desc, notapositiva desc ");	
			
			//traz 20 resultados caso não exista empresa como filtro
			if(codigoEmpresa == null){

				//caso seleção seja por empresa específica, adiciona filtro a busca  
				query= query.concat(" limit  ? ");

				parametros.add(limiteResultados);				
			}			

			//prepara query para execução
			pStmt = con.prepareStatement(query);

			for (int i = 0; i < parametros.size(); i++) {
				pStmt.setObject(i+1, parametros.get(i));
			}			

			rs = pStmt.executeQuery();
			

			while(rs.next()){

				//Resgata registros e insere em objeto atividade
				Atividade atividade = new Atividade();
				
				atividade.setCodigoAtividade      	 (rs.getInt   ("COD_ATIVIDADE"));

				atividade.setNomeAtividade        	 (rs.getString("NOME_ATIVIDADE"));

				atividade.getCategoria().setNome     (rs.getString("NOME_CATEGORIA"));

				//alias utilizado na query contagem de qualificações negativas
				atividade.setQualificacoesNegativas  (rs.getInt   ("NOTANEGATIVA")); 

				//alias utilizado na query contagem de qualificações positivas
				atividade.setQualificacoesPositivas  (rs.getInt   ("NOTAPOSITIVA")); 

				//alias utilizado na query contagem de vezes que 
				//atividade foi adicionada pelos viajantes
				atividade.setAdicionada				 (rs.getInt   ("ADICIONADA"));   		

				atividade.setCodigoParceiro        	 (rs.getInt   ("CODIGO_PARCEIRO"));

				listaAtividade.put(atividade.getCodigoAtividade() , atividade);	
			}

		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			Conexao.finalizaOperacaoBD(pStmt, rs);
		}
		return listaAtividade;
	}



	/**
	 * Consulta realizada pelo sistema de busca
	 * adequa campos conforme parâmetros enviados
	 * 
	 * @param nome
	 * @param logradouro
	 * @param bairro
	 * @param complemento
	 * @param numero
	 * @param cep
	 * @param codigoCategoria
	 * @param dataInicio
	 * @param dataFim
	 * @param qualificacao
	 * @return
	 * @throws Exception
	 * */

	public static ArrayList<Atividade> buscar(String nome, String logradouro, String bairro, String complemento, 
			String numero, String cep, Integer codigoCategoria,
			Data dataInicio, Data dataFim) throws Exception {
		ArrayList<Atividade> listaAtividade = new ArrayList<Atividade>();
		String query = "",
		where = "WHERE ",
		order = "ORDER BY DATA_CADASTRO DESC";

		String dataInicioBusca = "";
		String dataFimBusca    = "";

		List<Object> parametros = new ArrayList<Object>();

		try {
			con = Conexao.getConexao();

			query = "SELECT COD_ATIVIDADE," +
			"NOME_ATIVIDADE, " +
			"CODIGO_PARCEIRO, " +
			"DESC_ATV, " +
			"CUSTO, " +
			"PER_DISP_INI, " +
			"PER_DISP_FIM, " +
			"LOGRADOURO, " +
			"NUMERO, "+
			"COMPLEMENTO, " +
			"BAIRRO, " +
			"CEP, " +
			"HORA_FUNC_INI, " +
			"HORA_FUNC_FIM, " +
			"DIA_SEM_INI, " +
			"DIA_SEM_FIM, " +
			"IMAGEM, " +
			"DATA_CADASTRO, " +
			"CODIGO_EMPRESA," +
			"TB_CATEGORIA.COD_CATEGORIA, " +
			"TB_CATEGORIA.NOME_CATEGORIA, " +
			"TB_CATEGORIA.DESCRICAO_CATEGORIA, " +
			"TB_CATEGORIA.TIPO_CATEGORIA " +
			"FROM TB_ATIVIDADE " +
			"INNER JOIN TB_CATEGORIA ON TB_ATIVIDADE.COD_CATEGORIA = TB_CATEGORIA.COD_CATEGORIA ";
			
			if(nome != null){
				if (! nome.trim().equals("")) {
					where = where.concat("UPPER(NOME_ATIVIDADE) LIKE UPPER(?) AND ");
					parametros.add("%"+nome+"%");
				}
			}
			if(logradouro != null){
				if (!logradouro.trim().equals("")) {
					where = where.concat("UPPER(LOGRADOURO) LIKE UPPER(?) AND ");
					parametros.add("%"+logradouro+"%");
				}
			}
			if(bairro != null){
				if (! bairro.trim().equals("")) {
					where = where.concat("UPPER(BAIRRO) LIKE UPPER(?) AND ");
					parametros.add("%"+bairro+"%");
				}
			}
			if(complemento != null){
				if (! complemento.trim().equals("")) {
					where = where.concat("UPPER(COMPLEMENTO) LIKE UPPER(?) AND ");
					parametros.add("%"+complemento+"%");
				}
			}
			if(numero != null){			
				if (! numero.trim().equals("")) {
					where = where.concat("NUMERO = ? AND ");
					parametros.add(numero);
				}
			}
			if(cep != null){
				if (! cep.trim().equals("")) {
					where = where.concat("UPPER(CEP) LIKE UPPER(?) AND ");
					parametros.add("%"+cep+"%");
				}
			}
			
			System.out.println("codigoCategoria "+codigoCategoria);
			
			if(codigoCategoria != null){
				if (codigoCategoria != 0) {
					where = where.concat("TB_ATIVIDADE.COD_CATEGORIA = ? AND ");
					parametros.add(codigoCategoria);
				}
			}
			if(dataInicio.getDia() != null){
				//Se foi usado algum dos parametros de data na busca
				if (!dataInicio.getDataSemFormatacao().trim().equals("") || 
						! dataInicio.getDataSemFormatacao().trim().equals("")) {
	
					//Configura para que se não foi passada alguma data de inicio, utilizasse a primeira
					//data válida para busca
					if (! dataInicio.getDataSemFormatacao().trim().equals("")) 
						dataInicioBusca = dataInicio.getDataSemFormatacao();
					else
						dataInicioBusca = Data.DATA_INICIO_SISTEMA_YYYYMMDD;
	
					//Mesmo tratamento para data fim
					if (! dataFim.getDataSemFormatacao().trim().equals(""))
						dataFimBusca = dataFim.getDataSemFormatacao();
					else
						dataFimBusca = Data.DATA_FIM_SISTEMA_YYYYMMDD;
	
					parametros.add(dataInicioBusca);
					parametros.add(dataFimBusca);
					where = where.concat("(REPLACE( CAST(PER_DISP_INI AS VARCHAR), '-', '') >= ? AND " +
					"REPLACE( CAST(PER_DISP_FIM AS VARCHAR), '-', '') <= ?) AND ");
	
				}
			}
			if (where.endsWith("AND ")) {

				where = where.substring(0, where.lastIndexOf("AND "));
				query = query.concat(where);
				query = query.concat(order);
			}			

			pStmt = con.prepareStatement(query);

			for (int i = 0; i < parametros.size(); i++) {
				pStmt.setObject(i+1, parametros.get(i));
			}
			rs = pStmt.executeQuery();

			while(rs.next()){

				//Resgata registros e insere em objeto atividade
				Atividade atividade = new Atividade();

				atividade.setCodigoAtividade      	 (rs.getInt   ("COD_ATIVIDADE"));
				atividade.getCategoria().setCodigo	 (rs.getInt   ("COD_CATEGORIA"));
				atividade.getCategoria().setNome     (rs.getString("NOME_CATEGORIA"));
				atividade.setNomeAtividade        	 (rs.getString("NOME_ATIVIDADE"));
				atividade.setDescricao	        	 (rs.getString("DESC_ATV"));
				atividade.setPreco          	     (rs.getString("CUSTO"));
				atividade.getDataInicial().setDataBR (rs.getString("PER_DISP_INI"));
				atividade.getDataFinal().setDataBR 	 (rs.getString("PER_DISP_FIM"));
				atividade.getEndereco().setLogradouro(rs.getString("LOGRADOURO"));
				atividade.getEndereco().setNumero	 (rs.getString("NUMERO"));
				atividade.getEndereco().setBairro	 (rs.getString("BAIRRO"));
				atividade.getEndereco().setComplemento(rs.getString("COMPLEMENTO"));
				atividade.getEndereco().setCep		 (rs.getString("CEP"));
				atividade.getDataInicial().setHoraBR (rs.getString("HORA_FUNC_INI"));
				atividade.getDataFinal().setHoraBR 	 (rs.getString("HORA_FUNC_FIM"));
				atividade.getDataInicial().setDiaSemana(rs.getString("DIA_SEM_INI"));			
				atividade.getDataFinal().setDiaSemana(rs.getString("DIA_SEM_FIM"));
				atividade.setImagem		        	 (rs.getString("IMAGEM"));
				atividade.getDataCadastro().setDataBR(rs.getString("DATA_CADASTRO"));
				atividade.setCodigoEmpresa         	 (rs.getInt   ("CODIGO_EMPRESA"));
				atividade.setCodigoParceiro        	 (rs.getInt   ("CODIGO_PARCEIRO"));

				listaAtividade.add(atividade);
			}
		} finally {
			Conexao.finalizaOperacaoBD(pStmt, rs);
		}
		return listaAtividade;
	}

	/**
	 * Cadastra uma solicitção de parceria
	 * 
	 * @param codigoSolicitante
	 * @param codigoSolicitada
	 * @throws Exception
	 */
	public static void cadastraSolicitacao(int codigoSolicitante, int codigoSolicitada, int codigoEmpresaSolicitada) throws Exception{

		PreparedStatement cStmt = null;	

		try{
			con = Conexao.getConexao();		

			cStmt = con.prepareCall("{call INSERE_SOLICITACAO(?,?,?,?)}");

			cStmt.setInt	(1,  codigoSolicitante); 		//atividade que envia a solicitação			
			cStmt.setInt	(2,  codigoSolicitada);	 		//atividade que recebe o convite de parceria
			cStmt.setInt	(3,  codigoEmpresaSolicitada);  //empresa responsável pela atividade que recebe o convite	
			cStmt.setBoolean(4,  false); 					//booleano de status, sempre estará como false

			cStmt.execute();

		} finally {
			Conexao.finalizaOperacaoBD(cStmt); 
		}
	}




	/**
	 * Método responsável por excluir parcerias entre atividades 
	 * da empresa
	 * 
	 * @param codigoAtividade
	 * @param codigoParceiro
	 * @throws Exception
	 */
	public static void excluiParceria(Integer codigoAtividade, Integer codigoParceiro) throws Exception{

		PreparedStatement cStmt = null;	

		try{
			con = Conexao.getConexao();		

			cStmt = con.prepareCall("{call excluir_parceria(?,?)}");

			cStmt.setInt(1,  codigoAtividade);			
			cStmt.setInt(2,  codigoParceiro);			

			cStmt.execute();

		} finally {
			Conexao.finalizaOperacaoBD(cStmt); 
		}
	}


	/**
	 * Método responsável por excluir todas as entradas
	 * da atividade no sistema
	 * 
	 * @param codigoAtividade
	 * @throws Exception
	 */
	public static void excluiAtividade(Integer codigoAtividade) throws Exception{

		PreparedStatement cStmt = null;	

		try{
			con = Conexao.getConexao();		

			//procedure exclui todas as entradas da atividade
			//no sistema
			cStmt = con.prepareCall("{call EXCLUI_ATIVIDADE(?)}");

			cStmt.setInt(1,  codigoAtividade);			

			cStmt.execute();

		} finally {
			Conexao.finalizaOperacaoBD(cStmt); 
		}
	}



	/**
	 * Método responsável por eliminar solicitações pendentes para as atividades 
	 * da empresa
	 * 
	 * @param codAtvSolicitada
	 * @param codAtvSolicitante
	 * @throws Exception
	 */
	public static void excluiSolicitacao(Integer codAtvSolicitada, Integer codAtvSolicitante) throws Exception{

		pStmt = null;

		String query = "",
		where = "WHERE ";


		List<Object> parametros = new ArrayList<Object>();

		try{

			query = "DELETE FROM TB_SOLICITACOES WHERE ";
			con = Conexao.getConexao();		

			if (codAtvSolicitada != null) {
				where = "COD_ATV_SOLICITADA = ? AND ";
				parametros.add(codAtvSolicitada);
				query = query.concat(where);
			}

			if (codAtvSolicitante != null) {
				where = "COD_ATV_SOLICITANTE = ? ";
				parametros.add(codAtvSolicitante);
				query = query.concat(where);
			}

			if (query.endsWith("AND "))
				query = query.substring(0, query.lastIndexOf("AND "));


			pStmt = con.prepareStatement(query);

			for (int i = 0; i < parametros.size(); i++) {
				pStmt.setObject(i+1, parametros.get(i));
			}			

			pStmt.execute();

		} finally {
			Conexao.finalizaOperacaoBD(pStmt, rs); 
		}
	}


	/**
	 * Atualiza código dos parceiros e elimina ligação do parceiro
	 * atual com a atividade solicitada
	 * 
	 * @param codigoAtividadeSolicitada
	 * @param codigoAtividadeSolicitante
	 * @param codigoParceiroAtual
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static void editaParceria(Integer codigoAtividadeSolicitada, Integer codigoAtividadeSolicitante, 
			Integer codigoParceiroAtual) throws Exception{

		PreparedStatement cStmt = null;	

		try{
			con = Conexao.getConexao();		

			cStmt = con.prepareCall("{call tratar_parceria(?,?,?)}");

			cStmt.setInt(1,  codigoAtividadeSolicitada);			
			cStmt.setInt(2,  codigoAtividadeSolicitante);			
			cStmt.setInt(3,  codigoParceiroAtual);

			cStmt.execute();

		} finally {
			Conexao.finalizaOperacaoBD(cStmt); 
		}
	}



	/**
	 * Interface pública para busca de atividades pela empresa
	 * @param codigo
	 * @return
	 * @throws Exception
	 */
	public static HashMap<Integer,Atividade> selecionarAtividadesEmpresa(Integer codigoEmpresa) throws Exception {
		//código da empresa como parâmetro
		return selecionarAtividades(null, null, codigoEmpresa, null);
	}	

	/**
	 * Interface pública para busca de atividades pelo viajante
	 * @param codigoViajante
	 * @return
	 * @throws Exception
	 */
	public static HashMap<Integer,Atividade> selecionarAtividadesViajante(Integer codigoViajante) throws Exception {
		//código do viajante como parâmetro
		return selecionarAtividades(null, null, null, codigoViajante);
	}	

	/**
	 * Interface pública para busca de atividades pelo seu id
	 * @param codigoAtividade
	 * @return
	 * @throws Exception
	 */
	public static Atividade selecionarPorId(Integer codigoAtividade) throws Exception{

		//utilizo código da atividade como parâmetro de busca e invoco método de seleção
		//extraindo atividade da coleção para objeto de retorno baseado no seu indice
		Atividade  atv = selecionarAtividades(null, codigoAtividade, null, null).get(codigoAtividade);

		return atv;
	}


	/**
	 * Método responsável pela seleção das atividades cadastradas no sistema
	 * código passado como parâmetro determina entidade que está invocando-o
	 * e consequentemente o tipo de filtro/concatenação da query  
	 * 
	 * @param nomeAtividade
	 * @param codigoAtividade 
	 * @param codigoEmpresa
	 * @param codigoViajante	 
	 * 
	 * @return
	 * @throws Exception
	 */
	private static HashMap<Integer, Atividade> selecionarAtividades(String nomeAtividade, Integer codigoAtividade, 
			Integer codigoEmpresa, Integer codigoViajante) throws Exception {

		HashMap<Integer, Atividade>listaAtividade  =  new HashMap<Integer, Atividade>();

		Object filtroBusca = null;

		try{
			//invoca método de conexão
			con = Conexao.getConexao();

			String query = 
				" SELECT * FROM TB_ATIVIDADE INNER JOIN TB_CATEGORIA " +
				" ON TB_ATIVIDADE.COD_CATEGORIA =  TB_CATEGORIA.COD_CATEGORIA ";

			//caso a busca seja pelo nome da atividade 
			if(nomeAtividade != null){

				//UTILIZA UPPER PARA IGNORAR CASE SENSITIVE
				query = query.concat(" WHERE UPPER(NOME_ATIVIDADE) = UPPER(?) ");
				filtroBusca = nomeAtividade;

			}

			//caso busca seja efetuada pelo id da atividade
			else if(codigoAtividade != null){
				query = query.concat(" WHERE COD_ATIVIDADE = ?");

				filtroBusca = codigoAtividade;
			}

			//caso se trate da busca efetuada por uma empresa, concatena o filtro referente ao 
			//código da empresa
			else if(codigoEmpresa != null){

				query = query.concat(" WHERE TB_ATIVIDADE.CODIGO_EMPRESA = ? ");

				filtroBusca = codigoEmpresa;
			}

			//caso se trate da busca efetuada por um viajante, concatena o filtro referente ao 
			//código da atividade
			else if(codigoViajante != null){

				// CAMPOS TABELA FAVORITOS
				query= query.concat(
						" INNER JOIN TB_FAVORITOS ON TB_ATIVIDADE.COD_ATIVIDADE = " +
						" TB_FAVORITOS.COD_ATIVIDADE_FK "+
				" WHERE TB_FAVORITOS.COD_VIAJANTE_FK = ? ");

				//filtra a busca pelo código do viajante
				filtroBusca = codigoViajante;			
			}

			//ordena pela data de cadastro
			query = query.concat(" ORDER BY DATA_CADASTRO ");

			//executa query no banco de dados utilizando a string sql e o parâmetro enviado
			pStmt = con.prepareStatement(query);

			pStmt.setObject(1, filtroBusca);

			rs = pStmt.executeQuery();

			// Caso existam dados...	
			while(rs.next()){

				//Resgata registros e insere em objeto atividade
				Atividade atv = new Atividade();
				
				atv.setCodigoAtividade      	 (rs.getInt   ("cod_atividade"));
				atv.getCategoria().setCodigo	 (rs.getInt   ("cod_categoria"));
				atv.setCodigoEmpresa         	 (rs.getInt   ("codigo_empresa"));
				atv.getCategoria().setNome       (rs.getString("nome_categoria"));
				atv.setNomeAtividade        	 (rs.getString("nome_atividade"));
				atv.setDescricao	        	 (rs.getString("desc_atv"));
				atv.setPreco          	    	 (rs.getString("custo"));
				atv.getDataInicial().setDataBR 	 (rs.getString("per_disp_ini"));
				atv.getDataFinal().setDataBR 	 (rs.getString("per_disp_fim"));
				atv.getEndereco().setLogradouro	 (rs.getString("logradouro"));
				atv.getEndereco().setNumero		 (rs.getString("numero"));
				atv.getEndereco().setBairro		 (rs.getString("bairro"));
				atv.getEndereco().setComplemento (rs.getString("complemento"));
				atv.getEndereco().setCep		 (rs.getString("cep"));
				atv.getDataInicial().setHoraBR   (rs.getString("hora_func_ini"));
				atv.getDataFinal().setHoraBR 	 (rs.getString("hora_func_fim"));
				atv.getDataInicial().setDiaSemana(rs.getString("dia_sem_ini"));			
				atv.getDataFinal().setDiaSemana  (rs.getString("dia_sem_fim"));
				atv.setImagem		        	 (rs.getString("imagem"));
				atv.getDataCadastro().setDataBR	 (rs.getString("data_cadastro"));
				atv.setCodigoParceiro            (rs.getInt   ("codigo_parceiro"));

				//guarda objetos atividade em coleção, utilizando 
				//código da atividade como indice 
				listaAtividade.put(atv.getCodigoAtividade(), atv);
			}

		}finally{
			Conexao.finalizaOperacaoBD(pStmt, rs); 
		}

		return listaAtividade;
	}



	public static List<IAtividade> selecionarSolicitacoes(Integer codigoAtividade, Integer codigoEmpresa) throws Exception {

		List<IAtividade> solicitacoes  =  new ArrayList<IAtividade>();

		try{

			//invoca método de conexão			
			con = Conexao.getConexao();

			int filtroBusca = 0;

			String query = 
				" SELECT * FROM TB_ATIVIDADE INNER JOIN TB_CATEGORIA ON TB_ATIVIDADE.COD_CATEGORIA = TB_CATEGORIA.COD_CATEGORIA "+ 
				" INNER JOIN TB_SOLICITACOES ON TB_ATIVIDADE.COD_ATIVIDADE = TB_SOLICITACOES.COD_ATV_SOLICITANTE WHERE ";			

			//verifica o filtro que está sendo utilizando na busca
			if(codigoAtividade != null){

				query = query.concat("TB_SOLICITACOES.COD_ATV_SOLICITADA = ? ");				
				filtroBusca = codigoAtividade;

			}else if(codigoEmpresa!=null){	

				query = query.concat("TB_SOLICITACOES.COD_EMPRESA_SOLICITADA = ? ");
				filtroBusca = codigoEmpresa;				
			}

			//executa query no banco de dados utilizando a string sql e o parâmetro enviado
			pStmt = con.prepareStatement(query);			

			pStmt.setInt(1, filtroBusca);			

			query = query.concat(" ORDER BY DATA_SOLICITACAO ");

			rs = pStmt.executeQuery();

			// Caso existam dados...	
			while(rs.next()){

				//Resgata registros e insere em objeto atividade
				Atividade atv = new Atividade();

				atv.setCodigoAtividade      	 (rs.getInt   ("cod_atividade"));
				atv.getCategoria().setCodigo	 (rs.getInt   ("cod_categoria"));
				atv.setCodigoEmpresa         	 (rs.getInt   ("codigo_empresa"));
				atv.getCategoria().setNome       (rs.getString("nome_categoria"));
				atv.setNomeAtividade        	 (rs.getString("nome_atividade"));
				atv.setDescricao	        	 (rs.getString("desc_atv"));
				atv.setPreco          	    	 (rs.getString("custo"));
				atv.getDataInicial().setDataBR 	 (rs.getString("per_disp_ini"));
				atv.getDataFinal().setDataBR 	 (rs.getString("per_disp_fim"));
				atv.getEndereco().setLogradouro	 (rs.getString("logradouro"));
				atv.getEndereco().setNumero		 (rs.getString("numero"));
				atv.getEndereco().setBairro		 (rs.getString("bairro"));
				atv.getEndereco().setComplemento (rs.getString("complemento"));
				atv.getEndereco().setCep		 (rs.getString("cep"));
				atv.getDataInicial().setHoraBR   (rs.getString("hora_func_ini"));
				atv.getDataFinal().setHoraBR 	 (rs.getString("hora_func_fim"));
				atv.getDataInicial().setDiaSemana(rs.getString("dia_sem_ini"));			
				atv.getDataFinal().setDiaSemana  (rs.getString("dia_sem_fim"));
				atv.setImagem		        	 (rs.getString("imagem"));
				atv.getDataCadastro().setDataBR	 (rs.getString("data_cadastro"));
				atv.setCodigoParceiro            (rs.getInt   ("codigo_parceiro"));

				//guarda objetos atividade em coleção
				solicitacoes.add(atv);
			}

		}finally{
			Conexao.finalizaOperacaoBD(pStmt, rs); 
		}

		return solicitacoes;
	}

	public static Atividade selecionar(String nomeAtividade, Integer codigoAtividade) throws Exception {
		Atividade atividade = null;
		String query = "",
		where = "WHERE ";
		List<Object> parametros = new ArrayList<Object>();

		try {
			con = Conexao.getConexao();

			query = 
				" SELECT * FROM TB_ATIVIDADE INNER JOIN TB_CATEGORIA ON TB_ATIVIDADE.COD_CATEGORIA = TB_CATEGORIA.COD_CATEGORIA ";

			//define o filtro de busca
			if (nomeAtividade != null) {
				where = where.concat("UPPER(NOME_ATIVIDADE) = UPPER(?) AND ");
				query = query.concat(where);
				parametros.add(nomeAtividade);
			}

			if (codigoAtividade != null) {
				where = where.concat("COD_ATIVIDADE = ? AND ");
				query = query.concat(where);
				parametros.add(codigoAtividade);
			}

			if (where.endsWith("AND "))
				query = query.substring(0, query.lastIndexOf("AND "));

			pStmt = con.prepareStatement(query);

			for (int i = 0; i < parametros.size(); i++) {
				pStmt.setObject(i+1, parametros.get(i));
			}

			rs = pStmt.executeQuery();

			if(rs.next()){

				//Resgata registros e insere em objeto atividade
				atividade = new Atividade();

				atividade.setCodigoAtividade      	 (rs.getInt   ("COD_ATIVIDADE"));
				atividade.getCategoria().setCodigo	 (rs.getInt   ("COD_CATEGORIA"));
				atividade.setCodigoEmpresa         	 (rs.getInt   ("CODIGO_EMPRESA"));
				atividade.getCategoria().setNome     (rs.getString("NOME_CATEGORIA"));
				atividade.setNomeAtividade        	 (rs.getString("NOME_ATIVIDADE"));
				atividade.setDescricao	        	 (rs.getString("DESC_ATV"));
				atividade.setPreco          	     (rs.getString("CUSTO"));
				atividade.getDataInicial().setDataBR (rs.getString("PER_DISP_INI"));
				atividade.getDataFinal().setDataBR 	 (rs.getString("PER_DISP_FIM"));
				atividade.getEndereco().setLogradouro(rs.getString("LOGRADOURO"));
				atividade.getEndereco().setNumero	 (rs.getString("NUMERO"));
				atividade.getEndereco().setBairro	 (rs.getString("BAIRRO"));
				atividade.getEndereco().setComplemento(rs.getString("COMPLEMENTO"));
				atividade.getEndereco().setCep		 (rs.getString("CEP"));
				atividade.getDataInicial().setHoraBR (rs.getString("HORA_FUNC_INI"));
				atividade.getDataFinal().setHoraBR 	 (rs.getString("HORA_FUNC_FIM"));
				atividade.getDataInicial().setDiaSemana(rs.getString("DIA_SEM_INI"));			
				atividade.getDataFinal().setDiaSemana(rs.getString("DIA_SEM_FIM"));
				atividade.setImagem		        	 (rs.getString("IMAGEM"));
				atividade.getDataCadastro().setDataBR(rs.getString("DATA_CADASTRO"));
				atividade.setCodigoParceiro			 (rs.getInt   ("CODIGO_PARCEIRO"));
			}
		} finally {
			Conexao.finalizaOperacaoBD(pStmt, rs);
		}


		return atividade;
	}


	/**
	 * Inserção de uma atividade
	 * @param codigoEmpresa
	 * @param atividade
	 * @throws Exception
	 */
	public static void inserir(Atividade atividade) throws Exception{		

		PreparedStatement cStmt = null;	

		try{
			con = Conexao.getConexao();		

			cStmt = con.prepareCall("{call insere_atividade(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

			cStmt.setInt   (1,  atividade.getCategoria().getCodigo());			
			cStmt.setString(2,  atividade.getNomeAtividade());			
			cStmt.setString(3,  atividade.getDescricao());			
			cStmt.setString(4,  atividade.getPreco());
			cStmt.setString(5,  atividade.getDataInicial().getDataBD());
			cStmt.setString(6,  atividade.getDataFinal().getDataBD());			
			cStmt.setString(7,  atividade.getEndereco().getLogradouro());
			cStmt.setString(8,  atividade.getEndereco().getNumero());
			cStmt.setString(9,  atividade.getEndereco().getComplemento());
			cStmt.setString(10, atividade.getEndereco().getBairro());
			cStmt.setString(11, atividade.getEndereco().getCep());			
			cStmt.setString(12, atividade.getDataInicial().getHoraBD());
			cStmt.setString(13, atividade.getDataFinal().getHoraBD());    
			cStmt.setString(14, atividade.getDataInicial().getDiaSemana());
			cStmt.setString(15, atividade.getDataFinal().getDiaSemana());
			cStmt.setString(16, atividade.getImagem());	
			cStmt.setInt   (17, atividade.getCodigoEmpresa());

			cStmt.execute();

		} finally{
			Conexao.finalizaOperacaoBD(cStmt); 
		}
	}

	/**
	 * método responsável por atualizar atividades 
	 * 
	 * @param atividade
	 * @throws Exception
	 */
	public static void atualizar(Atividade atividade) throws Exception{

		CallableStatement cStmt = null;	

		try{
			con = Conexao.getConexao();

			cStmt = con.prepareCall("{call atualiza_atividade(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

			cStmt.setInt   (1,  atividade.getCodigoAtividade());			
			cStmt.setInt   (2,  atividade.getCategoria().getCodigo());			
			cStmt.setString(3,  atividade.getNomeAtividade());			
			cStmt.setString(4,  atividade.getDescricao());			
			cStmt.setString(5,  atividade.getPreco());
			cStmt.setString(6,  atividade.getDataInicial().getDataBD());
			cStmt.setString(7,  atividade.getDataFinal().getDataBD());			
			cStmt.setString(8,  atividade.getEndereco().getLogradouro());
			cStmt.setString(9,  atividade.getEndereco().getNumero());
			cStmt.setString(10, atividade.getEndereco().getComplemento());
			cStmt.setString(11, atividade.getEndereco().getBairro());
			cStmt.setString(12, atividade.getEndereco().getCep());			
			cStmt.setString(13, atividade.getDataInicial().getHoraBD());
			cStmt.setString(14, atividade.getDataFinal().getHoraBD());    
			cStmt.setString(15, atividade.getDataInicial().getDiaSemana());
			cStmt.setString(16, atividade.getDataFinal().getDiaSemana());
			cStmt.setString(17, atividade.getImagem());	

			cStmt.execute();

		} finally {
			Conexao.finalizaOperacaoBD(cStmt); 
		}
	}


//	public static HashMap<Integer, Atividade> buscarPorNome(String nomeAtividade) throws Exception {
//	HashMap<Integer, Atividade> listaAtividade = new HashMap<Integer, Atividade>();
//	String query = "",
//	where = "WHERE ";


//	List<Object> parametros = new ArrayList<Object>();

//	try {
//	con = Conexao.getConexao();

//	query = "SELECT COD_ATIVIDADE," +
//	"NOME_ATIVIDADE, " +
//	"DESC_ATV, " +
//	"CUSTO, " +
//	"PER_DISP_INI, " +
//	"PER_DISP_FIM, " +
//	"LOGRADOURO, " +
//	"NUMERO, "+
//	"COMPLEMENTO, " +
//	"BAIRRO, " +
//	"CEP, " +
//	"HORA_FUNC_INI, " +
//	"HORA_FUNC_FIM, " +
//	"DIA_SEM_INI, " +
//	"DIA_SEM_FIM, " +
//	"IMAGEM, " +
//	"DATA_CADASTRO, " +
//	"TB_CATEGORIA.COD_CATEGORIA, " +
//	"TB_CATEGORIA.NOME_CATEGORIA, " +
//	"TB_CATEGORIA.DESCRICAO_CATEGORIA, " +
//	"TB_CATEGORIA.TIPO_CATEGORIA " +
//	"FROM TB_ATIVIDADE " +
//	"INNER JOIN TB_CATEGORIA " +
//	"ON TB_ATIVIDADE.COD_CATEGORIA = TB_CATEGORIA.COD_CATEGORIA ";

//	if (nomeAtividade != null) {
//	where = where.concat("UPPER(NOME_ATIVIDADE) LIKE UPPER(?) AND ");
//	parametros.add("%"+nomeAtividade+"%");
//	query = query.concat(where);
//	}

//	if (where.endsWith("AND "))
//	query = query.substring(0, query.lastIndexOf("AND"));

//	pStmt = con.prepareStatement(query);

//	for (int i = 0; i < parametros.size(); i++){
//	pStmt.setObject(i+1, parametros.get(i));
//	}

//	rs = pStmt.executeQuery();

//	while(rs.next()){

//	//Resgata registros e insere em objeto atividade
//	Atividade atividade = new Atividade();

//	atividade.setCodigoAtividade      	 (rs.getInt   ("COD_ATIVIDADE"));
//	atividade.getCategoria().setCodigo	 (rs.getInt   ("COD_CATEGORIA"));
//	atividade.getCategoria().setNome     (rs.getString("NOME_CATEGORIA"));
//	atividade.setNomeAtividade        	 (rs.getString("NOME_ATIVIDADE"));
//	atividade.setDescricao	        	 (rs.getString("DESC_ATV"));
//	atividade.setPreco          	     (rs.getString("CUSTO"));
//	atividade.getDataInicial().setDataBR (rs.getString("PER_DISP_INI"));
//	atividade.getDataFinal().setDataBR 	 (rs.getString("PER_DISP_FIM"));
//	atividade.getEndereco().setLogradouro(rs.getString("LOGRADOURO"));
//	atividade.getEndereco().setNumero	 (rs.getString("NUMERO"));
//	atividade.getEndereco().setBairro	 (rs.getString("BAIRRO"));
//	atividade.getEndereco().setComplemento(rs.getString("COMPLEMENTO"));
//	atividade.getEndereco().setCep		 (rs.getString("CEP"));
//	atividade.getDataInicial().setHoraBR (rs.getString("HORA_FUNC_INI"));
//	atividade.getDataFinal().setHoraBR 	 (rs.getString("HORA_FUNC_FIM"));
//	atividade.getDataInicial().setDiaSemana(rs.getString("DIA_SEM_INI"));			
//	atividade.getDataFinal().setDiaSemana(rs.getString("DIA_SEM_FIM"));
//	atividade.setImagem		        	 (rs.getString("IMAGEM"));
//	atividade.getDataCadastro().setDataBR(rs.getString("DATA_CADASTRO"));

//	listaAtividade.put(atividade.getCodigoAtividade() , atividade);
//	}
//	} finally {
//	Conexao.finalizaOperacaoBD(pStmt, rs);
//	}
//	return listaAtividade;
//	}
}
