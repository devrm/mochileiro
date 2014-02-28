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
 * @Descri��o: Classe respons�vel por gerar os relat�rios na aplica��o
 */
public class GeraRelatorio {
	
	//tipo de relat�rio
	public static final int RELATORIO_RESUMO_EMPRESA = 1;
	public static final int RELATORIO_RESUMO_TODAS 	 = 2;
	public static final int RELATORIO_CATEGORIAS     = 3;	
	public static final int RELATORIO_PARCERIAS      = 4;
	public static final int RELATORIO_ESTIMATIVA	 = 5;
	
	//cole��o utilizada para reter elementos de exibi��o no relat�rio
	private List<Atividade> colecaoAtividade;
	
	//cole��o de objetos Categoria
	private List<Categoria> colecaoCategoria;
	
	//cole��o de objetos Categoria
	private List<Empresa> colecaoEmpresa;
	
	//arquivos de template
	private final String templateResumo 	= "relatorioResumo2.jasper";
	
	private final String templateCategoria  = "relatorioCategoria.jasper";
	
	private final String templateParceria 	= "relatorioParcerias.jasper";
	
	private final String templateEstimativa = "relatorioEstimativa.jasper";
	
	private String templateJasper;

	//configura informa��es do relat�rio
	HashMap<String, String> parametros = new HashMap<String, String>();
	
	//seta o titulo do relat�rio no template
	private String tituloRelatorio;
	
	private String relatorioResumo = "Relat�rio Qualifica��o das Atividades";

	private int relatorioSelecionado;

	private String subtitulo = "";

	/**
	 * Construtor utilizado na cria��o do relat�rio de atividades
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
	 * Construtor utilizado na cria��o do relat�rio de categorias
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
	 * Construtor utilizado na cria��o do relat�rio de parcerias
	 * 
	 * @param tipoRelatorio
	 * @param listaEmpresa
	 */
	public GeraRelatorio(int tipoRelatorio, List<Empresa> listaEmpresa) {
		
		this.relatorioSelecionado = tipoRelatorio;
		
		this.colecaoEmpresa = listaEmpresa;
	}

	/**
	 * Cria o relat�rio e envia para o browser
	 * @throws Exception
	 */
	public void criaRelatorio() throws Exception{

		//resgata contexto para manipula��o do arquivo pdf
		FacesContext context = FacesContext.getCurrentInstance();		 

		try { 
			
			//configura o tipo de relat�rio selecionado
			List<Object> infoRelatorio = this.configuraRelatorio();

			//resgata response para sa�da (gera��o de pdf)
			HttpServletResponse response = SessaoJSF.getResponseServlet();

			//envia a resposta com o MIME Type PDF
			response.setContentType("application/pdf");
			
			//utilizado para manipular arquivo 
			ServletOutputStream servletOutputStream = response.getOutputStream();
			
			//resgata o template do relat�rio (template definido em configuraRelatorio())
			InputStream reportStream = context.getExternalContext().getResourceAsStream("\\configuracao\\relatorios\\"+this.templateJasper);
			
			//dataSource utilizado pelo jasper encapsulando informa��es que ser�o utilizadas no relat�rio
			JRBeanCollectionDataSource fonteDados = new JRBeanCollectionDataSource(infoRelatorio);

			//for�a download do relat�rio
			response.setHeader("Content-disposition", "attachment;filename=relatorio"+System.currentTimeMillis()+".pdf");
			
			//envia para o navegador o PDF gerado
			JasperRunManager.runReportToPdfStream(reportStream, servletOutputStream, parametros, fonteDados);
			
			//limpa servlet e finaliza
			servletOutputStream.flush();
			servletOutputStream.close();
			
			reportStream.close();
			
		} finally {
			
			//evita erro do JSF ap�s completar a gera��o do relat�rio
			//avisando o FacesContext que a resposta est� completa
			context.responseComplete();
		}
	}

	/**
	 * Configura o tipo de relat�rio que ser� criado
	 * @return
	 * @throws Exception
	 */
	private List<Object> configuraRelatorio() throws Exception{
		
		List<Object> dados = new ArrayList<Object>();
		
		//limpa map 
		parametros.clear();

		//define relat�rio que ser� gerado
		if(this.relatorioSelecionado == RELATORIO_RESUMO_EMPRESA){	
			
			//seta titulo			
			this.tituloRelatorio = relatorioResumo;
			
			//define o template para o tipo de relat�rio escolhido
			this.templateJasper = templateResumo;
			
			//configura relat�rio selecionado
			this.configuraRelatorioResumo(dados);
			
			if(this.subtitulo == null)
				this.subtitulo = " Todas as Atividades Cadastradas no Sistema ";
			
			//define t�tulo do relat�rio
			parametros.put("TITULO",    this.tituloRelatorio);
			parametros.put("SUBTITULO", this.subtitulo);

		
		} else if(this.relatorioSelecionado == RELATORIO_CATEGORIAS){
			
			//define o template para o tipo de relat�rio escolhido
			this.templateJasper = templateCategoria;	
			
			//configura informa��es da Categoria
			this.configuraRelatorioCategorias(dados);
		
		} else if(this.relatorioSelecionado == RELATORIO_PARCERIAS){
			
			//define o template para o tipo de relat�rio escolhido
			this.templateJasper = templateParceria;		
			
			//configura relat�rio de parcerias
			this.configuraRelatorioParcerias(dados);
		}
		
		else if(this.relatorioSelecionado == RELATORIO_ESTIMATIVA){
			
			//define o template para o tipo de relat�rio escolhido
			this.templateJasper = templateEstimativa;	
			
			//configura relat�rio de parcerias
			this.configuraRelatorioEstimativa(dados);
			
			//extrai objeto com indice zero contendo a somat�ria das atividades
			parametros.put("totalEstimado", this.colecaoAtividade.get(0).getPreco());
			
			//envia nome do viajante que est� realizando a opera��o
			parametros.put("nomeViajante", this.subtitulo);
		}
		
		return dados;
	}
	
	/**
	 * M�todo respons�vel por setar informa��es no hashMap
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
		
		//define campos do relat�rio e guarda informa��es no hashMap
		for(Atividade atv : this.colecaoAtividade){
			
			colunasRelatorio = new HashMap<String, Object>();
			
			colunasRelatorio.put("nomeAtividade",		 atv.getNomeAtividade());	
			colunasRelatorio.put("nomeCategoria", 		 atv.getCategoria().getNome());
			colunasRelatorio.put("qualificacaoPositiva", atv.getQualificacoesPositivas());	
			colunasRelatorio.put("qualificacaoNegativa", atv.getQualificacoesNegativas());	
			colunasRelatorio.put("parceria",			 atv.getCodigoParceiro() == 0 ? "N�o" : "Sim");
			colunasRelatorio.put("adicionada", 			 atv.getAdicionada());				

			dados.add(colunasRelatorio);
		}

		return dados;
	}
	
	/**
	 * Configura relat�rio de parcerias
	 * @param dados
	 * @return
	 * @throws Exception
	 */
	private List<Object> configuraRelatorioParcerias(List<Object> dados) throws Exception{	
		
		HashMap<String, Object> colunasRelatorio = null;
		
		//define campos do relat�rio e guarda informa��es no hashMap
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
	 * Configura colunas do relat�rio de categorias
	 * @param dados
	 * @return
	 * @throws Exception
	 */
	private List<Object> configuraRelatorioCategorias(List<Object> dados) throws Exception{	
		
		HashMap<String, Object> colunasRelatorio = null;
		
		//define campos do relat�rio e guarda informa��es no hashMap
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
		
		//define campos do relat�rio e guarda informa��es no hashMap
		for(Atividade atv : this.colecaoAtividade){
			
			//evita inser��o de objeto de somat�ria
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
