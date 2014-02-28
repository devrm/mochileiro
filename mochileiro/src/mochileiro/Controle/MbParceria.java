package mochileiro.Controle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.faces.component.html.HtmlDataTable;

import mochileiro.Modelo.Atividade;
import mochileiro.Modelo.Empresa;
import mochileiro.Modelo.IAtividade;
import mochileiro.Modelo.Usuario;
import mochileiro.Util.FormataObjetoLayout;
import mochileiro.Util.InfoMensagem;
import mochileiro.Util.SessaoJSF;
import mochileiro.Util.paginacaoResultados;

/**
 * 
 * @author $h@rk
 * @projeto mochileiro
 * @Data 17/04/2010 @Hora 17:26:21
 * @Descri��o: Classe respons�vel por gerenciar as opera��es referentes as parcerias entre empresas/atividades
 */
public class MbParceria {

	//recebe objeto atrav�s de binding JSF (verificar view painel empresa - gerenciar parcerias)
	private Empresa empresa;
	private Atividade atividade;
	private IAtividade parceiroSolicitacao;

	private final String GER_PARC = "GERENCIARPARCERIAS";

	//lista de atividades com parceiros que podem possuir solicita��es
	private List<Atividade> atvComParcSolic;	

	//lista de atividades sem parceiros que Possuem solicita��es
	private ArrayList<IAtividade> atvSemParcComSolic;

	private final int LIMITE_NOME = 49;
	private final int LIMITE_DESCRICAO = 53;
	private final String MSG_SEM_PARC_SOLIC = "N�o existem parcerias ou solicita��es recebidas pendentes!";
	
	//atributo utilizado para setar visibilidade de bot�o de parcerias em visualiza atividades
	private boolean isPossuiAtividades = false;
	

	private HashMap<Long, Boolean> itensSelecionados;

	//atributos de pagina��o
	private final int maxPorP�gina  = 2;
	private paginacaoResultados p;
	private String mensagem;


	public MbParceria() {

		atvComParcSolic = new ArrayList<Atividade>();		
		atvSemParcComSolic = new ArrayList<IAtividade>();
		itensSelecionados = new HashMap<Long, Boolean>();

		//inst�ncia objeto de pagina��o
		this.p = new paginacaoResultados();		
		p.setMaxPorPagina(maxPorP�gina);	
	}


	public String acessaGerenciamentoParcerias(){

		return acessaParceria();
	}


	/**
	 * M�todo respons�vel por verificar a exist�ncia de parceiros
	 * ou solicita��es e liberar acesso ao gerenciamento de parcerias
	 * 
	 * @param mensagem
	 * @param navegacao
	 * @return
	 */
	private String acessaParceria(){
		String navegacao = "";
		String mensagem = MSG_SEM_PARC_SOLIC;

		try{

			//caso o redirecionamento seja para a p�gina de gerenciamento
			if(this.defineNavegacao().equalsIgnoreCase(GER_PARC)){
				mensagem = null;
				navegacao = GER_PARC;				
			} else {
				InfoMensagem.mensInfo(mensagem);
			}

		} catch(Exception e){
			e.printStackTrace();
			navegacao = "falhaInterna";
		}	
		return navegacao;
	}

	/** 
	 * M�todo respons�vel por definir navega��o da empresa
	 * 
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	private String defineNavegacao() throws Exception{

		String navegacao = "EMPRESA";

		try{
			//caso ainda existam parcerias ou solicita��es, redireciona para o gerenciamento
			if(this.carregaParceriasSolicitacoes()){
				navegacao = GER_PARC;		
			} 

		} catch(Exception e){
			e.printStackTrace();
			navegacao = "falhaInterna";
		}
		return navegacao;
	}


	/**
	 * M�todo respons�vel por atualizar c�digo dos parceiros
	 * utiliza objeto populado pelo "binding" realizado pelo JSF
	 * 
	 * (setPropertyAction)
	 * 
	 * @return
	 * @throws Exception 
	 */
	public String atualizaParceria() throws Exception{

		int codAtv       =  atividade.getCodigoAtividade(); 
		int codNovoParc  =  parceiroSolicitacao.getCodigoAtividade();
		int codParcAtual =  0;

		//verifica se atividade j� possui um parceiro, nesse
		//caso, extrai o c�digo para eliminar o v�nculo no BD
		if(atividade.getCodigoParceiro() > 0)
			codParcAtual = atividade.getCodigoParceiro();

		//invoca m�todo de atualiza��o da parceria e seta valores 
		//para atualiza��o 
		atividade.atualizaParceria(codAtv, codNovoParc, codParcAtual);

		//carrega lista da empresa atualizada
		empresa.selecionarAtividades(empresa.getUsuario().getCodigo());	

		return this.defineNavegacao();
	}


	/**
	 * Exclui uma solicita��o de parceria pendente
	 * @return
	 * @throws Exception 
	 */
	public String excluiSolicitacao() throws Exception{
		
		//na p�gina de gerenciamento a solicita��o que ser� exclu�da �
		//setada nesse atributo
		if(this.parceiroSolicitacao != null){
			//caso exista solicita��o definida invoca m�todo de exclus�o unit�rio
			this.excluiUnicaSolicitacao(this.atividade, this.parceiroSolicitacao);

		} else {
			//caso c�digo de exclus�o seja indefinido ou existam muitos valores, 
			//invoca m�todo de multiplas exclus�es
			this.excluiMultiplasSolicitacoes(this.atividade);
		}
		//carrega lista da empresa atualizada
		empresa.selecionarAtividades(empresa.getUsuario().getCodigo());	
		
		//anula objetos para evitar vest�gios da sess�o
		this.parceiroSolicitacao = null;
		this.atividade = null;		

		return this.defineNavegacao();
	}

	/**
	 * M�todo utilizado para excluir solicita��o pendente
	 * utiliza c�digos da solicita��o e atividade como
	 * par�metro para opera��o
	 * 
	 * @throws Exception
	 */
	private void excluiUnicaSolicitacao(Atividade atv, IAtividade solicitacao) throws Exception{

		//realiza exclus�o da solicita��o no BD
		atv.excluiSolicitacoes(atv.getCodigoAtividade(), solicitacao.getCodigoAtividade());

	}

	/**
	 * M�todo utilizado para excluir um conjunto
	 * de solicita��es selecionadas pelo usu�rio
	 * 
	 * caso usu�rio esteja visualizando p�ginas que 
	 * possuam checkbox, a sele��o das atividades
	 * � realizada pela cole��o itensSelecionados
	 * sendo assim � necess�rio varrer essa cole��o
	 * para determinar qual solicita��o deve ser exclu�da
	 * 
	 * @param atv: atividade setada pelo JSF (setProperty)
	 * @throws Exception
	 */
	private void excluiMultiplasSolicitacoes(Atividade atv)	throws Exception {

		//itera sobre a lista de solicita��es buscando c�digos das atividades selecionadas
		for (IAtividade solicitacao : atv.getSolicitacoes()){ 

			//verifica��o necess�ria para evitar NullPointerException
			if(itensSelecionados.containsKey(solicitacao.getCodigoAtividade())){

				//caso c�digo exista na lista de exclus�o e esteja setado como true (tenha sido selecionado)
				if (itensSelecionados.get(solicitacao.getCodigoAtividade()).booleanValue()) {

					//realiza exclus�o da solicita��o no BD
					atv.excluiSolicitacoes(atv.getCodigoAtividade(), solicitacao.getCodigoAtividade());

					// elimina item da lista de exclus�o
					itensSelecionados.remove(solicitacao.getCodigoAtividade()); 
				}
			}
		}
	}

	/**
	 * M�todo respons�vel por eliminar uma parceria existente
	 * utiliza objeto populado pelo "binding" realizado pelo Framework
	 * 
	 * (setPropertyAction)
	 * 
	 * @return
	 * @throws Exception 
	 */
	public String excluiParceria() throws Exception{

		//invoca m�todo de exclus�o da parceria
		atividade.excluiParceiro(atividade.getCodigoAtividade(), atividade.getParceiro().getCodigoAtividade());			

		//carrega lista da empresa atualizada
		empresa.selecionarAtividades(empresa.getUsuario().getCodigo());	

		return this.defineNavegacao();
	}

	/**
	 * M�todo respons�vel por verificar se atividades da lista Empresa
	 * possuem solicita��es ou parcerias
	 * Realiza formata��o em caso positivo
	 * @return
	 * @throws Exception
	 */
	private boolean carregaParceriasSolicitacoes() throws Exception{

		boolean isParceiriaSolicitacoes = false;

		//limpa atibutos para evitar vest�gios
		atvComParcSolic.clear();
		atvSemParcComSolic.clear();
		this.mensagem = "";

		//itera sobre a lista de atividades da empresa... (objeto setado pelo binding)
		for(Atividade atv : empresa.getListaAtividade().values()){

			//formata a atividade
			FormataObjetoLayout.formataParceriaSolicitacao(atv, LIMITE_NOME, LIMITE_DESCRICAO);

			//verifica se atividade possui parceiro e realiza a formata��o do mesmo
			if(this.carregaParceiro(atv)) {
				FormataObjetoLayout.formataParceriaSolicitacao(atv.getParceiro(), LIMITE_NOME, LIMITE_DESCRICAO);	

				//caso atividade n�o possua solicita��es, seta mensagem informativa
				if(!this.verificaSolicitacoes(atv)){				
					this.setMensagem(atv.getNomeAtividade(), " n�o possui solicita��es pendentes!");
				}

				//alimenta cole��o de atividades com parceiros
				if(!this.atvComParcSolic.contains(atv))
					this.atvComParcSolic.add(atv);

				isParceiriaSolicitacoes = true;

				//verifica solicita��es para atividades sem parceiros 
			} else if(this.verificaSolicitacoes(atv)){

				FormataObjetoLayout.formataParceriaSolicitacao(atv, 21, LIMITE_DESCRICAO);	

				//alimenta cole��o de atividades sem parceiros com solicita��es pendentes
				if(!atvSemParcComSolic.contains(atv))
					this.atvSemParcComSolic.add(atv);

				isParceiriaSolicitacoes = true;
			}
			
			//inverte cole��es para refletir resultado obtido na consulta
			Collections.reverse(atvComParcSolic);
			Collections.reverse(atvSemParcComSolic);
		}
		return isParceiriaSolicitacoes;
	}


	/**
	 * M�todo respons�vel por verificar se atividade par�metro possui
	 * solicita��es pendentes
	 * 
	 * @param atv
	 * @param isMensagem
	 * @throws Exception 
	 */
	private boolean verificaSolicitacoes(Atividade atv) throws Exception{

		boolean isSolicitacoes = false;

		if(this.carregaSolicitacoes(atv)){

			//realiza formata��o das solicita��es da atividade par�metro
			for(IAtividade solicitacoes : atv.getSolicitacoes())
				FormataObjetoLayout.formataParceriaSolicitacao(solicitacoes, 21, 53);

			isSolicitacoes = true;
		} 

		return isSolicitacoes;
	}

	/**
	 * setter especial usado para atualizar mensagem de exibi��o
	 * na view
	 * 
	 * @param nome
	 * @param mensagem
	 */	
	private void setMensagem(String nome, String mensagem){		
		this.mensagem = "";
		this.mensagem = nome+mensagem;
		this.mensagem.toLowerCase();
	}


	/**
	 * M�todo respons�vel por carregar as informa��es referentes as 
	 * solicita��es pendentes das atividades da empresa
	 * 
	 * @param atv
	 * @return
	 * @throws Exception
	 */
	private boolean carregaSolicitacoes(Atividade atv) throws Exception{
		boolean isSolicitacoes = false;

		//carrega solicita��es para a atividade enviada como par�metro
		atv.selecionaSolicitacoes(atv.getCodigoAtividade(), null);

		//caso exista solicita��es atualiza a vari�vel de retorno
		if(!atv.getSolicitacoes().isEmpty())
			isSolicitacoes = true;

		return isSolicitacoes;
	}



	/**
	 * M�todo respons�vel por carregar informa��es referentes
	 * os objetos parceiros
	 * 
	 * @param atv
	 * @throws Exception
	 */
	private boolean carregaParceiro(Atividade atv) throws Exception{

		boolean isParceiro = false;

		//carrega lista de coment�rios da atividade principal
		atv.selecionaComentarios(atv.getCodigoAtividade());	

		//resgata informa��es do parceiro 
		IAtividade parceiro = atv.seleciona(atv.getCodigoParceiro());

		if(parceiro != null){
			
			//carrega lista de coment�rios do parceiro
			parceiro.selecionaComentarios(parceiro.getCodigoAtividade());
			
			//atualiza parceiro na atividade
			atv.setParceiro(parceiro);

			isParceiro = true;
		}

		return isParceiro;
	}

	/**
	 * M�todo respons�vel por verificar se empresa
	 * possui atividades dispon�veis para realizar
	 * solicita��o de parceria
	 * caso retorne false, bot�o de solicita��o n�o
	 * ser� exibido
	 *  
	 * @return
	 */ 
	private boolean liberaAcessoSolicitacao(){

		boolean isExibe = true;

		try {

			//caso n�o exista empresa criada
			//resgata usu�rio para identificar empresa 
			if(empresa == null){

				this.empresa = new Empresa();

				//recupera usu�rio da sess�o 
				Usuario usuario = (Usuario) SessaoJSF.recuperarItemSessao("usuarioLogado");

				//seta em empresa
				this.empresa.setUsuario(usuario);
			}

			//realiza opera��o de sele��o de atividades da empresa
			this.empresa.selecionarAtividades(this.empresa.getUsuario().getCodigo());	

			//caso empresa n�o possua atividades em sua lista seta vari�vel 
			//de exibi��o como false, com isso bot�o de solicita��es ser� ocultado
			if(empresa.getListaAtividade().isEmpty())
				isExibe = false;				

		} catch(Exception e){
			e.printStackTrace();
			isExibe = false;
		}	

		return isExibe;
	}

	/**
	 * No contexto de solicita��o de parceria a empresa que realiza a opera��o
	 * seleciona uma de suas atividades, est� atividade se torna
	 * a solicitante
	 * 
	 * Esse m�todo � respons�vel por enviar a solicita��o de parceria a solicitada
	 * 
	 * 
	 */
	public String enviaSolicitacao(){

		String mensagem = "";
		String navegacao = null;
		String componente = "solicitacao";

		try {
			//verifica se existem atividades solicitante e solicitada selecionadas 
			if(atividade == null){
				mensagem = "Selecione a atividade que realizar� o convite de parceria";

			} else if(parceiroSolicitacao == null){
				mensagem = "N�o existe um parceiro selecionado para envio da solicita��o";

			} else {

				//registra solicita��o no sistema
				atividade.enviaSolicitacaoParceria(atividade, (Atividade) parceiroSolicitacao);
				navegacao = "SOLICITACAOOK";
				componente = "alertaPainel";
				mensagem = "Solicita��o enviada com sucesso, aguarde an�lise do parceiro!";

				//limpa atributos para evitar vest�gios na sess�o
				atividade = null;
				parceiroSolicitacao = null;
			}
			InfoMensagem.mensInfo(componente, mensagem);

		} catch(Exception e){
			e.printStackTrace();
			navegacao = "falhaInterna";
		}

		return navegacao;
	}



	/**
	 * M�todo respons�vel pelo redirecionamento a 
	 * tela de parcerias, reiniciaza dataTable
	 * para evitar erros na constru��o da view
	 * 
	 * @return
	 */
	public String exibeParcerias(){	
		p = new paginacaoResultados();
		p.setDataTable(new HtmlDataTable());
		p.setMaxPorPagina(maxPorP�gina);
		return "PARCERIAS";
	}

	/**
	 * M�todo respons�vel pelo redirecionamento a 
	 * tela de solicita��es, reiniciaza dataTable
	 * para evitar erros na constru��o da view
	 * 
	 * @return
	 */
	public String exibeSolicitacoes(){
		p = new paginacaoResultados();
		p.setDataTable(new HtmlDataTable());
		p.setMaxPorPagina(maxPorP�gina);

		return "SOLICITACOES";
	}


	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}


	public List<Atividade> getatvComParcSolic() {
		return atvComParcSolic;
	}


	public ArrayList<IAtividade> getAtvSemParcComSolic() {
		return atvSemParcComSolic;
	}


	public int getMaxPorP�gina() {
		return maxPorP�gina;
	}


	public paginacaoResultados getP() {
		return p;
	}

	public void setAtividade(Atividade atividade) {
		this.atividade = atividade;
	}

	public Atividade getAtividade() {
		return atividade; 
	}


	public String getMensagem() {
		return mensagem;
	}

	public HashMap<Long, Boolean> getItensSelecionados() {
		return itensSelecionados;
	}


	public IAtividade getParceiroSolicitacao() {
		return parceiroSolicitacao;
	}

	public void setParceiroSolicitacao(IAtividade parceiroSolicitacao) {
		this.parceiroSolicitacao = parceiroSolicitacao;
	}

	
	/**
	 * M�todo utilizado em visualiza atividade
	 * @return
	 */
	public boolean isPossuiAtividades() {
		
		isPossuiAtividades = this.liberaAcessoSolicitacao();
		
		return isPossuiAtividades;
	}
}


