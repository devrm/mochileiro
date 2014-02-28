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
 * @Descrição: Classe responsável por gerenciar as operações referentes as parcerias entre empresas/atividades
 */
public class MbParceria {

	//recebe objeto através de binding JSF (verificar view painel empresa - gerenciar parcerias)
	private Empresa empresa;
	private Atividade atividade;
	private IAtividade parceiroSolicitacao;

	private final String GER_PARC = "GERENCIARPARCERIAS";

	//lista de atividades com parceiros que podem possuir solicitações
	private List<Atividade> atvComParcSolic;	

	//lista de atividades sem parceiros que Possuem solicitações
	private ArrayList<IAtividade> atvSemParcComSolic;

	private final int LIMITE_NOME = 49;
	private final int LIMITE_DESCRICAO = 53;
	private final String MSG_SEM_PARC_SOLIC = "Não existem parcerias ou solicitações recebidas pendentes!";
	
	//atributo utilizado para setar visibilidade de botão de parcerias em visualiza atividades
	private boolean isPossuiAtividades = false;
	

	private HashMap<Long, Boolean> itensSelecionados;

	//atributos de paginação
	private final int maxPorPágina  = 2;
	private paginacaoResultados p;
	private String mensagem;


	public MbParceria() {

		atvComParcSolic = new ArrayList<Atividade>();		
		atvSemParcComSolic = new ArrayList<IAtividade>();
		itensSelecionados = new HashMap<Long, Boolean>();

		//instância objeto de paginação
		this.p = new paginacaoResultados();		
		p.setMaxPorPagina(maxPorPágina);	
	}


	public String acessaGerenciamentoParcerias(){

		return acessaParceria();
	}


	/**
	 * Método responsável por verificar a existência de parceiros
	 * ou solicitações e liberar acesso ao gerenciamento de parcerias
	 * 
	 * @param mensagem
	 * @param navegacao
	 * @return
	 */
	private String acessaParceria(){
		String navegacao = "";
		String mensagem = MSG_SEM_PARC_SOLIC;

		try{

			//caso o redirecionamento seja para a página de gerenciamento
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
	 * Método responsável por definir navegação da empresa
	 * 
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	private String defineNavegacao() throws Exception{

		String navegacao = "EMPRESA";

		try{
			//caso ainda existam parcerias ou solicitações, redireciona para o gerenciamento
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
	 * Método responsável por atualizar código dos parceiros
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

		//verifica se atividade já possui um parceiro, nesse
		//caso, extrai o código para eliminar o vínculo no BD
		if(atividade.getCodigoParceiro() > 0)
			codParcAtual = atividade.getCodigoParceiro();

		//invoca método de atualização da parceria e seta valores 
		//para atualização 
		atividade.atualizaParceria(codAtv, codNovoParc, codParcAtual);

		//carrega lista da empresa atualizada
		empresa.selecionarAtividades(empresa.getUsuario().getCodigo());	

		return this.defineNavegacao();
	}


	/**
	 * Exclui uma solicitação de parceria pendente
	 * @return
	 * @throws Exception 
	 */
	public String excluiSolicitacao() throws Exception{
		
		//na página de gerenciamento a solicitação que será excluída é
		//setada nesse atributo
		if(this.parceiroSolicitacao != null){
			//caso exista solicitação definida invoca método de exclusão unitário
			this.excluiUnicaSolicitacao(this.atividade, this.parceiroSolicitacao);

		} else {
			//caso código de exclusão seja indefinido ou existam muitos valores, 
			//invoca método de multiplas exclusões
			this.excluiMultiplasSolicitacoes(this.atividade);
		}
		//carrega lista da empresa atualizada
		empresa.selecionarAtividades(empresa.getUsuario().getCodigo());	
		
		//anula objetos para evitar vestígios da sessão
		this.parceiroSolicitacao = null;
		this.atividade = null;		

		return this.defineNavegacao();
	}

	/**
	 * Método utilizado para excluir solicitação pendente
	 * utiliza códigos da solicitação e atividade como
	 * parâmetro para operação
	 * 
	 * @throws Exception
	 */
	private void excluiUnicaSolicitacao(Atividade atv, IAtividade solicitacao) throws Exception{

		//realiza exclusão da solicitação no BD
		atv.excluiSolicitacoes(atv.getCodigoAtividade(), solicitacao.getCodigoAtividade());

	}

	/**
	 * Método utilizado para excluir um conjunto
	 * de solicitações selecionadas pelo usuário
	 * 
	 * caso usuário esteja visualizando páginas que 
	 * possuam checkbox, a seleção das atividades
	 * é realizada pela coleção itensSelecionados
	 * sendo assim é necessário varrer essa coleção
	 * para determinar qual solicitação deve ser excluída
	 * 
	 * @param atv: atividade setada pelo JSF (setProperty)
	 * @throws Exception
	 */
	private void excluiMultiplasSolicitacoes(Atividade atv)	throws Exception {

		//itera sobre a lista de solicitações buscando códigos das atividades selecionadas
		for (IAtividade solicitacao : atv.getSolicitacoes()){ 

			//verificação necessária para evitar NullPointerException
			if(itensSelecionados.containsKey(solicitacao.getCodigoAtividade())){

				//caso código exista na lista de exclusão e esteja setado como true (tenha sido selecionado)
				if (itensSelecionados.get(solicitacao.getCodigoAtividade()).booleanValue()) {

					//realiza exclusão da solicitação no BD
					atv.excluiSolicitacoes(atv.getCodigoAtividade(), solicitacao.getCodigoAtividade());

					// elimina item da lista de exclusão
					itensSelecionados.remove(solicitacao.getCodigoAtividade()); 
				}
			}
		}
	}

	/**
	 * Método responsável por eliminar uma parceria existente
	 * utiliza objeto populado pelo "binding" realizado pelo Framework
	 * 
	 * (setPropertyAction)
	 * 
	 * @return
	 * @throws Exception 
	 */
	public String excluiParceria() throws Exception{

		//invoca método de exclusão da parceria
		atividade.excluiParceiro(atividade.getCodigoAtividade(), atividade.getParceiro().getCodigoAtividade());			

		//carrega lista da empresa atualizada
		empresa.selecionarAtividades(empresa.getUsuario().getCodigo());	

		return this.defineNavegacao();
	}

	/**
	 * Método responsável por verificar se atividades da lista Empresa
	 * possuem solicitações ou parcerias
	 * Realiza formatação em caso positivo
	 * @return
	 * @throws Exception
	 */
	private boolean carregaParceriasSolicitacoes() throws Exception{

		boolean isParceiriaSolicitacoes = false;

		//limpa atibutos para evitar vestígios
		atvComParcSolic.clear();
		atvSemParcComSolic.clear();
		this.mensagem = "";

		//itera sobre a lista de atividades da empresa... (objeto setado pelo binding)
		for(Atividade atv : empresa.getListaAtividade().values()){

			//formata a atividade
			FormataObjetoLayout.formataParceriaSolicitacao(atv, LIMITE_NOME, LIMITE_DESCRICAO);

			//verifica se atividade possui parceiro e realiza a formatação do mesmo
			if(this.carregaParceiro(atv)) {
				FormataObjetoLayout.formataParceriaSolicitacao(atv.getParceiro(), LIMITE_NOME, LIMITE_DESCRICAO);	

				//caso atividade não possua solicitações, seta mensagem informativa
				if(!this.verificaSolicitacoes(atv)){				
					this.setMensagem(atv.getNomeAtividade(), " não possui solicitações pendentes!");
				}

				//alimenta coleção de atividades com parceiros
				if(!this.atvComParcSolic.contains(atv))
					this.atvComParcSolic.add(atv);

				isParceiriaSolicitacoes = true;

				//verifica solicitações para atividades sem parceiros 
			} else if(this.verificaSolicitacoes(atv)){

				FormataObjetoLayout.formataParceriaSolicitacao(atv, 21, LIMITE_DESCRICAO);	

				//alimenta coleção de atividades sem parceiros com solicitações pendentes
				if(!atvSemParcComSolic.contains(atv))
					this.atvSemParcComSolic.add(atv);

				isParceiriaSolicitacoes = true;
			}
			
			//inverte coleções para refletir resultado obtido na consulta
			Collections.reverse(atvComParcSolic);
			Collections.reverse(atvSemParcComSolic);
		}
		return isParceiriaSolicitacoes;
	}


	/**
	 * Método responsável por verificar se atividade parâmetro possui
	 * solicitações pendentes
	 * 
	 * @param atv
	 * @param isMensagem
	 * @throws Exception 
	 */
	private boolean verificaSolicitacoes(Atividade atv) throws Exception{

		boolean isSolicitacoes = false;

		if(this.carregaSolicitacoes(atv)){

			//realiza formatação das solicitações da atividade parâmetro
			for(IAtividade solicitacoes : atv.getSolicitacoes())
				FormataObjetoLayout.formataParceriaSolicitacao(solicitacoes, 21, 53);

			isSolicitacoes = true;
		} 

		return isSolicitacoes;
	}

	/**
	 * setter especial usado para atualizar mensagem de exibição
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
	 * Método responsável por carregar as informações referentes as 
	 * solicitações pendentes das atividades da empresa
	 * 
	 * @param atv
	 * @return
	 * @throws Exception
	 */
	private boolean carregaSolicitacoes(Atividade atv) throws Exception{
		boolean isSolicitacoes = false;

		//carrega solicitações para a atividade enviada como parâmetro
		atv.selecionaSolicitacoes(atv.getCodigoAtividade(), null);

		//caso exista solicitações atualiza a variável de retorno
		if(!atv.getSolicitacoes().isEmpty())
			isSolicitacoes = true;

		return isSolicitacoes;
	}



	/**
	 * Método responsável por carregar informações referentes
	 * os objetos parceiros
	 * 
	 * @param atv
	 * @throws Exception
	 */
	private boolean carregaParceiro(Atividade atv) throws Exception{

		boolean isParceiro = false;

		//carrega lista de comentários da atividade principal
		atv.selecionaComentarios(atv.getCodigoAtividade());	

		//resgata informações do parceiro 
		IAtividade parceiro = atv.seleciona(atv.getCodigoParceiro());

		if(parceiro != null){
			
			//carrega lista de comentários do parceiro
			parceiro.selecionaComentarios(parceiro.getCodigoAtividade());
			
			//atualiza parceiro na atividade
			atv.setParceiro(parceiro);

			isParceiro = true;
		}

		return isParceiro;
	}

	/**
	 * Método responsável por verificar se empresa
	 * possui atividades disponíveis para realizar
	 * solicitação de parceria
	 * caso retorne false, botão de solicitação não
	 * será exibido
	 *  
	 * @return
	 */ 
	private boolean liberaAcessoSolicitacao(){

		boolean isExibe = true;

		try {

			//caso não exista empresa criada
			//resgata usuário para identificar empresa 
			if(empresa == null){

				this.empresa = new Empresa();

				//recupera usuário da sessão 
				Usuario usuario = (Usuario) SessaoJSF.recuperarItemSessao("usuarioLogado");

				//seta em empresa
				this.empresa.setUsuario(usuario);
			}

			//realiza operação de seleção de atividades da empresa
			this.empresa.selecionarAtividades(this.empresa.getUsuario().getCodigo());	

			//caso empresa não possua atividades em sua lista seta variável 
			//de exibição como false, com isso botão de solicitações será ocultado
			if(empresa.getListaAtividade().isEmpty())
				isExibe = false;				

		} catch(Exception e){
			e.printStackTrace();
			isExibe = false;
		}	

		return isExibe;
	}

	/**
	 * No contexto de solicitação de parceria a empresa que realiza a operação
	 * seleciona uma de suas atividades, está atividade se torna
	 * a solicitante
	 * 
	 * Esse método é responsável por enviar a solicitação de parceria a solicitada
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
				mensagem = "Selecione a atividade que realizará o convite de parceria";

			} else if(parceiroSolicitacao == null){
				mensagem = "Não existe um parceiro selecionado para envio da solicitação";

			} else {

				//registra solicitação no sistema
				atividade.enviaSolicitacaoParceria(atividade, (Atividade) parceiroSolicitacao);
				navegacao = "SOLICITACAOOK";
				componente = "alertaPainel";
				mensagem = "Solicitação enviada com sucesso, aguarde análise do parceiro!";

				//limpa atributos para evitar vestígios na sessão
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
	 * Método responsável pelo redirecionamento a 
	 * tela de parcerias, reiniciaza dataTable
	 * para evitar erros na construção da view
	 * 
	 * @return
	 */
	public String exibeParcerias(){	
		p = new paginacaoResultados();
		p.setDataTable(new HtmlDataTable());
		p.setMaxPorPagina(maxPorPágina);
		return "PARCERIAS";
	}

	/**
	 * Método responsável pelo redirecionamento a 
	 * tela de solicitações, reiniciaza dataTable
	 * para evitar erros na construção da view
	 * 
	 * @return
	 */
	public String exibeSolicitacoes(){
		p = new paginacaoResultados();
		p.setDataTable(new HtmlDataTable());
		p.setMaxPorPagina(maxPorPágina);

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


	public int getMaxPorPágina() {
		return maxPorPágina;
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
	 * Método utilizado em visualiza atividade
	 * @return
	 */
	public boolean isPossuiAtividades() {
		
		isPossuiAtividades = this.liberaAcessoSolicitacao();
		
		return isPossuiAtividades;
	}
}


