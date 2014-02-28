package mochileiro.Controle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.faces.component.html.HtmlDataTable;
import javax.faces.model.SelectItem;

import mochileiro.Modelo.Atividade;
import mochileiro.Modelo.Categoria;
import mochileiro.Modelo.Usuario;
import mochileiro.Util.Data;
import mochileiro.Util.FormataObjetoLayout;
import mochileiro.Util.InfoMensagem;
import mochileiro.Util.SessaoJSF;
import mochileiro.Util.paginacaoResultados;

public class MbBusca {

	private ArrayList<Atividade> 					listaAtividadeFormatada; 
	private Atividade 								atividadeSelecionada;
	private ArrayList<Atividade> 					listaAtividade;

	//atributos de pagina��o
	private paginacaoResultados 					paginacao;
	private int 									numeroRegistros;
	private String 									qualificacao;
	private HashMap<Integer,Categoria> 				catList;
	private List<SelectItem> 						listaSelect;
	private Atividade 								atividadeParametros;
	private final int								maxPorPagina  		 = 3;

	private Usuario 								usuario;

	private ArrayList<String> jsArray;

	private String menuUsuario = "";

	public MbBusca() {
		configuraBusca();
	}


	/**
	 * reinicializa resultado da busca
	 * @return
	 */
	public  String acessaBusca(){

		this.configuraBusca();

		return "BUSCA";
	}

	/**
	 * M�todo respons�vel por inicializar elementos que comp�em 
	 * a busca
	 * 
	 */
	private void configuraBusca() {

		//reinicializa atributos para evitar vest�gios
		//na sess�o
		usuario = (Usuario)SessaoJSF.recuperarItemSessao("usuarioLogado");

		listaSelect = carregaCategorias();

		paginacao = new paginacaoResultados();

		paginacao.setMaxPorPagina(maxPorPagina);

		atividadeParametros = new Atividade();

		menuUsuario = this.configuraMenu();	
	}

	private List<SelectItem> carregaCategorias(){

		Categoria categoria = new Categoria();

		//Array JSF usado para preencher combobox
		setListaSelect(new ArrayList<SelectItem>());

		try {
			//recupera informa��es persistidas no banco de dados
			catList = categoria.exibeCategorias();

			//caso exista retorno de dados do BD
			if(catList != null){

				getListaSelect().add(new SelectItem(0, "TODAS"));

				//array de elementos enviados para o javascript
				jsArray = new ArrayList<String>();

				//itera sobre array catList...
				for(Categoria cat : catList.values()){	

					//alimentando array selectItem 
					getListaSelect().add(new SelectItem(cat.getCodigo(), cat.getNome()));

					//alimenta array usado pelo javascript
					jsArray.add("['"+cat.getCodigo()+"|', '"+cat.getDescricao()+"|' , '"+cat.getTipo()+"|']");

				}
			}
		}catch (Exception ex){	    
			getListaSelect().add(new SelectItem(0, "TODAS"));
			ex.printStackTrace();
		}
		return getListaSelect();
	}

	public String buscar() { 

		String navegacao = null;

		listaAtividadeFormatada = new ArrayList<Atividade>();

		listaAtividade = new ArrayList<Atividade>();

		try {
			
			paginacao.setDataTable(new HtmlDataTable());			

			//verifica se datas foram preenchidas e s�o v�lidas
			if(this.validaDatas()){

				listaAtividade = atividadeParametros.buscar(atividadeParametros.getNomeAtividade(), 
						atividadeParametros.getEndereco().getLogradouro(), 
						atividadeParametros.getEndereco().getBairro(), 
						atividadeParametros.getEndereco().getComplemento(), 
						atividadeParametros.getEndereco().getNumero(), 
						atividadeParametros.getEndereco().getCep(), 
						atividadeParametros.getCategoria().getCodigo(), 
						atividadeParametros.getDataInicial(), 
						atividadeParametros.getDataFinal());

				//extrai objetos da lista para formata��o
				if (listaAtividade.size() > 0) {
					for(Atividade atv : listaAtividade ){

						//Carrega as classifica��es da atividade
						atv.selecionaComentarios(atv.getCodigoAtividade());

						//Seta o total de qualifica��es para a atividade
						atv.getTotalQualificacao();

						//invoco m�todo de configura��o para adequar elementos conforme
						//layout definido na view
						FormataObjetoLayout.formataExibicaoAtividade(atv, FormataObjetoLayout.LAYOUT_LISTA);			

						//alimento lista que ser� exibida na view
						listaAtividadeFormatada.add(atv);	
					}
					
					//inverte a ordem para refletir resultado da busca
					Collections.reverse(listaAtividadeFormatada);
					
				} else {
					InfoMensagem.mensInfo("busca", "N�o foram encontradas atividades com o(s) dado(s) informado(s)".toUpperCase());
				}
			}		

			//seta destino
			navegacao = "RESULTADOBUSCA";
			
			//limpa vest�gios da sess�o
			atividadeParametros = new Atividade();
			
		} catch (Exception ex) {
			ex.printStackTrace();
			navegacao = "falhaInterna";
		}

		return navegacao;
	}

	/**
	 * Determina menu conforme usu�rio logado
	 * @return
	 */
	private String configuraMenu(){
		String menu ="";

		if(usuario != null){

			if(usuario.getTipo().equalsIgnoreCase("EMPRESA"))
				menu = "operacoes_empresa.xhtml";

			else if(usuario.getTipo().equalsIgnoreCase("VIAJANTE"))
				menu = "operacoes_viajante.xhtml";
		}

		return menu;
	}

	private boolean validaDatas(){
		boolean isValidas = true;
		boolean isDtInicialValida = true;
		boolean isDtFinalValida = true;
		String mensagemErro = "";


		//resgata datas do per�odo de disponibilidade para manipula��o
		Data dtInicio = this.atividadeParametros.getDataInicial();
		Data dtFim	  = this.atividadeParametros.getDataFinal();

			//caso data inicial esteja preenchida realiza valida��o 
			if (!dtInicio.dataCompleta(false).equals("")){

				//verifica se s�o datas v�lidas
				if(!dtInicio.validaDataHora(dtInicio.dataCompleta(false), Data.PADRAO_DATA_BR)){				
					isDtInicialValida = false;
					isValidas = false;
					mensagemErro = "Data de disponibilidade inicial � inv�lida!";			 
				}

			} else {				
				//assume falso caso data esteja vazia para impedir a valida��o
				//de per�odo
				isDtInicialValida = false;
			}	
				
			//caso data final esteja preenchida realiza valida��o 
			if (!dtFim.dataCompleta(false).equals("")){

				if(!dtFim.validaDataHora(dtFim.dataCompleta(false), Data.PADRAO_DATA_BR)){
					isDtFinalValida = false;
					isValidas = false;
					mensagemErro = "Data de disponibilidade final � inv�lida!";
				}
			}

			//caso ambas as datas sejam v�lidas, ent�o inicia valida��o de per�odo 
			//procurado � v�lido
			if((isDtInicialValida == true) && (isDtFinalValida == true)){

				//limpa campo de mensagem para evitar vest�gios
				mensagemErro = "";

				//invoca m�todo de compara��o de datas e recebe c�digo da opera��o
				int erroComparacao = dtInicio.comparaDatas(dtInicio, dtFim);

				//caso data final seja menor que data inicial
				if(erroComparacao == Data.DT_INICIAL_MAIOR_FINAL){
					mensagemErro = "Data de disponibilidade final n�o deve ser inferior a data inicial";
					isValidas = false;
				} 
			} 			

			//envia a mensagem de erro, caso exista
			InfoMensagem.mensInfo("data", mensagemErro);
		
		return isValidas;		
	}

	//getters e setters
	public void setNumeroRegistros(int numeroRegistros) {
		this.numeroRegistros = numeroRegistros;
	}


	public int getNumeroRegistros() {
		return numeroRegistros;
	}

	public void setQualificacao(String qualificacao) {
		this.qualificacao = qualificacao;
	}

	public String getQualificacao() {
		return qualificacao;
	}


	public void setAtividadeParametros(Atividade atividadeParametros) {
		this.atividadeParametros = atividadeParametros;
	}

	public Atividade getAtividadeParametros() {
		return atividadeParametros;
	}

	public void setListaSelect(List<SelectItem> listaSelect) {
		this.listaSelect = listaSelect;
	}

	public List<SelectItem> getListaSelect() {
		return listaSelect;
	}


	public void setPaginacao(paginacaoResultados paginacao) {
		this.paginacao = paginacao;
	}

	public paginacaoResultados getPaginacao() {
		return paginacao;
	}

	public int getMaxPorPagina() {
		return maxPorPagina;
	}


	public void setAtividadeSelecionada(Atividade atividadeSelecionada) {
		this.atividadeSelecionada = atividadeSelecionada;
	}


	public Atividade getAtividadeSelecionada() {
		return atividadeSelecionada;
	}


	public ArrayList<Atividade> getListaAtividadeFormatada() {
		return listaAtividadeFormatada;
	}


	public String getMenuUsuario() {
		return menuUsuario;
	}


	public ArrayList<String> getJsArray() {
		return jsArray;
	}



}
