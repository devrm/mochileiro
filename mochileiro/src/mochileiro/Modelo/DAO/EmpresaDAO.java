package mochileiro.Modelo.DAO;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import mochileiro.Modelo.Empresa;
import mochileiro.Modelo.Usuario;
import mochileiro.Util.Conexao;

/**
 * @Autor rams
 * @Projeto mochileiro
 * @Data 12/06/2009 @Hora 21:39:28
 */
public class EmpresaDAO {

	private static Connection 		 con   = null;
	private static PreparedStatement pStmt = null;
	private static ResultSet         rs    = null;
	
	/**
	 * Insere as informacoes da empresa
	 */
	public static void inserir(Empresa empresa) throws Exception{
		
		CallableStatement cStmt = null;	
		
		try{
			con = Conexao.getConexao();
			
			cStmt = con.prepareCall("{call insere_empresa(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

			cStmt.setString(1,  empresa.getNomeEmpresa());
			cStmt.setString(2,  empresa.getEndereco().getLogradouro());
			cStmt.setString(3,  empresa.getEndereco().getNumero());
			cStmt.setString(4,  empresa.getEndereco().getComplemento());
			cStmt.setString(5,  empresa.getEndereco().getBairro());
			cStmt.setString(6,  empresa.getEndereco().getCidade());
			cStmt.setInt   (7,  empresa.getEndereco().getUf());
			cStmt.setString(8,  empresa.getEndereco().getCep());
			cStmt.setString(9,  empresa.getDddTel()+empresa.getTelefone());
			cStmt.setString(10, empresa.getCnpj());
			cStmt.setString(11, empresa.getEmail());
			cStmt.setString(12, empresa.getHomePage());
			cStmt.setString(13, empresa.getUsuario().getNickName());
			cStmt.setString(14, empresa.getUsuario().getSenha());

			cStmt.execute();
			
		} finally{
			Conexao.finalizaOperacaoBD(cStmt);
		}
	}


	/**
	 * Atualiza as informacoes da empresa
	 */
	public static void atualizar(Empresa empresa) throws Exception{		

		CallableStatement cStmt = null;
		
		try{
			
			con = Conexao.getConexao();
			
			cStmt = con.prepareCall("{call atualiza_empresa(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

			cStmt.setString(1,  empresa.getNomeEmpresa());
			cStmt.setString(2,  empresa.getEndereco().getLogradouro());
			cStmt.setString(3,  empresa.getEndereco().getNumero());
			cStmt.setString(4,  empresa.getEndereco().getComplemento());
			cStmt.setString(5,  empresa.getEndereco().getBairro());
			cStmt.setString(6,  empresa.getEndereco().getCidade());
			cStmt.setInt   (7,  empresa.getEndereco().getUf());
			cStmt.setString(8,  empresa.getEndereco().getCep());
			cStmt.setString(9,  empresa.getDddTel()+empresa.getTelefone());
			cStmt.setString(10, empresa.getCnpj());
			cStmt.setString(11, empresa.getEmail());
			cStmt.setString(12, empresa.getHomePage());
			cStmt.setString(13, empresa.getUsuario().getNickName().toLowerCase());
			cStmt.setString(14, empresa.getUsuario().getSenha());
			cStmt.setInt   (15, empresa.getUsuario().getCodigo());
			cStmt.setString(16, empresa.getUsuario().getTipo());

			cStmt.execute();
		
		}finally{
			
			Conexao.finalizaOperacaoBD(cStmt);
		}
	}
	
	
	/**
	 * Seleciona a empresa utilizando os filtros desejados, caso algum dos parametros
	 * possua o valor null, o mesmo sera ignorado na busca e nao sera usado como filtro.
	 */
	public static Empresa selecionar(Integer id, String cnpj, String nomeEmpresa) throws Exception {

		List<Object> parametros = new ArrayList<Object>();
		Empresa 	 empresa 	= null;
		String 		 where 		= "";
		
		String query = " SELECT * FROM TB_EMPRESA " +
					   " INNER JOIN TB_USUARIO " +
					   " ON COD_EMPRESA = COD_USUARIO" +
					   " WHERE UPPER (TIPO_USUARIO) = UPPER('"+Usuario.TIPO_USUARIO_EMPRESA+"') AND ";

		try {
			
			if (id != null) {
				where = " COD_EMPRESA = ? AND";
				parametros.add(id);
				query = query.concat(where);
			}
			
			if (cnpj != null) {
				where = " CNPJ = ? AND";
				parametros.add(cnpj);
				query = query.concat(where);
			}
			
			if (nomeEmpresa != null) {
				where = " UPPER(RAZAO_SOCIAL) = UPPER(?) AND";
				parametros.add(nomeEmpresa);
				query = query.concat(where);
			}
			
			con = Conexao.getConexao();
			
			if (! where.equals(""))
				query = query.substring(0, query.lastIndexOf("AND"));
			
			pStmt = con.prepareStatement(query);

			for (int i = 0; i < parametros.size(); i++) {
				pStmt.setObject(i+1, parametros.get(i));
			}
			
			rs = pStmt.executeQuery();
			
			if (rs.next()){
				
				empresa = new Empresa();
				
				empresa.setNomeEmpresa				(rs.getString("RAZAO_SOCIAL"));
				empresa.getEndereco().setLogradouro (rs.getString("LOGRADOURO"));
				empresa.getEndereco().setNumero		(rs.getString("NUMERO"));				
				empresa.getEndereco().setComplemento(rs.getString("COMPLEMENTO"));
				empresa.getEndereco().setBairro		(rs.getString("BAIRRO"));
				empresa.getEndereco().setCidade		(rs.getString("CIDADE"));	
				empresa.getEndereco().setUf			(rs.getInt	 ("ESTADO"));
				empresa.getEndereco().setCep		(rs.getString("CEP"));
				empresa.setTelefone					(rs.getString("TELEFONE"));
				empresa.setCnpj						(rs.getString("CNPJ"));
				empresa.setEmail					(rs.getString("E_MAIL"));
				empresa.setHomePage					(rs.getString("HOME_PAGE"));
				empresa.getDataCadastro().setDataBR (rs.getString("DATA_CADASTRO"));
				empresa.getUsuario().setCodigo		(rs.getInt   ("COD_USUARIO"));
				empresa.getUsuario().setNickName	(rs.getString("USUARIO"));
				empresa.getUsuario().setTipo		(rs.getString("TIPO_USUARIO"));
			}     	

		}finally{
			Conexao.finalizaOperacaoBD(pStmt, rs);
		}
		return empresa; 
	}
	
	
	
	/**
	 * Metodo responsável por resgatar empresas com contagem de parcerias para 
	 * criação de relatório de parcerias
	 * 
	 * @param id
	 * @param cnpj
	 * @param nomeEmpresa
	 * @return
	 * @throws Exception
	 */
	public static List<Empresa> selecionarRelatorioParceria() throws Exception {

		List<Empresa> listaEmpresaParceria = new ArrayList<Empresa>();
		
		Empresa 	 empresa 	= null;
		
		
		//seleção de todas as empresas que possuam codigo de parceria maior que zero
		//realiza contagem dessa ocorrência determinando número de parcerias firmadas
		String query = " SELECT EMP.RAZAO_SOCIAL, EMP.HOME_PAGE, COUNT(ATV.COD_ATIVIDADE) AS PARCEIRAS "+
					   " FROM TB_ATIVIDADE ATV "+
					   " INNER JOIN TB_EMPRESA EMP ON ATV.CODIGO_EMPRESA = EMP.COD_EMPRESA WHERE ATV.CODIGO_PARCEIRO "+
					   " <> 0 GROUP BY EMP.RAZAO_SOCIAL, EMP.HOME_PAGE ";
		

		try {			
	
			con = Conexao.getConexao();			
		
			pStmt = con.prepareStatement(query);
		
			rs = pStmt.executeQuery();
			
			while (rs.next()){
				
				empresa = new Empresa();
				
				empresa.setNomeEmpresa				(rs.getString("RAZAO_SOCIAL"));
				empresa.setHomePage					(rs.getString("HOME_PAGE"));
				empresa.setAdicionada				(rs.getInt	 ("PARCEIRAS"));
				
				listaEmpresaParceria.add(empresa);
			}     	

		}finally{
			Conexao.finalizaOperacaoBD(pStmt, rs);
		}
		return listaEmpresaParceria; 
	}

}







