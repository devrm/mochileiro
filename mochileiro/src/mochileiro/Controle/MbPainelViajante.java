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
 * @Descri��o: Classe respons�vel por gerenciar a��es do Viajante
 */
public class MbPainelViajante {

	private static final String MENSAGEM_LISTA_VAZIA = "N�o existem atividades adicionadas a sua lista de favoritos!";

	private GoogleMaps maps;

	private String infoQtdeAtividades = "Lista Vazia";
	private int    numAtividades;

	private ArrayList<Atividade> listaAtividades;

	private HashMap<Integer, Boolean> atividadesSelecionadas;

	private paginacaoResultados p;

	private final int maxPorP�gina  = 20;
	private GeraRelatorio relatorio;
	private boolean erroRota = false;

	private boolean isExibeBotao = true;

	private Viajante viajante;


	public MbPainelViajante() {

		this.viajante = new Viajante();	

		this.ajustaInfoConta();

		this.p = new paginacaoResultados();

		this.p.setMaxPorPagina(maxPorP�gina);

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

				//verifical��o necess�ria para evitar nullpointer 
				if(atividadesSelecionadas.containsKey(selecionada.getCodigoAtividade())){

					//verifica se item foi checado
					if (this.atividadesSelecionadas.get(selecionada.getCodigoAtividade()).booleanValue()){						
						//em caso positivo, realiza exclus�o
						viajante.excluiFavoritos(selecionada.getCodigoAtividade(), codigoViajante);
						isExcluiu = true;
					}
				}		
			}
			
			//caso tenha realizado exclus�o, informa o usu�rio
			if(isExcluiu){
				mensagem = "Atividade(s) exclu�da(s) da lista!";
			} else {
				mensagem = "Selecione as atividades para exclus�o";
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
	 * M�todo respons�vel por adequar informa��es para 
	 * gera��o de relat�rio de gastos
	 * @return
	 */
	public String estimarGastos(){

		String navegacao = null;
		String mensagem  = null;

		try{		

			HashMap<Integer, Atividade> listaRelatorio = this.configurarAtividadesEstimativa();

			//verifica se existem atividades selecionadas
			if(listaRelatorio.isEmpty()){			
				mensagem = "Selecione as atividades que far�o parte da estimativa!";
			} else {				
				//caso possua informa��es cria relat�rio, m�todo extraiAtividades verifica quais 
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
	 * M�todo respons�vel por identificar quais atividades foram selecionadas
	 * pelo viajante para gera��o de relat�rio
	 * @return
	 */
	private HashMap<Integer, Atividade> configurarAtividadesEstimativa(){

		HashMap<Integer, Atividade> atvRelatorio = new HashMap<Integer, Atividade>();

		Double somatoriaPreco = 0.00;

		for (Atividade atv : viajante.getListaAtividade().values()){           

			//verifica quais atividades foram selecionadas 
			if(atividadesSelecionadas.containsKey(atv.getCodigoAtividade())){

				//verifica se atividade est� checada
				if (this.atividadesSelecionadas.get(atv.getCodigoAtividade()).booleanValue()) {

					//soma o pre�o da atividade, caso seja nulo ou em branco,
					//exibe mensagem informativa					
					if((atv.getPreco() == null) || (atv.getPreco().equals(""))){
						atv.setPreco("Sem Custo");
						
					} else { 
						somatoriaPreco = somatoriaPreco + Double.parseDouble(atv.getPreco());

						//troca ponto por v�rgula na String que representa o pre�o
						atv.setPreco(String.format("%.2f", Double.parseDouble(atv.getPreco())));
					}	

					atvRelatorio.put(atv.getCodigoAtividade(), atv);
				}
			}
		}

		//caso exista algum item selecionado, realiza
		//formata��o do valor
		if(!atvRelatorio.isEmpty()){

			//utiliza objeto para transportar somat�ria	
			Atividade atvTemp = new Atividade();
			
			//formata n�mero para precis�o de duas casas decimais
			//e insere em objeto de transporte
			atvTemp.setPreco(String.format("%.2f", somatoriaPreco));
			atvRelatorio.put(0, atvTemp);		
		}

		return atvRelatorio;
	}

	/**
	 * M�todo respons�vel por adicionar objeto atividade
	 * na lista de favoritos do viajante
	 */
	public String adicionaFavoritos(){

		String navegacao = "VIAJANTE";
		String mensagem  = "Atividade adicionada a lista de Favoritos com sucesso!";

		try {

			//tenta recuperar c�digo da atividade na sess�o			
			Integer codigoAtividade = SessaoJSF.getCodigoSessao("id");

			//caso exista codigo da atividade na sess�o, realiza inser��o na lista de favoritos
			if(codigoAtividade != null){
				
				this.viajante.inserirFavoritos(codigoAtividade, this.viajante.getUsuario().getCodigo());

				//exibe mensagem informando o sucesso da opera��o
				InfoMensagem.mensInfo("alertaPainel", mensagem);
			}			

		} catch (Exception ex) {
			navegacao = "falhaInterna";
			ex.printStackTrace();
		}
		return navegacao;

	}

	/**
	 * Ajusta informa��es do viajante para exibi��o
	 */
	public void ajustaInfoConta(){

		erroRota = false;
		isExibeBotao = true;

		try{

			//Recupera o usuario da sessao
			Usuario usuario = (Usuario) SessaoJSF.recuperarItemSessao("usuarioLogado");

			//Caso exista usuario...
			if(usuario != null){

				//recupera informa��es do objeto baseado no id encontrado na sess�o
				this.viajante = this.viajante.selecionarPorId(usuario.getCodigo());

				//ajusta informa��es do painel
				FormataObjetoLayout.formataContaViajante(viajante);

				//Seleciona atividades cadastradas para o viajante corrente
				viajante.selecionarAtividadesFavoritas(viajante.getUsuario().getCodigo());

				numAtividades = viajante.getListaAtividade().size();

				//Se o numero de atividades for maior do que zero, exibe a quantidade
				if(numAtividades > 0){
					infoQtdeAtividades = String.valueOf(numAtividades);
				}

				//configura inforama��es da rota
				this.configuraGoogleMaps();				

				//extrai booleano da sess�o para verificar exibi��o do bot�o
				//de grava��o da rota
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
	 * M�todo respons�vel por ajustar informa��es para cria��o
	 * do mapa
	 */
	private void configuraGoogleMaps() {

		//verifica por informa��es de rotas na sess�o
		maps = (GoogleMaps) SessaoJSF.recuperarItemSessao(GoogleMaps.ROTA_SELECIONADA);

		if(maps == null){
			//cria objeto GoogleMaps para manipula��o de rotas
			maps = new GoogleMaps(this.viajante);			
		}

		//verifica se existe conectividade com o servi�o de mapas
		if(maps.testeConexao()){

			//caso n�o existam rotas pesquisadas seta atributo
			//utilizado na view para exibi��o de segmentos da p�gina
			if(maps.carregaRota() == null){
				erroRota = true;				
			} 

		} else {					
			erroRota = true;
		}
	}	

	/**
	 * M�todo respons�vel por definir atividades que far�o 
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

			//somente realiza procedimento caso exista no m�nimo um e no m�ximo 2 itens selecionados
			if((selecionadas.size() > 0) && (selecionadas.size() < 3)){	

				if(maps == null){
					//cria objeto GoogleMaps para manipula��o de rotas
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

					//caso n�o exista item origem da rota significa que viajante optou por setar 
					//seu endere�o como origem, ent�o seta objeto vazio que ser� tratado pelo objeto maps
					Atividade atv = new Atividade();
					atv.setCodigoAtividade(0);
					maps.getRotaMaps().setAtividadeOrigem(atv);
				}

				//seta valor de c�digo para passar pela valida��o
				maps.getRotaMaps().setCodigoRota(0);

				//guarda rota selecionada na sess�o, a��o necess�ria pois
				//ap�s o request dados ser�o perdidos
				SessaoJSF.setParametroSessao(maps, GoogleMaps.ROTA_SELECIONADA);

				//seta exibi��o do bot�o de grava��o de rota
				SessaoJSF.setParametroSessao(true, "isExibeBotao");

				//redireciona para p�gina de exibi��o da rota
				navegacao = "ROTA";

			} else {

				//caso viajante n�o tenha selecionado atividades para gerar rota, 
				//exibe mensagem informativa 
				InfoMensagem.mensInfo("formulario", "Selecione no m�nimo uma e no m�ximo duas atividade para gera��o da rota!");
			}

		} catch (Exception e) {

			navegacao = "falhaInterna";
			e.printStackTrace();
		}
		return navegacao;		
	}


	/**
	 * M�todo respons�vel por gravar a rota 
	 * selecionada pelo viajante
	 * @return
	 */
	public String salvaRota(){

		String navegacao = "VIAJANTE";
		try {
			//recupera itens selecionados da sess�o
			this.maps = (GoogleMaps) SessaoJSF.recuperarItemSessao(GoogleMaps.ROTA_SELECIONADA);

			//invoca m�todo respons�vel por persistir rota selecionada
			maps.salvarRota();	

			//remove itens da sess�o
			SessaoJSF.removeItemSessao(GoogleMaps.ROTA_SELECIONADA);
			
			InfoMensagem.mensInfo("alertaPainel", "Rota Cadastradada com Sucesso!");

		}catch(Exception e){
			navegacao = "falhaInterna";
			e.printStackTrace();			
		}

		return navegacao;
	}


	/**
	 * M�todo respons�vel pela navega��o at� a lista de atividades, utiliza String de navega��o
	 * para determinar o redirecionamento do usu�rio para a p�gina de exibi��o das atividades
	 * caso n�o existam itens cadastrados, exibe uma mensagem  no painel informando o usu�rio.
	 */
	public String getLista(){

		String navegacao = "LISTAVIAJANTE";

		try {			

			//Caso o viajante nao possua atividade cadastrada, informa o mesmo 
			if(viajante.getListaAtividade().isEmpty()){

				InfoMensagem.mensInfo("alertaPainel", MENSAGEM_LISTA_VAZIA);

				//Nao ocorre o redirecionamento de pagina neste caso, pois o usuario continuar� no painel 
				navegacao = null;
			} 

		} catch (Exception e) {

			navegacao = "falhaInterna";
			e.printStackTrace();
		}
		return navegacao;		
	}

	/**
	 * Adequa elementos para exibi��o na view
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
	 * M�todo respons�vel por setar visibilidade do bot�o de grava��o antes de redirecionar
	 * para p�gina de visualiza��o da rota, realiza tratamente para impedir usu�rio de tentar
	 * gravar uma rota sem passar pelo fluxo necess�rio ao cadastro de rota
	 * @return
	 */
	public String exibeInfoRota(){		

		//envia par�metro para sess�o 
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

	public int getMaxPorP�gina() {
		return maxPorP�gina;
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


