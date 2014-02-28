package mochileiro.Modelo;

import java.util.HashMap;
import java.util.List;

import mochileiro.Modelo.DAO.AtividadeDAO;
import mochileiro.Modelo.DAO.EmpresaDAO;
import mochileiro.Util.Data;


/**
 * @Autor rams
 * @Projeto mochileiro
 * @Data 12/06/2009 @Hora 14:23:21
 */
public class Empresa {

	private String   nomeEmpresa;
	private String   cnpj;
	private String   dddTel;
	private Endereco endereco;
	private String   telefone;
	private String   email;
	private String   homePage;
	private Usuario  usuario;
	private Data	 dataCadastro;	

	//utilizado no relat�rio
	private int 	 adicionada;

	private HashMap<Integer,Atividade> listaAtividade;

	/**
	 * Construtor utilizado quando a empresa instanciada for oriunda de uma busca realizada.
	 * Por isto, e necessario que exista um usuario em "Branco" para que os dados de usuario
	 * possam ser alimentados do resultado da busca.
	 */
	public Empresa(){
		dataCadastro = new Data();
		endereco	 = new Endereco();
		usuario      = new Usuario();
	}

	/**
	 * Construtor usado para insercao de uma nova empresa, passando os dados de usuario desejado
	 * para efetuar futuras validacoes. (Utilizado no MBEmpresa)
	 */
	public Empresa(Usuario usuario){
		dataCadastro = new Data();
		endereco	 = new Endereco();
		this.usuario = usuario;
	}
	
/*	*//**
	 * Contrutor utilizado para carregar atividades com solicita��es pendentes
	 * @throws Exception 
	 *//*
	public Empresa(Usuario usuario, Integer codigo) throws Exception{
		dataCadastro = new Data();
		endereco	 = new Endereco();
		this.usuario = usuario;
		
		//carrega informa��es da empresa
		this.selecionarPorId(this.usuario.getCodigo());
		
		//carrega atividades
		this.selecionarAtividades(codigo);
		
	}
*/	
	
	
	/**
	 * Valida cnpj duplicado
	 * @param codigo
	 * @param cnpj
	 * @return
	 * @throws Exception
	 */
	public boolean validarCnpjDuplicado(Integer codigo, String cnpj) throws Exception {
		boolean isDuplicado = false;
		Empresa empresa = validarCNPJDuplicado(cnpj);
		if (codigo != null) {
			if (codigo != empresa.getUsuario().getCodigo() ) {
				if (empresa.getCnpj().equals(cnpj))
					isDuplicado = true;
			}
		}
		else {
			isDuplicado = (usuario != null ? true : false);
		}
		return isDuplicado;
	}
	
	
	/**
	 * invoca m�todo DAO para listar atividades 
	 * que ser�o exibidas nos relat�rios de Resumo 
	 * @param codigoEmpresa
	 * @throws Exception 
	 */
	public void selecionaRelatorioResumo(Integer codigoEmpresa) throws Exception {		
		
		this.listaAtividade = AtividadeDAO.selecionarAtividadesResumo(codigoEmpresa);		
	}	
	
	
	
	/**
	 * invoca DAO para listar empresas com parceiros
	 * informa��o ser� utilizada na cria��o de relat�rio
	 * de parcerias
	 * 
	 * @return
	 * @throws Exception 
	 */
	public List<Empresa>  selecionaRelatorioParceria() throws Exception{
			
		return EmpresaDAO.selecionarRelatorioParceria();
	}

	/**
	 * Resgata atividades cadastras no sistema tendo como par�metro de busca
	 * ID da empresa
	 * @param id
	 * @throws Exception 
	 */
	public void selecionarAtividades(int codigoEmpresa) throws Exception{

		this.listaAtividade = AtividadeDAO.selecionarAtividadesEmpresa(codigoEmpresa);
	}	


	/**
	 * M�todo respons�vel por atualizar as informacoes da entidade empresa
	 */
	public void atualizar(Empresa empresa) throws Exception{
		EmpresaDAO.atualizar(empresa);
	}

	/**
	 * Insere nova empresa no sistema
	 */
	public void inserir(Empresa empresa) throws Exception
	{
		EmpresaDAO.inserir(empresa);
	}

	/**
	 * Seleciona a empresa desejada por cnpj
	 */
	public Empresa validarCNPJDuplicado(String cnpj) throws Exception {

		//recebe retorno de sele��o em objeto tempor�rio
		Empresa temp = EmpresaDAO.selecionar(this.getUsuario().getCodigo(), cnpj, null);

		//verifica se ids s�o iguais, caso contr�rio retorna objeto populado
		if(temp != null){
			if(temp.getUsuario().getCodigo().equals(this.getUsuario().getCodigo()))
				temp = null;			
		}

		return temp;
	}	

	/**
	 * Seleciona a empresa pelo codigo
	 */
	public Empresa selecionarPorId(int codigoEmpresa) throws Exception {
		return EmpresaDAO.selecionar(codigoEmpresa, null, null);	
	}
	
	
	/**
	 * Seleciona a empresa pelo nome
	 * @param nomeEmpresa
	 * @return
	 * @throws Exception
	 */
	public Empresa validarRazaoSocialDuplicada(String nomeEmpresa) throws Exception{

		//recebe retorno de sele��o em objeto tempor�rio
		Empresa temp = EmpresaDAO.selecionar(null, null, nomeEmpresa);

		//verifica se ids s�o iguais, caso contr�rio retorna objeto populado
		if(temp != null){
			if(temp.getUsuario().getCodigo().equals(this.getUsuario().getCodigo()))
				temp = null;			
		}

		return temp;
	}


	//getters e setters
	public String getNomeEmpresa() {
		return nomeEmpresa;
	}

	public void setNomeEmpresa(String nomeEmpresa) {
		this.nomeEmpresa = nomeEmpresa;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public String getDddTel() {
		return dddTel;
	}

	public void setDddTel(String dddTel) {
		this.dddTel = dddTel;
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getHomePage() {
		return homePage;
	}

	public void setHomePage(String homePage) {
		this.homePage = homePage;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public Data getDataCadastro() {
		return dataCadastro;
	}

	public HashMap<Integer,Atividade> getListaAtividade() {
		return listaAtividade;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public int getAdicionada() {
		return adicionada;
	}

	public void setAdicionada(int adicionada) {
		this.adicionada = adicionada;
	}
}
