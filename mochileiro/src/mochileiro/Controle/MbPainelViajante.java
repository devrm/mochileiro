package mochileiro.Controle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mochileiro.Modelo.Atividade;
import mochileiro.Modelo.GeraRelatorio;
import mochileiro.Modelo.GoogleMaps;
import mochileiro.Modelo.Rota;
import mochileiro.Modelo.Usuario;
import mochileiro.Modelo.Viajante;
import mochileiro.Util.FormataObjetoLayout;
import mochileiro.Util.InfoMensagem;
import mochileiro.Util.SessaoJSF;
import mochileiro.Util.paginacaoResultados;

/**
 * @author $h@rk
 * @projeto mochileiro
 * @Data 03/06/2010 @Hora 09:24:26
 * @Descrição: Classe responsável por gerenciar ações do Viajante
 */
public class MbPainelViajante {

	private static final String MENSAGEM_LISTA_VAZIA = "Não existem atividades adicionadas a sua lista de favoritos!";

	private GoogleMaps maps;

	private String infoQtdeAtividades = "Lista Vazia";
	private int    numAtividades;

	private ArrayList<Atividade> listaAtividades;

	private HashMap<Integer, Boolean> atividadesSelecionadas;

	private paginacaoResultados p;

	private final int maxPorPágina  = 20;
	private GeraRelatorio relatorio;
	private boolean erroRota = false;

	private boolean isExibeBotao = true;

	private Viajante viajante;


	public MbPainelViajante() {

		this.viajante = new Viajante();	

		this.ajustaInfoConta();

		this.p = new paginacaoResultados();

		this.p.setMaxPorPagina(maxPorPágina);

		atividadesSelecionadas = new HashMap<Integer, Boolean>();	
	}


	/**
	 * Exclui atividade da lista de favoritos
	 * @return
	 */
	public String ExcluiAtividade() {

		String navegacao = "VIAJANTE";
		String mensagem = "";
		String componente = "alertaPainel";
		
		int codigoViajante = viajante.getUsuario().getCodigo();
		
		boolean isExcluiu = false;
		
		try{

			for (Atividade selecionada : viajante.getListaAtividade().values()) {           

				//verificalção necessária para evitar nullpointer 
				if(atividadesSelecionadas.containsKey(selecionada.getCodigoAtividade())){

					//verifica se item foi checado
					if (this.atividadesSelecionadas.get(selecionada.getCodigoAtividade()).booleanValue()){						
						//em caso positivo, realiza exclusão
						viajante.excluiFavoritos(selecionada.getCodigoAtividade(), codigoViajante);
						isExcluiu = true;
					}
				}		
			}
			
			//caso tenha realizado exclusão, informa o usuário
			if(isExcluiu){
				mensagem = "Atividade(s) excluída(s) da lista!";
			} else {
				mensagem = "Selecione as atividades para exclusão";
				navegacao = null;
				componente = "formulario";
			}
			
			if(!mensagem.equals(""))
				InfoMensagem.mensInfo(componente, mensagem);

		} catch (Exception ex) {
			navegacao = "falhaInterna";
			ex.printStackTrace();
		}
		return navegacao;		
	}


	/**
	 * Método responsável por adequar informações para 
	 * geração de relatório de gastos
	 * @return
	 */
	public String estimarGastos(){

		String navegacao = null;
		String mensagem  = null;

		try{		

			HashMap<Integer, Atividade> listaRelatorio = this.configurarAtividadesEstimativa();

			//verifica se existem atividades selecionadas
			if(listaRelatorio.isEmpty()){			
				mensagem = "Selecione as atividades que farão parte da estimativa!";
			} else {				
				//caso possua informações cria relatório, método extraiAtividades verifica quais 
				//atividades foram selecionadas
				relatorio = new GeraRelatorio(viajante.getNome(), GeraRelatorio.RELATORIO_ESTIMATIVA, listaRelatorio );
				relatorio.criaRelatorio();
			}		
			
			//seta mensagem de erro (caso exista)
			if(mensagem != null)
				InfoMensagem.mensInfo("formulario", mensagem);

		} catch (Exception ex) {
			navegacao = "falhaInterna";
			ex.printStackTrace();
		}		

		return navegacao;
	}

	/**
	 * Método responsável por identificar quais atividades foram selecionadas
	 * pelo viajante para geração de relatório
	 * @return
	 */
	private HashMap<Integer, Atividade> configurarAtividadesEstimativa(){

		HashMap<Integer, Atividade> atvRelatorio = new HashMap<Integer, Atividade>();

		Double somatoriaPreco = 0.00;

		for (Atividade atv : viajante.getListaAtividade().values()){           

			//verifica quais atividades foram selecionadas 
			if(atividadesSelecionadas.containsKey(atv.getCodigoAtividade())){

				//verifica se atividade está checada
				if (this.atividadesSelecionadas.get(atv.getCodigoAtividade()).booleanValue()) {

					//soma o preço da atividade, caso seja nulo ou em branco,
					//exibe mensagem informativa					
					if((atv.getPreco() == null) || (atv.getPreco().equals(""))){
						atv.setPreco("Sem Custo");
						
					} else { 
						somatoriaPreco = somatoriaPreco + Double.parseDouble(atv.getPreco());

						//troca ponto por vírgula na String que representa o preço
						atv.setPreco(String.format("%.2f", Double.parseDouble(atv.getPreco())));
					}	

					atvRelatorio.put(atv.getCodigoAtividade(), atv);
				}
			}
		}

		//caso exista algum item selecionado, realiza
		//formatação do valor
		if(!atvRelatorio.isEmpty()){

			//utiliza objeto para transportar somatória	
			Atividade atvTemp = new Atividade();
			
			//formata número para precisão de duas casas decimais
			//e insere em objeto de transporte
			atvTemp.setPreco(String.format("%.2f", somatoriaPreco));
			atvRelatorio.put(0, atvTemp);		
		}

		return atvRelatorio;
	}

	/**
	 * Método responsável por adicionar objeto atividade
	 * na lista de favoritos do viajante
	 */
	public String adicionaFavoritos(){

		String navegacao = "VIAJANTE";
		String mensagem  = "Atividade adicionada a lista de Favoritos com sucesso!";

		try {

			//tenta recuperar código da atividade na sessão			
			Integer codigoAtividade = SessaoJSF.getCodigoSessao("id");

			//caso exista codigo da atividade na sessão, realiza inserção na lista de favoritos
			if(codigoAtividade != null){
				
				this.viajante.inserirFavoritos(codigoAtividade, this.viajante.getUsuario().getCodigo());

				//exibe mensagem informando o sucesso da operação
				InfoMensagem.mensInfo("alertaPainel", mensagem);
			}			

		} catch (Exception ex) {
			navegacao = "falhaInterna";
			ex.printStackTrace();
		}
		return navegacao;

	}

	/**
	 * Ajusta informações do viajante para exibição
	 */
	public void ajustaInfoConta(){

		erroRota = false;
		isExibeBotao = true;

		try{

			//Recupera o usuario da sessao
			Usuario usuario = (Usuario) SessaoJSF.recuperarItemSessao("usuarioLogado");

			//Caso exista usuario...
			if(usuario != null){

				//recupera informações do objeto baseado no id encontrado na sessão
				this.viajante = this.viajante.selecionarPorId(usuario.getCodigo());

				//ajusta informações do painel
				FormataObjetoLayout.formataContaViajante(viajante);

				//Seleciona atividades cadastradas para o viajante corrente
				viajante.selecionarAtividadesFavoritas(viajante.getUsuario().getCodigo());

				numAtividades = viajante.getListaAtividade().size();

				//Se o numero de atividades for maior do que zero, exibe a quantidade
				if(numAtividades > 0){
					infoQtdeAtividades = String.valueOf(numAtividades);
				}

				//configura inforamações da rota
				this.configuraGoogleMaps();				

				//extrai booleano da sessão para verificar exibição do botão
				//de gravação da rota
				Object itemSessao = SessaoJSF.recuperarItemSessao("isExibeBotao");

				if(itemSessao != null){
					isExibeBotao = (Boolean) itemSessao;
				}

				//resgata mensagem de erro setada pelo objeto maps (caso exista)
				if(maps.getErroMaps()!= null)
					InfoMensagem.mensInfo("rota", maps.getErroMaps());	
			} 

		}catch(Exception e){
			infoQtdeAtividades = "Erro ao acessar a lista";
			e.printStackTrace();
		}
	}


	/**
	 * Método responsável por ajustar informações para criação
	 * do mapa
	 */
	private void configuraGoogleMaps() {

		//verifica por informações de rotas na sessão
		maps = (GoogleMaps) SessaoJSF.recuperarItemSessao(GoogleMaps.ROTA_SELECIONADA);

		if(maps == null){
			//cria objeto GoogleMaps para manipulação de rotas
			maps = new GoogleMaps(this.viajante);			
		}

		//verifica se existe conectividade com o serviço de mapas
		if(maps.testeConexao()){

			//caso não existam rotas pesquisadas seta atributo
			//utilizado na view para exibição de segmentos da página
			if(maps.carregaRota() == null){
				erroRota = true;				
			} 

		} else {					
			erroRota = true;
		}
	}	

	/**
	 * Método responsável por definir atividades que farão 
	 * parte da rota googleMaps
	 * @return 
	 */
	public String gerarRota(){

		String navegacao = null;

		try{

			List<Atividade> selecionadas = new ArrayList<Atividade>();

			for(Atividade atv : this.viajante.getListaAtividade().values()){

				//verifica atividades selecionadas
				if(atividadesSelecionadas.get(atv.getCodigoAtividade()).booleanValue())
					selecionadas.add(atv);				
			}

			//somente realiza procedimento caso exista no mínimo um e no máximo 2 itens selecionados
			if((selecionadas.size() > 0) && (selecionadas.size() < 3)){	

				if(maps == null){
					//cria objeto GoogleMaps para manipulação de rotas
					maps = new GoogleMaps(this.viajante);
				}

				if(maps.getRotaMaps() == null){
					maps.setRotaMaps(new Rota());
				}	
				
				//seta em objeto de rota do mapa atividades selecionadas pelo viajante
				maps.getRotaMaps().setAtividadeDestino(selecionadas.get(0));

				//caso exista mais de um item, seta o mesmo como origem
				if(selecionadas.size() == 2){
					maps.getRotaMaps().setAtividadeOrigem(selecionadas.get(1));				

				} else {

					//caso não exista item origem da rota significa que viajante optou por setar 
					//seu endereço como origem, então seta objeto vazio que será tratado pelo objeto maps
					Atividade atv = new Atividade();
					atv.setCodigoAtividade(0);
					maps.getRotaMaps().setAtividadeOrigem(atv);
				}

				//seta valor de código para passar pela validação
				maps.getRotaMaps().setCodigoRota(0);

				//guarda rota selecionada na sessão, ação necessária pois
				//após o request dados serão perdidos
				SessaoJSF.setParametroSessao(maps, GoogleMaps.ROTA_SELECIONADA);

				//seta exibição do botão de gravação de rota
				SessaoJSF.setParametroSessao(true, "isExibeBotao");

				//redireciona para página de exibição da rota
				navegacao = "ROTA";

			} else {

				//caso viajante não tenha selecionado atividades para gerar rota, 
				//exibe mensagem informativa 
				InfoMensagem.mensInfo("formulario", "Selecione no mínimo uma e no máximo duas atividade para geração da rota!");
			}

		} catch (Exception e) {

			navegacao = "falhaInterna";
			e.printStackTrace();
		}
		return navegacao;		
	}


	/**
	 * Método responsável por gravar a rota 
	 * selecionada pelo viajante
	 * @return
	 */
	public String salvaRota(){

		String navegacao = "VIAJANTE";
		try {
			//recupera itens selecionados da sessão
			this.maps = (GoogleMaps) SessaoJSF.recuperarItemSessao(GoogleMaps.ROTA_SELECIONADA);

			//invoca método responsável por persistir rota selecionada
			maps.salvarRota();	

			//remove itens da sessão
			SessaoJSF.removeItemSessao(GoogleMaps.ROTA_SELECIONADA);
			
			InfoMensagem.mensInfo("alertaPainel", "Rota Cadastradada com Sucesso!");

		}catch(Exception e){
			navegacao = "falhaInterna";
			e.printStackTrace();			
		}

		return navegacao;
	}


	/**
	 * Método responsável pela navegação até a lista de atividades, utiliza String de navegação
	 * para determinar o redirecionamento do usuário para a página de exibição das atividades
	 * caso não existam itens cadastrados, exibe uma mensagem  no painel informando o usuário.
	 */
	public String getLista(){

		String navegacao = "LISTAVIAJANTE";

		try {			

			//Caso o viajante nao possua atividade cadastrada, informa o mesmo 
			if(viajante.getListaAtividade().isEmpty()){

				InfoMensagem.mensInfo("alertaPainel", MENSAGEM_LISTA_VAZIA);

				//Nao ocorre o redirecionamento de pagina neste caso, pois o usuario continuará no painel 
				navegacao = null;
			} 

		} catch (Exception e) {

			navegacao = "falhaInterna";
			e.printStackTrace();
		}
		return navegacao;		
	}

	/**
	 * Adequa elementos para exibição na view
	 * 
	 * @return
	 */
	public ArrayList<Atividade> getListaOrdenada(){

		listaAtividades = new ArrayList<Atividade>();

		for(Atividade a: viajante.getListaAtividade().values()){			
			a.setNomeAtividade(FormataObjetoLayout.captulacao(a.getNomeAtividade()));		
			listaAtividades.add(a);
		}
		//inverte os resultados para refletir resultado obtido
		//na busca
		//Collections.reverse(lista);			

		return listaAtividades;
	} 

	/**
	 * Método responsável por setar visibilidade do botão de gravação antes de redirecionar
	 * para página de visualização da rota, realiza tratamente para impedir usuário de tentar
	 * gravar uma rota sem passar pelo fluxo necessário ao cadastro de rota
	 * @return
	 */
	public String exibeInfoRota(){		

		//envia parâmetro para sessão 
		SessaoJSF.setParametroSessao(false, "isExibeBotao");

		return "ROTA";		
	}

	// getters e setters
	public String getQtdAtividades() {
		return infoQtdeAtividades;
	}

	public GoogleMaps getMaps() {
		return maps;
	}

	public paginacaoResultados getP() {
		return p;
	}

	public int getMaxPorPágina() {
		return maxPorPágina;
	}

	public ArrayList<Atividade> getListaAtividades() {
		return listaAtividades;
	}

	public HashMap<Integer, Boolean> getAtividadesSelecionadas() {
		return atividadesSelecionadas;
	}

	public boolean isErroRota() {
		return erroRota;
	}

	public Viajante getViajante() {
		return viajante;
	}

	public boolean isExibeBotao() {
		return isExibeBotao;
	}
}


