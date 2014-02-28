package mochileiro.Modelo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mochileiro.Modelo.DAO.AtividadeDAO;
import mochileiro.Util.Data;


/**
 * @author Thiago
 * @projeto mochileiro
 * @Data 15/11/2009 @Hora 18:36:58
 * @Descrição: Classe entidade, implementa interface para visão de parceiro
 */
public class Atividade implements IAtividade{

	public static final String QUALIFICACAO_POSITIVA = "POSITIVA";
	public static final String QUALIFICACAO_NEGATIVA = "NEGATIVA";
	
	private Integer 	codigoAtividade;
	private Integer     codigoParceiro = 0;
	private Integer 	codigoEmpresa;
	private String  	nomeAtividade;
	private String  	descricao;
	private Endereco 	endereco;
	
	private Data 		dataCadastro;
	private Data 		dataInicial;
	private Data		dataFinal;
	
	private Categoria 	categoria;
	
	private String 		Imagem;

	private boolean 	semCusto;
	private String 		preco;

	private String 		comentarios;

	//campos utilizados para formatação na view 
	private String descricaoLista;
	private String nomeLista;
	
	private int qualificacoesPositivas = 0;
	private int qualificacoesNegativas = 0;
	
	private int adicionada;
	
	
	public static final String IMAGEM_PADRAO = "noimage.jpg";

	private ArrayList<Classificacao> listaComentarios;

	ArrayList<IAtividade> solicitacoes;

	private IAtividade parceiro;
	
	public Atividade(){
		
		this.endereco     = new Endereco();
		this.categoria 	  =	new Categoria();
		this.dataCadastro = new Data();
		this.dataInicial  = new Data();
		this.dataFinal 	  = new Data();
	}
	
	/* (non-Javadoc)
	 * @see mochileiro.Modelo.IAtividade#seleciona(int)
	 */
	public Atividade seleciona(int codigoAtividade) throws Exception {
		
		return AtividadeDAO.selecionarPorId(codigoAtividade); 
	}
	
	
	/**
	 * Método responsável por iniciar o processo
	 * de exclusão da atividade
	 * 
	 * @param codigoAtividade
	 * @throws Exception 
	 */
	public void excluiAtividade(int codigoAtividade) throws Exception{
		
		AtividadeDAO.excluiAtividade(codigoAtividade);		
	}
	
	/**
	 * elimina uma parceria firmada
	 * 
	 * @param codAtvSolicitada
	 * @param codAtvSolicitante
	 * @throws Exception
	 */
	public void excluiParceiro(Integer codigoAtividade, Integer codigoParceiro) throws Exception{

		AtividadeDAO.excluiParceria(codigoAtividade, codigoParceiro);
	}

	
	
	
	/**
	 * Exclui solicitações pendentes
	 * 
	 * @param codAtvSolicitada
	 * @param codAtvSolicitante
	 * @throws Exception
	 */
	public void excluiSolicitacoes(Integer codAtvSolicitada, Integer codAtvSolicitante) throws Exception{

		AtividadeDAO.excluiSolicitacao(codAtvSolicitada, codAtvSolicitante);
	}


	/**
	 * Método responsável por gerenciar parcerias
	 * atualiza o código da atividade solicitante na atividade 
	 * solicitada e atualiza parceiro atual eliminando a ligação
	 * existente 
	 * 
	 * @param codAtvSolicitada
	 * @param codAtvNovaParc
	 * @param codParcAtual
	 * @throws Exception
	 */
	public void atualizaParceria(Integer codAtvSolicitada, Integer codAtvNovaParc, Integer codParcAtual) throws Exception{

		AtividadeDAO.editaParceria(codAtvSolicitada, codAtvNovaParc, codParcAtual);
	}

	/**
	 * Carrega solicitações pendentes em coleção com IAtividade
	 * como tipo
	 * @param codigoAtividade
	 * @throws Exception
	 */
	public void selecionaSolicitacoes(Integer codigoAtividade,Integer codigoEmpresa) throws Exception{
		this.setSolicitacoes(AtividadeDAO.selecionarSolicitacoes(codigoAtividade, codigoEmpresa));
	}


	/**
	 * Método responsável por selecionar comentários das atividades
	 * @throws Exception
	 */
	public void selecionaComentarios(Integer codigoAtividade) throws Exception{
		Classificacao cla = new Classificacao();
		listaComentarios = cla.selecionaClassificacao(codigoAtividade);
	}

	public ArrayList<Atividade> buscar(String nome, String logradouro, String bairro, String complemento, 
			String numero, String cep, Integer codigoCategoria,
			Data dataInicio, Data dataFim) throws Exception {
		return AtividadeDAO.buscar(nome, logradouro, bairro, complemento, numero, cep, 
				codigoCategoria, dataInicio, dataFim);
	}


	/**
	 * Método responsável por verificar se nome proposta para 
	 * atividade já se encontra cadastrado no sistema
	 * 
	 * @param aNome
	 * @return boolean
	 * @throws Exception 
	 */
	public boolean selecionarPorNome(String nomeAtividade) throws Exception{
		
		boolean isDuplicado = false;
		
		//busca atividades utilizando nome parâmetro como filtro de busca e armazena
		//resultado em lista temporária		
		HashMap<Integer,Atividade> listaAtividade = AtividadeDAO.selecionarPorNome(nomeAtividade);
		
		for(Atividade temp : listaAtividade.values()){
		
			//verifica se atividade retornada é a atividade em que foi realizada a busca
			if(temp.getCodigoAtividade() != this.getCodigoAtividade())
				isDuplicado = true;
			}			
		
		return isDuplicado;		
	}

	/**
	 * Método responsável por invocar DAO de Cadastro das atividades
	 * realiza verificação de imagem de exibição
	 * @param usuario
	 * @param atividade
	 * @throws Exception
	 */
	public void cadastra(Atividade atividade) throws Exception {	
		
		//caso não exista imagem de exibição, seta padrão
		if(atividade.getImagem() == null )
			atividade.setImagem(IMAGEM_PADRAO);

		AtividadeDAO.inserir(atividade);
	}
	
	/**
	 * Método responsável por selecionar atividade
	 * utiliza iCodAtividade como filtro de busca
	 * 
	 * @param codigoAtividade
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public Atividade selecionarPorId(int codigoAtividade) throws Exception {
		return AtividadeDAO.selecionar(null, new Integer(codigoAtividade)); 
	}
	
	
	/**
	 * Método responsável por invocar DAO de atualização das atividades
	 * realiza verificação de imagem de exibição
	 * @param atividade
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public void atualiza(Atividade atividade) throws Exception {
			AtividadeDAO.atualizar(atividade);
	}
	
	/**
	 * Armazena o total de qualificações positivas e negativas para a atividade
	 */
	public int getTotalQualificacao(){
		
		int total = 0;
		qualificacoesPositivas = 0;
		qualificacoesNegativas = 0;		
		
		if (listaComentarios != null) {
			for (Classificacao comentario : listaComentarios) {
				if (comentario.getNota())
					qualificacoesPositivas++;
				else
					qualificacoesNegativas++;
				
				total++;
			}			
		}
		
		return total;
	}
	
	/**
	 * Método responsável por iniciar o processo de
	 * solicitação de parceria
	 * 
	 * @param solicitante atividade que solicita a parceria
	 * @param solicitada atividade que recebe o convite de parceria
	 * 
	 * @throws Exception
	 */
	public void enviaSolicitacaoParceria(Atividade solicitante,
										 Atividade solicitada) throws Exception {
		
		AtividadeDAO.cadastraSolicitacao(solicitante.getCodigoAtividade(), 
										 solicitada.getCodigoAtividade(),
										 solicitada.getCodigoEmpresa());
	}


	
	//getters e setters
	public Integer getCodigoAtividade() {
		return codigoAtividade;
	}


	public void setCodigoAtividade(Integer codigoAtividade) {
			
		this.codigoAtividade = codigoAtividade;
	}


	public Integer getCodigoEmpresa() {
		return codigoEmpresa;
	}


	public void setCodigoEmpresa(Integer codigoEmpresa) {
		this.codigoEmpresa = codigoEmpresa;
	}


	/* (non-Javadoc)
	 * @see mochileiro.Modelo.IAtividade#getNomeAtividade()
	 */
	public String getNomeAtividade() {
		return nomeAtividade;
	}


	public void setNomeAtividade(String nomeAtividade) {
		this.nomeAtividade = nomeAtividade;
	}


	/* (non-Javadoc)
	 * @see mochileiro.Modelo.IAtividade#getDescricao()
	 */
	public String getDescricao() {
		return descricao;
	}


	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}


	public Categoria getCategoria() {
		return categoria;
	}


	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}


	public String getImagem() {
		return Imagem;
	}


	public void setImagem(String imagem) {
		Imagem = imagem;
	}


	public boolean isSemCusto() {
		return semCusto;
	}


	public void setSemCusto(boolean semCusto) {
		this.semCusto = semCusto;
	}


	public String getPreco() {
		return preco;
	}


	public void setPreco(String preco) {
		this.preco = preco;
	}

	public String getDescricaoLista() {
		return descricaoLista;
	}


	public void setDescricaoLista(String descricaoLista) {
		this.descricaoLista = descricaoLista;
	}


	public String getNomeLista() {
		return nomeLista;
	}


	public void setNomeLista(String nomeLista) {
		this.nomeLista = nomeLista;
	}


	public Endereco getEndereco() {
		return endereco;
	}


	/* (non-Javadoc)
	 * @see mochileiro.Modelo.IAtividade#getDataCadastro()
	 */
	public Data getDataCadastro() {
		return dataCadastro;
	}


	public Data getDataInicial() {
		return dataInicial;
	}


	public Data getDataFinal() {
		return dataFinal;
	}


	public ArrayList<Classificacao> getListaComentarios() {
		return listaComentarios;
	}


	public Integer getCodigoParceiro() {
		return codigoParceiro;
	}

	public void setCodigoParceiro(Integer codigoParceiro) {
		this.codigoParceiro = codigoParceiro;
	}


	public IAtividade getParceiro() {
		return parceiro;
	}


	public void setParceiro(IAtividade parceiro) {
		this.parceiro = parceiro;
	}


	public String getComentarios() {
		return comentarios;
	}


	public void setComentarios(String comentarios) {
		this.comentarios = comentarios;
	}

	private void setSolicitacoes(List<IAtividade> list) {
		this.solicitacoes = (ArrayList<IAtividade>) list;
	}

	public List<IAtividade> getSolicitacoes() {
		return solicitacoes;
	}

	public int getQualificacoesPositivas() {
		return qualificacoesPositivas;
	}

	public int getQualificacoesNegativas() {
		return qualificacoesNegativas;
	}

	public void setQualificacoesPositivas(int qualificacoesPositivas) {
		this.qualificacoesPositivas = qualificacoesPositivas;
	}

	public void setQualificacoesNegativas(int qualificacoesNegativas) {
		this.qualificacoesNegativas = qualificacoesNegativas;
	}

	public int getAdicionada() {
		return adicionada;
	}

	public void setAdicionada(int adicionada) {
		this.adicionada = adicionada;
	}
}
