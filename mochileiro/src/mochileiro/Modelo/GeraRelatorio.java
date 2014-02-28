package mochileiro.Modelo;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import mochileiro.Util.SessaoJSF;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * 
 * @author $h@rk
 * @projeto mochileiro
 * @Data 20/05/2010 @Hora 22:58:29
 * @Descrição: Classe responsável por gerar os relatórios na aplicação
 */
public class GeraRelatorio {
	
	//tipo de relatório
	public static final int RELATORIO_RESUMO_EMPRESA = 1;
	public static final int RELATORIO_RESUMO_TODAS 	 = 2;
	public static final int RELATORIO_CATEGORIAS     = 3;	
	public static final int RELATORIO_PARCERIAS      = 4;
	public static final int RELATORIO_ESTIMATIVA	 = 5;
	
	//coleção utilizada para reter elementos de exibição no relatório
	private List<Atividade> colecaoAtividade;
	
	//coleção de objetos Categoria
	private List<Categoria> colecaoCategoria;
	
	//coleção de objetos Categoria
	private List<Empresa> colecaoEmpresa;
	
	//arquivos de template
	private final String templateResumo 	= "relatorioResumo2.jasper";
	
	private final String templateCategoria  = "relatorioCategoria.jasper";
	
	private final String templateParceria 	= "relatorioParcerias.jasper";
	
	private final String templateEstimativa = "relatorioEstimativa.jasper";
	
	private String templateJasper;

	//configura informações do relatório
	HashMap<String, String> parametros = new HashMap<String, String>();
	
	//seta o titulo do relatório no template
	private String tituloRelatorio;
	
	private String relatorioResumo = "Relatório Qualificação das Atividades";

	private int relatorioSelecionado;

	private String subtitulo = "";

	/**
	 * Construtor utilizado na criação do relatório de atividades
	 * @param nomeEntidade
	 * @param tipoRelatorio
	 * @param atv
	 * @throws Exception
	 */
	public GeraRelatorio(String nomeEntidade, int tipoRelatorio, HashMap<Integer, Atividade> atv) throws Exception{

		this.subtitulo = nomeEntidade;
		
		this.relatorioSelecionado = tipoRelatorio;
		
		this.colecaoAtividade = this.converteHashAtividade(atv);
	}
	
	/**
	 * Construtor utilizado na criação do relatório de categorias
	 * 
	 * @param tipoRelatorio
	 * @param catRel
	 * @throws Exception 
	 */
	public GeraRelatorio(int tipoRelatorio, HashMap<Integer, Categoria> catRel) throws Exception {
		
		this.relatorioSelecionado = tipoRelatorio;
		
		this.colecaoCategoria = this.converteHashCategoria(catRel);
	}
	
		
	/**
	 * Construtor utilizado na criação do relatório de parcerias
	 * 
	 * @param tipoRelatorio
	 * @param listaEmpresa
	 */
	public GeraRelatorio(int tipoRelatorio, List<Empresa> listaEmpresa) {
		
		this.relatorioSelecionado = tipoRelatorio;
		
		this.colecaoEmpresa = listaEmpresa;
	}

	/**
	 * Cria o relatório e envia para o browser
	 * @throws Exception
	 */
	public void criaRelatorio() throws Exception{

		//resgata contexto para manipulação do arquivo pdf
		FacesContext context = FacesContext.getCurrentInstance();		 

		try { 
			
			//configura o tipo de relatório selecionado
			List<Object> infoRelatorio = this.configuraRelatorio();

			//resgata response para saída (geração de pdf)
			HttpServletResponse response = SessaoJSF.getResponseServlet();

			//envia a resposta com o MIME Type PDF
			response.setContentType("application/pdf");
			
			//utilizado para manipular arquivo 
			ServletOutputStream servletOutputStream = response.getOutputStream();
			
			//resgata o template do relatório (template definido em configuraRelatorio())
			InputStream reportStream = context.getExternalContext().getResourceAsStream("\\configuracao\\relatorios\\"+this.templateJasper);
			
			//dataSource utilizado pelo jasper encapsulando informações que serão utilizadas no relatório
			JRBeanCollectionDataSource fonteDados = new JRBeanCollectionDataSource(infoRelatorio);

			//força download do relatório
			response.setHeader("Content-disposition", "attachment;filename=relatorio"+System.currentTimeMillis()+".pdf");
			
			//envia para o navegador o PDF gerado
			JasperRunManager.runReportToPdfStream(reportStream, servletOutputStream, parametros, fonteDados);
			
			//limpa servlet e finaliza
			servletOutputStream.flush();
			servletOutputStream.close();
			
			reportStream.close();
			
		} finally {
			
			//evita erro do JSF após completar a geração do relatório
			//avisando o FacesContext que a resposta está completa
			context.responseComplete();
		}
	}

	/**
	 * Configura o tipo de relatório que será criado
	 * @return
	 * @throws Exception
	 */
	private List<Object> configuraRelatorio() throws Exception{
		
		List<Object> dados = new ArrayList<Object>();
		
		//limpa map 
		parametros.clear();

		//define relatório que será gerado
		if(this.relatorioSelecionado == RELATORIO_RESUMO_EMPRESA){	
			
			//seta titulo			
			this.tituloRelatorio = relatorioResumo;
			
			//define o template para o tipo de relatório escolhido
			this.templateJasper = templateResumo;
			
			//configura relatório selecionado
			this.configuraRelatorioResumo(dados);
			
			if(this.subtitulo == null)
				this.subtitulo = " Todas as Atividades Cadastradas no Sistema ";
			
			//define título do relatório
			parametros.put("TITULO",    this.tituloRelatorio);
			parametros.put("SUBTITULO", this.subtitulo);

		
		} else if(this.relatorioSelecionado == RELATORIO_CATEGORIAS){
			
			//define o template para o tipo de relatório escolhido
			this.templateJasper = templateCategoria;	
			
			//configura informações da Categoria
			this.configuraRelatorioCategorias(dados);
		
		} else if(this.relatorioSelecionado == RELATORIO_PARCERIAS){
			
			//define o template para o tipo de relatório escolhido
			this.templateJasper = templateParceria;		
			
			//configura relatório de parcerias
			this.configuraRelatorioParcerias(dados);
		}
		
		else if(this.relatorioSelecionado == RELATORIO_ESTIMATIVA){
			
			//define o template para o tipo de relatório escolhido
			this.templateJasper = templateEstimativa;	
			
			//configura relatório de parcerias
			this.configuraRelatorioEstimativa(dados);
			
			//extrai objeto com indice zero contendo a somatória das atividades
			parametros.put("totalEstimado", this.colecaoAtividade.get(0).getPreco());
			
			//envia nome do viajante que está realizando a operação
			parametros.put("nomeViajante", this.subtitulo);
		}
		
		return dados;
	}
	
	/**
	 * Método responsável por setar informações no hashMap
	 * utilizado pelo JasperReports, seta colunas definidas
	 * no template
	 * 
	 * @param colunasRelatorio
	 * @param dados
	 * @return]
	 * @throws Exception
	 */
	private List<Object> configuraRelatorioResumo(List<Object> dados) throws Exception{	
		
		HashMap<String, Object> colunasRelatorio = null;
		
		//define campos do relatório e guarda informações no hashMap
		for(Atividade atv : this.colecaoAtividade){
			
			colunasRelatorio = new HashMap<String, Object>();
			
			colunasRelatorio.put("nomeAtividade",		 atv.getNomeAtividade());	
			colunasRelatorio.put("nomeCategoria", 		 atv.getCategoria().getNome());
			colunasRelatorio.put("qualificacaoPositiva", atv.getQualificacoesPositivas());	
			colunasRelatorio.put("qualificacaoNegativa", atv.getQualificacoesNegativas());	
			colunasRelatorio.put("parceria",			 atv.getCodigoParceiro() == 0 ? "Não" : "Sim");
			colunasRelatorio.put("adicionada", 			 atv.getAdicionada());				

			dados.add(colunasRelatorio);
		}

		return dados;
	}
	
	/**
	 * Configura relatório de parcerias
	 * @param dados
	 * @return
	 * @throws Exception
	 */
	private List<Object> configuraRelatorioParcerias(List<Object> dados) throws Exception{	
		
		HashMap<String, Object> colunasRelatorio = null;
		
		//define campos do relatório e guarda informações no hashMap
		for(Empresa emp : this.colecaoEmpresa){
			
			colunasRelatorio = new HashMap<String, Object>();
			
			colunasRelatorio.put("razao_social",		 emp.getNomeEmpresa());	
			colunasRelatorio.put("home_page", 		 	 emp.getHomePage());
			colunasRelatorio.put("parcerias",			 emp.getAdicionada());	

			dados.add(colunasRelatorio);
		}

		return dados;
	}
	

	
	/**
	 * Configura colunas do relatório de categorias
	 * @param dados
	 * @return
	 * @throws Exception
	 */
	private List<Object> configuraRelatorioCategorias(List<Object> dados) throws Exception{	
		
		HashMap<String, Object> colunasRelatorio = null;
		
		//define campos do relatório e guarda informações no hashMap
		for(Categoria cat : this.colecaoCategoria){
			
			colunasRelatorio = new HashMap<String, Object>();
			
			//seta coluna - valor
			colunasRelatorio.put("nome",		cat.getNome());	
			colunasRelatorio.put("descricao", 	cat.getDescricao());
			colunasRelatorio.put("tipo", 		cat.getTipo());	
			colunasRelatorio.put("adicionada", 	cat.getAdicao());				

			dados.add(colunasRelatorio);
		}

		return dados;
	}	
	
	
	private List<Object> configuraRelatorioEstimativa(List<Object> dados) throws Exception{	
		
		HashMap<String, Object> colunasRelatorio = null;
		
		//define campos do relatório e guarda informações no hashMap
		for(Atividade atv : this.colecaoAtividade){
			
			//evita inserção de objeto de somatória
			if(atv.getCodigoAtividade() != null){
			
				colunasRelatorio = new HashMap<String, Object>();
				
				colunasRelatorio.put("nomeAtividade",		 atv.getNomeAtividade());	
				colunasRelatorio.put("nomeCategoria", 		 atv.getCategoria().getNome());
				colunasRelatorio.put("custo", 			     atv.getPreco());			
				dados.add(colunasRelatorio);
			}
		}

		return dados;
	}
	
	/**
	 * Converte HashMap em List de Atividades
	 * @param listaCat
	 * @return
	 * @throws Exception
	 */
	private List<Categoria> converteHashCategoria(HashMap<Integer,Categoria> listaCat) throws Exception{

		List<Categoria> b = new ArrayList<Categoria>();
		
		for(Categoria a : listaCat.values()){
			b.add(a);
		}
		
		return b;		
	}

	/**
	 * converte HashMap em list de Categorias
	 * @param listaEmpresa
	 * @return
	 * @throws Exception
	 */
	private List<Atividade> converteHashAtividade(HashMap<Integer,Atividade> listaEmpresa) throws Exception{

		List<Atividade> b = new ArrayList<Atividade>();
		
		for(Atividade a : listaEmpresa.values()){
			b.add(a);
		}
		
		return b;		
	}
}
