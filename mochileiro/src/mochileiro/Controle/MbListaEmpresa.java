package mochileiro.Controle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.faces.component.html.HtmlDataTable;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import mochileiro.Modelo.Atividade;
import mochileiro.Modelo.Empresa;
import mochileiro.Modelo.Usuario;
import mochileiro.Util.CategoriaComparator;
import mochileiro.Util.DataCadastroComparator;
import mochileiro.Util.FormataObjetoLayout;
import mochileiro.Util.InfoMensagem;
import mochileiro.Util.SessaoJSF;
import mochileiro.Util.paginacaoResultados;

/**
 * @author Thiago
 * @projeto mochileiro
 * @Data 20/12/2009 @Hora 17:50:26
 * @Descri��o: Classe respons�vel pelas informa��es visualizadas na view lista - empresa
 */
public class MbListaEmpresa {

	private Empresa empresa;
	private List<Atividade> listaAtividadesEmpresa; 
	private DataModel model;

	//atributos de pagina��o
	private final int maxPorPagina  = 3;
	private paginacaoResultados p;
	private int numeroRegistros;
	private static String tipoOrdenacao = "";
	private String navegacao = "LISTAEMPRESA";

	public MbListaEmpresa(){
		
		Usuario usuario = (Usuario) SessaoJSF.recuperarItemSessao("usuarioLogado");
		this.empresa = new Empresa(usuario);
		
		//inst�ncia objeto de pagina��o
		this.p = new paginacaoResultados();	
		
		//configura n�mero de elementos por p�gina
		p.setMaxPorPagina(maxPorPagina);
		
	}

	/**
	 * M�todo de neg�cio respons�vel por carregar atividades cadastradas pela empresa
	 * 
	 * @return
	 */
	private void carregaLista(){
		
		try{
			
			//invoca m�todo de sele��o passando id empresa/usu�rio como par�metro
			empresa.selecionarAtividades(empresa.getUsuario().getCodigo());

			listaAtividadesEmpresa = new ArrayList<Atividade>();		

			//extrai objetos da lista para formata��o
			for(Atividade atv : empresa.getListaAtividade().values()){
				
				//invoco m�todo de configura��o para adequar elementos conforme
				//layout definido na view
				FormataObjetoLayout.formataExibicaoAtividade(atv, FormataObjetoLayout.LAYOUT_LISTA);			
					
				//alimento lista que ser� exibida na view
				listaAtividadesEmpresa.add(atv);					
			}
		//determina ordena��o escolhida na view
		if(tipoOrdenacao.equals("categoria"))
			this.ordenaPorCategoria();			
		else 
			this.ordenaPorData();
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	/**
	 * M�todo p�blico utilizado como interface para m�todo privado de ordena��o por categoria
	 * @return
	 */
	public String ordenaCat(){
		//valida��o necess�ria para evitar erro JSF: 1007
		if(tipoOrdenacao.equals("categoria")){
		navegacao = null;
		} else {
			tipoOrdenacao = "categoria";
		}
		return navegacao;
	}
	
	/**
	 * M�todo p�blico utilizado como interface para m�todo privado de ordena��o por data
	 * @return
	 */
	public String ordenaCad(){

		if(tipoOrdenacao.equals("data")){
		navegacao = null;
		} else {
			tipoOrdenacao = "data";
		}
		
		p.setDataTable(new HtmlDataTable());
		
		return navegacao;
	}

	
	/**
	 * Realiza ordena��o de List por data
	 */
	private void ordenaPorData(){		
		//ordena por data
		Collections.sort(listaAtividadesEmpresa, new DataCadastroComparator());
		//inverte a ordena��o para exibi��o das datas mais recentes primeiro
		Collections.reverse(listaAtividadesEmpresa);
	}
	
	/**
	 * Realiza Ordena��o por Categoria
	 */
	private void ordenaPorCategoria(){		
		Collections.sort(listaAtividadesEmpresa, new CategoriaComparator());
	}


	/**
	 * M�todo p�blico respons�vel pela navega��o at� a lista de atividades, utiliza String de navaga��o
	 * para determinar o redirecionamento do usu�rio para a p�gina de exibi��o das atividades
	 *  caso n�o existam itens cadastrados, exibe uma mensagem  no painel informando o usu�rio sobre a situa��o
	 *  
	 * @return
	 */
	public String getLista(){

		String sMensagem = "N�o existem itens para exibi��o, acesse a op��o Cadastrar atividade " +
		"e adicione uma nova atividade a sua lista!";

		String sListaOk = ""; 				  

		try {			
			
			//invoca m�todo de sele��o passando id empresa/usu�rio como par�metro
			empresa.selecionarAtividades(empresa.getUsuario().getCodigo());
			numeroRegistros = empresa.getListaAtividade().size();
			
			//limpo campo ordena��o para evitar vest�gios de navega��es anteriores
			MbListaEmpresa.tipoOrdenacao = "";

			if(numeroRegistros > 0){
				//caso lista contenha itens, atualiza String de navega��o
				//que ser� usada para redirecionar usu�rio para lista de atividades
				sListaOk = navegacao;
			} else {
				//exibe mensagem ao usu�rio informando que n�o existem registros
				InfoMensagem.mensInfo(sMensagem);
			}
		} catch (Exception e) {

			sListaOk = "falhaInterna";
			e.printStackTrace();
		}
		return sListaOk;		
	}	
	

	/**
	 * Getter especial respons�vel por chamar o m�todo de neg�cio
	 * que cria a lista de atividades da empresa
	 * @see #carregaLista()
	 * @return
	 */
	public DataModel getModel() {
		this.carregaLista();		
		model = new ListDataModel(listaAtividadesEmpresa);
		
		return 	model;
	}

	public paginacaoResultados getP() {
		return p;
	}

	public int getMaxPorPagina() {
		return maxPorPagina;
	}	

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}
}
