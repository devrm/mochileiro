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
 * @Descrição:
 */
public class MbVisualizaAtividade {

	//através do listener da lista, faço o "binding" com objeto atividade
	private Atividade atividade;
	private int 	  numComentarios;
	private String    recomendacoesParceiro = "NENHUMA";

	private Classificacao classifica;

	//atividade é da empresa logada
	private boolean   empresaResponsavel = false ;

	//viajante possui em sua lista de favoritos
	private boolean   exibeBotaoFavoritos = false;

	//exibe link de comentários
	private boolean exibeLinkComentarios = false;

	//em conjunto com empresaResponsável exibe solicitação
	//de parcerias
	private boolean liberabotaoParceria = false;

	private final int maxPorPágina  = 3;
	
	private Usuario usuario;

	//paginação de resultados 
	private paginacaoResultados p;

	public MbVisualizaAtividade() {
		classifica = new Classificacao();
		p = new paginacaoResultados();
		p.setMaxPorPagina(maxPorPágina);
	}


	/**
	 * Método responsável por invocar método
	 * de exclusão da atividade
	 * invocado pela view
	 */
	public String excluiAtividade(){
		String navegacao = "ATIVIDADEEXCLUIDA";

		try{

			this.atividade.excluiAtividade(this.atividade.getCodigoAtividade());

			InfoMensagem.mensInfo("AlertaPainel", "Atividade excluída do Sistema!");

		} catch(Exception e){

			navegacao = "falhaInterna";	
			e.printStackTrace();
		}	
		return navegacao;
	}



	/**
	 * Troca a exibição da atividade pelo parceiro
	 * invocado pela view
	 * 
	 * @return
	 */
	public String trocaEntidade(){

		atividade = (Atividade) atividade.getParceiro();

		return this.carregaAtividade();

	}

	/**
	 * Método responsável por carregar atividade com lista de comentários
	 * 
	 * @return
	 */
	public String carregaAtividade(){

		String navegacao = "VISUALIZAATIVIDADE";

		try{
			
			//carrega comentários feitos pelos viajantes
			atividade.selecionaComentarios(atividade.getCodigoAtividade());

			//realiza contagem dos comentários
			numComentarios = atividade.getTotalQualificacao();	

			if(numComentarios == 0)
				InfoMensagem.mensInfo("tbcomentarios","Não existem comentários para essa atividade");
			else
				this.classificar();			

			//verifica a existência de parceiro para a atividade
			if(!this.verificaParceiro())
				InfoMensagem.mensInfo("parceria", "Não existem Parceiros cadastrados para essa atividade");	

			//verifica se visitante possui algum vínculo com 
			//a atividade e configura funcionalidades
			this.trataExibicao();

			//realiza formatação de campos para exibição na view 
			FormataObjetoLayout.formataExibicaoAtividade(atividade, FormataObjetoLayout.LAYOUT_COMPLETO);

		} catch(Exception e){
			navegacao = "falhaInterna";	
			e.printStackTrace();
		}		

		return navegacao;
	}


	/**
	 * Método responsável por verificar se usuário
	 * visitante, empresa ou viajante.
	 * 
	 * Caso seja um viajante, verifica se o mesmo
	 * possui a atividade em sua lista de favoritos
	 * em caso positivo libera acesso a página de comentários
	 * 
	 * Caso se trate de uma empresa, verifica se a mesma é a
	 * responsável pela atividade, em caso positivo libera
	 * ações de edição e exclusão da atividade além de exibir
	 * alerta caso atividade esteja com período de disponibilidade
	 * expirado
	 * 
	 * Caso se trate de um visitante não logado, não libera 
	 * acesso a qualquer funcionalidade descrita anteriormente
	 * @throws Exception 
	 * 
	 * 
	 */
	private void trataExibicao() throws Exception{

		//inicializa atributos para evitar vestígios da sessão
		this.empresaResponsavel   = false;
		this.exibeBotaoFavoritos  = false;
		this.exibeLinkComentarios = false;
		this.liberabotaoParceria  = false;

		//tenta resgatar usuário da sessão
		usuario = (Usuario) SessaoJSF.recuperarItemSessao("usuarioLogado");

		if(usuario != null){

			//verifica o tipo do usuário e direciona ao método adequado
			if(usuario.getTipo().equalsIgnoreCase("EMPRESA")){				

				//libera a operação de parceria, esse estado
				//pode mudar após execução do método
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
	 * lista de favoritos e libera acesso aos comentários
	 * em caso positivo
	 * 
	 * @throws Exception 
	 */
	private void acessoViajanteFavoritos(Viajante viajante, Atividade atv) throws Exception{		

	/*	mensagem = "";
		String sugestao = "\n Você ainda não realizou seu comentário, expresse sua opinião sobre essa Atividade!";
*/
		viajante.selecionarAtividadesFavoritas(viajante.getUsuario().getCodigo());

		//verifica se viajante possui atividade em sua lista
		if(viajante.getListaAtividade().containsKey(atv.getCodigoAtividade())){/*
			
			//atualiza lista de comenários
			atv.selecionaComentarios();
			
			//caso existam comentários na lista...
			if(!atv.getListaComentarios().isEmpty()){

				//verifica se viajante já realizou comentário para atividade 
				//visualizada (lista de comentários selecionada no método carregaAtividade())
				for(Classificacao cla : atv.getListaComentarios()){	

					//identifica que viajante não realizou comentários para a atividade realizada
					if(cla.getCodViajante() != viajante.getUsuario().getCodigo()){

						//envia sugestão ao viajante
						mensagem = mensagem.concat(sugestao);
						break;					
					}
				} 

			} else {
				//caso não existam comentários para a atividade envia sugestão ao usuário
				mensagem = sugestao;
			}

			//informa ao visitante sobre a situação da atividade e sugestão de comentário
			InfoMensagem.mensInfo("viajanteFav", mensagem);

*/			//como atividade já existe na lista de favoritos, desabilita botão de adição
			exibeBotaoFavoritos  = false;

			//permite que usuário realize comentários 
			exibeLinkComentarios = true;

		} else {

			//habilita botão de adição, pois viajante não possui comentários 
			exibeBotaoFavoritos = true;
			//mensagem = "";
		}		
	}


	/**
	 * Configura ações pertinentes a empresa 
	 * responsável pela atividade
	 * @return
	 */
	private void acessoEmpresaResponsavel(Atividade a, Usuario user){

		//como usuário possui o código da empresa e atividade
		//carrega codigo de sua empresa responsável, realiza
		//verificação dos códigos resgatados para liberar acesso
		//as funcionalidades		
		if(a.getCodigoEmpresa() == user.getCodigo()){

			//extrai as datas de funcionamento da atividade
			Data dtInicial = a.getDataInicial();
			Data dtFinal   = a.getDataFinal();

			//verifica se data de disponibilidade é inferior a data atual
			if(dtInicial.comparaDatas(dtInicial, dtFinal) == Data.DT_FINAL_MENOR_ATUAL){

				//informa a empresa sobre a situação
				InfoMensagem.mensInfo("dataFim", "Atividade com período de disponibilidade expirado, atualize o cadastro!");
			}
			//libera acesso as operações da empresa
			empresaResponsavel = true;	

			//como a atividade pertence a empresa
			//seta false para o botão de parceria
			this.liberabotaoParceria = false;
		}
	}

	/**
	 * Método responsável por realizar a contagem de notas
	 * e setar valor por extenso das classificações enviadas
	 * pelos viajantes também realiza formatação das datas
	 * 
	 * Obs.: Através de referência, objetos modificados
	 * 		 refletem alterações no objeto original da coleção
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
			
			//limita comentário exibido na visualização da atividade
			if(cla.getComentarioViajante() != null){
				String comentCurto = FormataObjetoLayout.limitaCampo(cla.getComentarioViajante(), 150);
				cla.setComentarioLista(comentCurto);
			}
			//extrai data do objeto comentário
			Data data = cla.getDataComentario();

			//extrai string no formato dd/MM/yyyy
			String dataFormatada = data.dataCompleta(true);

			//reinsere data formatada no objeto classificação
			cla.getDataComentario().setDataFormatada(dataFormatada);
		}
	}

	/**
	 * Método responsável por carregar o parceiro da atividade visualizada
	 * @return 
	 * @throws Exception 
	 */
	private boolean verificaParceiro() throws Exception{

		boolean isParceiro = false;

		//verifica existência de parceiro
		if(atividade.getCodigoParceiro() > 0)
			atividade.setParceiro(atividade.seleciona(atividade.getCodigoParceiro()));		

		if(atividade.getParceiro() != null){

			//carrega lista de comentários do parceiro para contagem
			atividade.getParceiro().selecionaComentarios(atividade.getCodigoParceiro());				

			//caso exista comentários, realiza a contagem
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
				
				//recarrega informações da atividade
				resultado = this.carregaAtividade();
				
			} else {
				InfoMensagem.mensInfo("descricao", "Limite máximo 499 caracteres");
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
