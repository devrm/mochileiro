package mochileiro.Controle;

import javax.faces.component.html.HtmlDataTable;

import mochileiro.Modelo.Atividade;
import mochileiro.Modelo.Classificacao;
import mochileiro.Modelo.Usuario;
import mochileiro.Modelo.Viajante;
import mochileiro.Util.Data;
import mochileiro.Util.FormataObjetoLayout;
import mochileiro.Util.InfoMensagem;
import mochileiro.Util.SessaoJSF;
import mochileiro.Util.paginacaoResultados;

/**
 * @author Thiago
 * @projeto mochileiro
 * @Data 06/02/2010 @Hora 13:06:11
 * @Descri��o:
 */
public class MbVisualizaAtividade {

	//atrav�s do listener da lista, fa�o o "binding" com objeto atividade
	private Atividade atividade;
	private int 	  numComentarios;
	private String    recomendacoesParceiro = "NENHUMA";

	private Classificacao classifica;

	//atividade � da empresa logada
	private boolean   empresaResponsavel = false ;

	//viajante possui em sua lista de favoritos
	private boolean   exibeBotaoFavoritos = false;

	//exibe link de coment�rios
	private boolean exibeLinkComentarios = false;

	//em conjunto com empresaRespons�vel exibe solicita��o
	//de parcerias
	private boolean liberabotaoParceria = false;

	private final int maxPorP�gina  = 3;
	
	private Usuario usuario;

	//pagina��o de resultados 
	private paginacaoResultados p;

	public MbVisualizaAtividade() {
		classifica = new Classificacao();
		p = new paginacaoResultados();
		p.setMaxPorPagina(maxPorP�gina);
	}


	/**
	 * M�todo respons�vel por invocar m�todo
	 * de exclus�o da atividade
	 * invocado pela view
	 */
	public String excluiAtividade(){
		String navegacao = "ATIVIDADEEXCLUIDA";

		try{

			this.atividade.excluiAtividade(this.atividade.getCodigoAtividade());

			InfoMensagem.mensInfo("AlertaPainel", "Atividade exclu�da do Sistema!");

		} catch(Exception e){

			navegacao = "falhaInterna";	
			e.printStackTrace();
		}	
		return navegacao;
	}



	/**
	 * Troca a exibi��o da atividade pelo parceiro
	 * invocado pela view
	 * 
	 * @return
	 */
	public String trocaEntidade(){

		atividade = (Atividade) atividade.getParceiro();

		return this.carregaAtividade();

	}

	/**
	 * M�todo respons�vel por carregar atividade com lista de coment�rios
	 * 
	 * @return
	 */
	public String carregaAtividade(){

		String navegacao = "VISUALIZAATIVIDADE";

		try{
			
			//carrega coment�rios feitos pelos viajantes
			atividade.selecionaComentarios(atividade.getCodigoAtividade());

			//realiza contagem dos coment�rios
			numComentarios = atividade.getTotalQualificacao();	

			if(numComentarios == 0)
				InfoMensagem.mensInfo("tbcomentarios","N�o existem coment�rios para essa atividade");
			else
				this.classificar();			

			//verifica a exist�ncia de parceiro para a atividade
			if(!this.verificaParceiro())
				InfoMensagem.mensInfo("parceria", "N�o existem Parceiros cadastrados para essa atividade");	

			//verifica se visitante possui algum v�nculo com 
			//a atividade e configura funcionalidades
			this.trataExibicao();

			//realiza formata��o de campos para exibi��o na view 
			FormataObjetoLayout.formataExibicaoAtividade(atividade, FormataObjetoLayout.LAYOUT_COMPLETO);

		} catch(Exception e){
			navegacao = "falhaInterna";	
			e.printStackTrace();
		}		

		return navegacao;
	}


	/**
	 * M�todo respons�vel por verificar se usu�rio
	 * visitante, empresa ou viajante.
	 * 
	 * Caso seja um viajante, verifica se o mesmo
	 * possui a atividade em sua lista de favoritos
	 * em caso positivo libera acesso a p�gina de coment�rios
	 * 
	 * Caso se trate de uma empresa, verifica se a mesma � a
	 * respons�vel pela atividade, em caso positivo libera
	 * a��es de edi��o e exclus�o da atividade al�m de exibir
	 * alerta caso atividade esteja com per�odo de disponibilidade
	 * expirado
	 * 
	 * Caso se trate de um visitante n�o logado, n�o libera 
	 * acesso a qualquer funcionalidade descrita anteriormente
	 * @throws Exception 
	 * 
	 * 
	 */
	private void trataExibicao() throws Exception{

		//inicializa atributos para evitar vest�gios da sess�o
		this.empresaResponsavel   = false;
		this.exibeBotaoFavoritos  = false;
		this.exibeLinkComentarios = false;
		this.liberabotaoParceria  = false;

		//tenta resgatar usu�rio da sess�o
		usuario = (Usuario) SessaoJSF.recuperarItemSessao("usuarioLogado");

		if(usuario != null){

			//verifica o tipo do usu�rio e direciona ao m�todo adequado
			if(usuario.getTipo().equalsIgnoreCase("EMPRESA")){				

				//libera a opera��o de parceria, esse estado
				//pode mudar ap�s execu��o do m�todo
				//acessoEmpresaResponsavel
				liberabotaoParceria = true;

				this.acessoEmpresaResponsavel(this.atividade, usuario);				

			} else if(usuario.getTipo().equalsIgnoreCase("VIAJANTE")){

				Viajante viajante = new Viajante(usuario);

				this.acessoViajanteFavoritos(viajante, this.atividade);
			}
		}
	}


	/**
	 * Verifica se viajante possui atividade em sua
	 * lista de favoritos e libera acesso aos coment�rios
	 * em caso positivo
	 * 
	 * @throws Exception 
	 */
	private void acessoViajanteFavoritos(Viajante viajante, Atividade atv) throws Exception{		

	/*	mensagem = "";
		String sugestao = "\n Voc� ainda n�o realizou seu coment�rio, expresse sua opini�o sobre essa Atividade!";
*/
		viajante.selecionarAtividadesFavoritas(viajante.getUsuario().getCodigo());

		//verifica se viajante possui atividade em sua lista
		if(viajante.getListaAtividade().containsKey(atv.getCodigoAtividade())){/*
			
			//atualiza lista de comen�rios
			atv.selecionaComentarios();
			
			//caso existam coment�rios na lista...
			if(!atv.getListaComentarios().isEmpty()){

				//verifica se viajante j� realizou coment�rio para atividade 
				//visualizada (lista de coment�rios selecionada no m�todo carregaAtividade())
				for(Classificacao cla : atv.getListaComentarios()){	

					//identifica que viajante n�o realizou coment�rios para a atividade realizada
					if(cla.getCodViajante() != viajante.getUsuario().getCodigo()){

						//envia sugest�o ao viajante
						mensagem = mensagem.concat(sugestao);
						break;					
					}
				} 

			} else {
				//caso n�o existam coment�rios para a atividade envia sugest�o ao usu�rio
				mensagem = sugestao;
			}

			//informa ao visitante sobre a situa��o da atividade e sugest�o de coment�rio
			InfoMensagem.mensInfo("viajanteFav", mensagem);

*/			//como atividade j� existe na lista de favoritos, desabilita bot�o de adi��o
			exibeBotaoFavoritos  = false;

			//permite que usu�rio realize coment�rios 
			exibeLinkComentarios = true;

		} else {

			//habilita bot�o de adi��o, pois viajante n�o possui coment�rios 
			exibeBotaoFavoritos = true;
			//mensagem = "";
		}		
	}


	/**
	 * Configura a��es pertinentes a empresa 
	 * respons�vel pela atividade
	 * @return
	 */
	private void acessoEmpresaResponsavel(Atividade a, Usuario user){

		//como usu�rio possui o c�digo da empresa e atividade
		//carrega codigo de sua empresa respons�vel, realiza
		//verifica��o dos c�digos resgatados para liberar acesso
		//as funcionalidades		
		if(a.getCodigoEmpresa() == user.getCodigo()){

			//extrai as datas de funcionamento da atividade
			Data dtInicial = a.getDataInicial();
			Data dtFinal   = a.getDataFinal();

			//verifica se data de disponibilidade � inferior a data atual
			if(dtInicial.comparaDatas(dtInicial, dtFinal) == Data.DT_FINAL_MENOR_ATUAL){

				//informa a empresa sobre a situa��o
				InfoMensagem.mensInfo("dataFim", "Atividade com per�odo de disponibilidade expirado, atualize o cadastro!");
			}
			//libera acesso as opera��es da empresa
			empresaResponsavel = true;	

			//como a atividade pertence a empresa
			//seta false para o bot�o de parceria
			this.liberabotaoParceria = false;
		}
	}

	/**
	 * M�todo respons�vel por realizar a contagem de notas
	 * e setar valor por extenso das classifica��es enviadas
	 * pelos viajantes tamb�m realiza formata��o das datas
	 * 
	 * Obs.: Atrav�s de refer�ncia, objetos modificados
	 * 		 refletem altera��es no objeto original da cole��o
	 * 
	 */
	private void classificar(){		

		//itera sobre lista contado notas e formatando data
		for(Classificacao cla : atividade.getListaComentarios()){

			//realiza contagem das notas
			if(cla.getNota()){
				//seta valor por extenso (true positiva)
				cla.setNotaExtenso(cla.NOTA_POSITIVA);
			} else {
				//seta valor por extenso (false negativa)
				cla.setNotaExtenso(cla.NOTA_NEGATIVA);
			}
			
			//limita coment�rio exibido na visualiza��o da atividade
			if(cla.getComentarioViajante() != null){
				String comentCurto = FormataObjetoLayout.limitaCampo(cla.getComentarioViajante(), 150);
				cla.setComentarioLista(comentCurto);
			}
			//extrai data do objeto coment�rio
			Data data = cla.getDataComentario();

			//extrai string no formato dd/MM/yyyy
			String dataFormatada = data.dataCompleta(true);

			//reinsere data formatada no objeto classifica��o
			cla.getDataComentario().setDataFormatada(dataFormatada);
		}
	}

	/**
	 * M�todo respons�vel por carregar o parceiro da atividade visualizada
	 * @return 
	 * @throws Exception 
	 */
	private boolean verificaParceiro() throws Exception{

		boolean isParceiro = false;

		//verifica exist�ncia de parceiro
		if(atividade.getCodigoParceiro() > 0)
			atividade.setParceiro(atividade.seleciona(atividade.getCodigoParceiro()));		

		if(atividade.getParceiro() != null){

			//carrega lista de coment�rios do parceiro para contagem
			atividade.getParceiro().selecionaComentarios(atividade.getCodigoParceiro());				

			//caso exista coment�rios, realiza a contagem
			if(atividade.getParceiro().getListaComentarios().size() > 0){
				recomendacoesParceiro = String.valueOf(atividade.getParceiro().getListaComentarios().size());
			}

			isParceiro = true;
		} 		

		return isParceiro;
	}

	public String registrarComentario() {
		String resultado = null;
		
		try {				
			
			if(classifica.getComentarioViajante().length() < 500){				
				
				classifica.insereClassificacao(atividade, usuario);
				
				classifica = new Classificacao();
				
				//recarrega informa��es da atividade
				resultado = this.carregaAtividade();
				
			} else {
				InfoMensagem.mensInfo("descricao", "Limite m�ximo 499 caracteres");
			}
			

		}catch (Exception ex) {
			resultado = "falhaInterna";
			ex.printStackTrace();
		}
		
		return resultado;
	}
	
	
	public String exibeComentarios(){
		
		String navegacao = "COMENTARIOS";
		
		p.setDataTable(new HtmlDataTable());
		
		return navegacao;
	}

	//getters e setters
	public String getRecomendacoesParceiro() {
		return recomendacoesParceiro;
	}

	public Atividade getAtividade() {
		return atividade;
	}

	public void setAtividade(Atividade atividade) {
		this.atividade = atividade;
	}

	public int getNumComentarios() {
		return numComentarios;
	}

	public boolean isEmpresaResponsavel() {
		return empresaResponsavel;
	}

	public boolean isexibeBotaoFavoritos() {
		return exibeBotaoFavoritos;
	}

	public Classificacao getClassificacao() {
		return classifica;
	}

	public boolean isExibeLinkComentarios() {
		return exibeLinkComentarios;
	}

	public paginacaoResultados getP() {
		return p;
	}

	public boolean isLiberabotaoParceria() {
		return liberabotaoParceria;
	}
}
