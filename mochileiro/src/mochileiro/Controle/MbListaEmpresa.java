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
 * @Descrição: Classe responsável pelas informações visualizadas na view lista - empresa
 */
public class MbListaEmpresa {

	private Empresa empresa;
	private List<Atividade> listaAtividadesEmpresa; 
	private DataModel model;

	//atributos de paginação
	private final int maxPorPagina  = 3;
	private paginacaoResultados p;
	private int numeroRegistros;
	private static String tipoOrdenacao = "";
	private String navegacao = "LISTAEMPRESA";

	public MbListaEmpresa(){
		
		Usuario usuario = (Usuario) SessaoJSF.recuperarItemSessao("usuarioLogado");
		this.empresa = new Empresa(usuario);
		
		//instância objeto de paginação
		this.p = new paginacaoResultados();	
		
		//configura número de elementos por página
		p.setMaxPorPagina(maxPorPagina);
		
	}

	/**
	 * Método de negócio responsável por carregar atividades cadastradas pela empresa
	 * 
	 * @return
	 */
	private void carregaLista(){
		
		try{
			
			//invoca método de seleção passando id empresa/usuário como parâmetro
			empresa.selecionarAtividades(empresa.getUsuario().getCodigo());

			listaAtividadesEmpresa = new ArrayList<Atividade>();		

			//extrai objetos da lista para formatação
			for(Atividade atv : empresa.getListaAtividade().values()){
				
				//invoco método de configuração para adequar elementos conforme
				//layout definido na view
				FormataObjetoLayout.formataExibicaoAtividade(atv, FormataObjetoLayout.LAYOUT_LISTA);			
					
				//alimento lista que será exibida na view
				listaAtividadesEmpresa.add(atv);					
			}
		//determina ordenação escolhida na view
		if(tipoOrdenacao.equals("categoria"))
			this.ordenaPorCategoria();			
		else 
			this.ordenaPorData();
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	/**
	 * Método público utilizado como interface para método privado de ordenação por categoria
	 * @return
	 */
	public String ordenaCat(){
		//validação necessária para evitar erro JSF: 1007
		if(tipoOrdenacao.equals("categoria")){
		navegacao = null;
		} else {
			tipoOrdenacao = "categoria";
		}
		return navegacao;
	}
	
	/**
	 * Método público utilizado como interface para método privado de ordenação por data
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
	 * Realiza ordenação de List por data
	 */
	private void ordenaPorData(){		
		//ordena por data
		Collections.sort(listaAtividadesEmpresa, new DataCadastroComparator());
		//inverte a ordenação para exibição das datas mais recentes primeiro
		Collections.reverse(listaAtividadesEmpresa);
	}
	
	/**
	 * Realiza Ordenação por Categoria
	 */
	private void ordenaPorCategoria(){		
		Collections.sort(listaAtividadesEmpresa, new CategoriaComparator());
	}


	/**
	 * Método público responsável pela navegação até a lista de atividades, utiliza String de navagação
	 * para determinar o redirecionamento do usuário para a página de exibição das atividades
	 *  caso não existam itens cadastrados, exibe uma mensagem  no painel informando o usuário sobre a situação
	 *  
	 * @return
	 */
	public String getLista(){

		String sMensagem = "Não existem itens para exibição, acesse a opção Cadastrar atividade " +
		"e adicione uma nova atividade a sua lista!";

		String sListaOk = ""; 				  

		try {			
			
			//invoca método de seleção passando id empresa/usuário como parâmetro
			empresa.selecionarAtividades(empresa.getUsuario().getCodigo());
			numeroRegistros = empresa.getListaAtividade().size();
			
			//limpo campo ordenação para evitar vestígios de navegações anteriores
			MbListaEmpresa.tipoOrdenacao = "";

			if(numeroRegistros > 0){
				//caso lista contenha itens, atualiza String de navegação
				//que será usada para redirecionar usuário para lista de atividades
				sListaOk = navegacao;
			} else {
				//exibe mensagem ao usuário informando que não existem registros
				InfoMensagem.mensInfo(sMensagem);
			}
		} catch (Exception e) {

			sListaOk = "falhaInterna";
			e.printStackTrace();
		}
		return sListaOk;		
	}	
	

	/**
	 * Getter especial responsável por chamar o método de negócio
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
