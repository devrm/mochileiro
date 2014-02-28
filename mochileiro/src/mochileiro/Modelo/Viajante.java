package mochileiro.Modelo;

import java.util.HashMap;

import mochileiro.Modelo.DAO.AtividadeDAO;
import mochileiro.Modelo.DAO.ViajanteDAO;
import mochileiro.Util.Data;


/**
 * @Autor David
 * @Projeto mochileiro
 * @Data 12/06/2009
 * @Hora 14:23:21
 */
public class Viajante {

	private String   		nome;
	private String   		cpf;
	private String   		email;
	private Usuario  		usuario;
	private Endereco 		endereco;
	private String   		ddd;
	private String   		telefone;
	private Data     		dataCadastro;
	
	private HashMap<Integer,Atividade> listaAtividade;
	
	private Rota rotasCadastradas;

	/**
	 * Construtor utilizado na busca das informacoes contidas no objeto, por isto, todas as entidades
	 * que obrigatoriamente possuam as informacoes da entidade devem ser inicializados.
	 */
	public Viajante() {
		usuario      = new Usuario();
		endereco 	 = new Endereco();
		dataCadastro = new Data();
	}
	

	public Viajante(Usuario usuario) {
		endereco 	 = new Endereco();
		dataCadastro = new Data();
		this.usuario = usuario;
	}
	
	/**
	 * Método responsável por carregar rotas cadastradas
	 * pelo viajante
	 * @throws Exception 
	 */
	public Rota selecionarRota(Integer codigoViajante) throws Exception{
		
		//regata tabela com rotas cadastradas
		rotasCadastradas = ViajanteDAO.selecionaRotas(codigoViajante);			
		
			//caso possua código de rota, então resgata objetos 
			if(rotasCadastradas != null){
			//resgata da base a atividade que compreende o código origem da rota
			Atividade tempOrigem = AtividadeDAO.selecionarPorId(rotasCadastradas.getCodigoOrigem());
			
			//caso tenha sido cadastrado endereço do viajante como código origem
			//não existirá retorno, então seta zero para os campos chave
			if(tempOrigem == null){
				tempOrigem = new Atividade();
				tempOrigem.setCodigoAtividade(0);
			} 
			
			//seta na rota o objeto encontrado
			rotasCadastradas.setAtividadeOrigem(tempOrigem);
			
			//resgata a atividade que corresponde ao código de destino
			Atividade tempDestino = AtividadeDAO.selecionarPorId(rotasCadastradas.getCodigoDestino());
			
			//seta na rota o objeto encontrado
			rotasCadastradas.setAtividadeDestino(tempDestino);					
		}
		
		return rotasCadastradas;
	}
	
	/**
	 * Método responsável por gravar a rota selecionada pelo viajante para
	 * posterior exibição
	 * 
	 * @param codigoViajante
	 * @param codigoOrigem
	 * @param codigoDestino
	 * @throws Exception
	 */
	public void salvarRota(Integer codigoViajante, Integer codigoOrigem, Integer codigoDestino) throws Exception{
		
		ViajanteDAO.salvaRota(codigoViajante, codigoOrigem, codigoDestino);
			
	}
	

	/**
	 * Seleciona o viajante desejado passando o codigo(id) como filtro
	 */
	public Viajante selecionarPorId(int id) throws Exception {
		return ViajanteDAO.selecionar(new Integer(id), null);
	}
	
	/**
	 * Seleciona o viajante desejado passando o cpf como filtro, verifica
	 * por duplicidade no campo
	 */
	public boolean selecionarPorCpf(String cpf) throws Exception {
		
		boolean isDuplicado = false;
		//retorna viajante encontrado na busca para objeto temporário
		Viajante temp = ViajanteDAO.selecionar(null, cpf);
		
		//caso exista retorno com o filtro pesquisado, 
		//verifico se viajante que invocou a pesquisa
		//possui o mesmo id do viajante retornado
		if(temp != null){			
			//caso retorne false, trata-se de registros diferentes
			boolean isCodigosIguais = temp.getUsuario().getCodigo().equals(this.getUsuario().getCodigo());
			
			//atualizo variável invertendo a lógica da resposta
			isDuplicado = isCodigosIguais == false ? true : false;
			
		}
		
		return isDuplicado;
	}
	
	/**
	 * Insere objeto na lista de favoritos do viajante
	 * @param atividade
	 * @throws Exception 
	 */
	public void inserirFavoritos(Integer codigoAtividade, Integer codigoViajante) throws Exception {
		
		ViajanteDAO.insereFav(codigoAtividade, codigoViajante);
	}
	
	/**
	 * elimina atividades da lista do viajantes
	 * @param codigoAtividade
	 * @param codigoViajante
	 * @throws Exception
	 */
	public void excluiFavoritos(Integer codigoAtividade, Integer codigoViajante) throws Exception{
		ViajanteDAO.excluiFav(codigoAtividade, codigoViajante);
	}
	
	/**
	 * Insere as informacoes relacionadas ao viajante
	 */
	public void inserir(Viajante viajante) throws Exception{		
		ViajanteDAO.inserir(viajante);
	}
	
	/**
	 * Método responsável pela atualização da entidade viajante
	 */
	public void atualizar(Viajante viajante) throws Exception{
		ViajanteDAO.atualizar(viajante);
	}
	
	/**
	 * Método responsável por resgatar atividades cadastradas na 
	 * lista de favoritos do viajante tendo código do viajante como filtro
	 */
	public void selecionarAtividadesFavoritas(int codVajante) throws Exception {
		
		this.listaAtividade = AtividadeDAO.selecionarAtividadesViajante(codVajante);
	}
	
	
	//getters e setters
	public String getNome() {
		return nome;
	}


	public void setNome(String nome) {
		this.nome = nome;
	}


	public String getCpf() {
		return cpf;
	}


	public void setCpf(String cpf) {
		this.cpf = cpf;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public Usuario getUsuario() {
		return usuario;
	}


	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}


	public Endereco getEndereco() {
		return endereco;
	}


	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}


	public Data getDataCadastro() {
		return dataCadastro;
	}


	public void setDataCadastro(Data dataCadastro) {
		this.dataCadastro = dataCadastro;
	}


	public HashMap<Integer,Atividade> getListaAtividade() {
		return listaAtividade;
	}


	public String getDDD() {
		return ddd;
	}


	public void setDDD(String ddd) {
		this.ddd = ddd;
	}


	public String getTelefone() {
		return telefone;
	}


	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public Rota getRotasCadastradas() {
		return rotasCadastradas;
	}

}