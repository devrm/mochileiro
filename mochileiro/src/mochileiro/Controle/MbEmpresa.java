package mochileiro.Controle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.model.SelectItem;

import mochileiro.Modelo.Atividade;
import mochileiro.Modelo.Categoria;
import mochileiro.Modelo.Empresa;
import mochileiro.Modelo.GeraRelatorio;
import mochileiro.Modelo.Usuario;
import mochileiro.Util.FormataObjetoLayout;
import mochileiro.Util.InfoMensagem;
import mochileiro.Util.SessaoJSF;

/**
 * 
 * @author Rodrigo
 * @projeto mochileiro
 * @Data 01/09/2009 @Hora 22:20:18
 * @Descri��o: Managed Bean respons�vel por gerenciar contas das empresas
 *  
 */

public class MbEmpresa {

	//Entidades correspondentes � tela de cadastro que ser�o usadas para o cadastro
	private Empresa empresa;

	private static final String sMENSAGEM_CNPJ_DUP  = "CNPJ j� cadastrado!";
	private static final String sMENSAGEM_LOGIN_DUP = "Login j� cadastrado!";	
	private static final String sMENSAGEM_NOME_DUP = "Razao Social j� cadastrada!";

	private String infoQtdeAtividades = "Lista Vazia";
	private int    numAtividades;

	private Atividade atividade;
	
	private final String MSG_PARCERIA = " N�o existem solicita��es pendentes, " +
	   								    " para enviar uma solicita��o busque por uma atividade " +
	   								    " e ao visualiz�-la, clique na op��o 'Solicitar Parceria'";
	
	private final String MSG_SEM_ATIVIDADES_QUALIFICADAS = "Voc� n�o possui atividades qualificadas pelos viajantes, n�o ser� poss�vel gerar o relat�rio";
	
	private final String MSG_SEM_INFORMACOES_SIS = "N�o existem informa��es dispon�veis, n�o ser� poss�vel gerar o relat�rio";
	
	private String mensagemSolicitacao;

	GeraRelatorio relatorio;
	
	//identifica o relat�rio selecionado no combo
	private int relatorioSelecionado = 0;
	
	//atributo utilizado para travar edi��o
	//de campos na view
	private boolean campoSomenteLeitura = false;
	
	
	public MbEmpresa(){
		empresa = new Empresa();
		this.defineAcao();		
	}	
	
	
	/**
	 * alimenta combo com lista de relat�rios
	 * @return
	 */
	public ArrayList<SelectItem> getListaRelatorios(){
		
		//cria cole��o de tipos de relat�rio 
		ArrayList<SelectItem> relatorio = new ArrayList<SelectItem>();
		
		relatorio.add(new SelectItem(0, "  Selecione um Relat�rio  "));				
		relatorio.add(new SelectItem(GeraRelatorio.RELATORIO_RESUMO_EMPRESA, "Qualifica��es Empresa"));				
		relatorio.add(new SelectItem(GeraRelatorio.RELATORIO_RESUMO_TODAS,   "Qualifica��es Geral"));
		relatorio.add(new SelectItem(GeraRelatorio.RELATORIO_CATEGORIAS,     "Categorias Cadastradas"));	
		relatorio.add(new SelectItem(GeraRelatorio.RELATORIO_PARCERIAS,      "Empresas com Parceiros"));				
			
		return 	relatorio;	
	}
	
	/**
	 * M�todo respons�vel por definir o tipo de relat�rio que ser� gerado
	 * verifica atributo relatorioSelecionado setado pelo usu�rio na  
	 * view para identificar relat�rio escolhido.
	 *  
	 * Caso algum dos relat�rios retorne mensagens de erro, exibe
	 * a mesma ao usu�rio
	 * 
	 * @return
	 */
	public String defineRelatorio(){
		
		//caso n�o ocorra erros, n�o redireciona usu�rio
		String navegacao = null;		
		String mensagem = null;
		
		try {			
			
			//atrav�s de um tratamento no relat�rio resumo � poss�vel definir
			//se o relat�rio gerado ter� ou n�o o filtro da empresa, por essa
			//raz�o o mesmo � invocado nas op��es 1 e 2
			switch(relatorioSelecionado){
			
			case 1: //gera relat�rio de atividades qualificadas (com atividades da empresa)
					mensagem = this.relatorioResumo();
					
				break;
				
			case 2: //gera relat�rio de atividades qualificadas (utiliza todas as atividades do sistema)
					mensagem = this.relatorioResumo();
					
				break;
				
			case 3: //gera relat�rio de categorias mais adicionadas
					mensagem = this.relatorioCategorias();
					
				break;	
				
			case 4: //gera relat�rio de parcerias
				mensagem = this.relatorioParcerias();
				
			break;	
				
				default:
					mensagem = "Selecione um relat�rio na lista!";	
				
			}	
			
		//seta mensagem de erro (caso exista)
		InfoMensagem.mensErro(mensagem);		
	
			
		} catch (Exception ex){
			navegacao = "falhaInterna";
			ex.printStackTrace();
		}		

		return navegacao;
	}	
	
	
	private String relatorioParcerias() throws Exception{
		
		String mensagem = "";
		
		//resgata empresas com parcerias cadastradas
		List<Empresa> listaEmpresa = this.empresa.selecionaRelatorioParceria();	
		
		if(listaEmpresa.isEmpty()){
			mensagem = MSG_SEM_INFORMACOES_SIS;
		} else {
			relatorio = new GeraRelatorio(GeraRelatorio.RELATORIO_PARCERIAS, listaEmpresa);
			relatorio.criaRelatorio();
		}		
		
		return mensagem;		
	}
	
	
	/**
	 * M�todo respons�vel por iniciar processo de cria��o de
	 * relat�rio de categoria
	 * @return
	 * @throws Exception 
	 */
	private String relatorioCategorias() throws Exception{
		
		String mensagem = "";

			//objeto categoria necess�rio para invocar m�todo de selecao
			Categoria categoria = new Categoria();
			
			//invoca m�todo que extrai informa��es para relat�rio
			HashMap<Integer,Categoria> catRel = categoria.selecionaCatRelatorio();
			
			//verifica se existem resultados, em caso negativo, 
			//seta mensagem ao usu�rio, do contr�rio gera relat�rio
			if(catRel.isEmpty()){				
				mensagem = MSG_SEM_INFORMACOES_SIS;
				
			} else {
				relatorio = new GeraRelatorio(GeraRelatorio.RELATORIO_CATEGORIAS, catRel);
				relatorio.criaRelatorio();					
			}
			
			return mensagem;
		}	
	
	
	/**
	 * Gera relat�rio de resumo das atividades, par�metro 
	 * enviado pela view define se ser�o somente as atividades da empresa
	 * ou todas as atividades cadastradas no sistema
	 * 
	 * @return
	 * @throws Exception 
	 */
	private String relatorioResumo() throws Exception{	
		
		Integer codEmpresa  = null;
		String  nomeEmpresa = null;
		String  mensagem    = null;
		
		//caso seja selecionado o tipo de relatorio de empresas, significa
		//que devem ser listadas somente as atividades da empresa
		//que est� realizando a opera��o
		if(relatorioSelecionado == GeraRelatorio.RELATORIO_RESUMO_EMPRESA){
			codEmpresa  = this.empresa.getUsuario().getCodigo();
			nomeEmpresa = this.empresa.getNomeEmpresa();			
		}
		
		//seleciona atividades que comp�e o relat�rio
		this.empresa.selecionaRelatorioResumo(codEmpresa);
		
		//caso empresa n�o possua atividades e a busca tenha filtro pela mesma
		//seta mensagem informando sobre a impossibilidade de gerar relat�rios
		if(this.empresa.getListaAtividade().isEmpty() && (relatorioSelecionado == GeraRelatorio.RELATORIO_RESUMO_EMPRESA)){
			mensagem = MSG_SEM_ATIVIDADES_QUALIFICADAS;
		
		//caso empresa e o sistema n�o possuam atividades cadastradas
		//seta mensagem informando sobre a impossibilidade de gerar relat�rios
		} else if(this.empresa.getListaAtividade().isEmpty() && (relatorioSelecionado == GeraRelatorio.RELATORIO_RESUMO_TODAS)){
			mensagem = MSG_SEM_INFORMACOES_SIS;
			
		} else {
			//caso exista retorno, cria relat�rio
			relatorio = new GeraRelatorio(nomeEmpresa, GeraRelatorio.RELATORIO_RESUMO_EMPRESA, this.empresa.getListaAtividade());			
			relatorio.criaRelatorio();
		}		
		
		return mensagem;
	}
	
	

	/**
	 * M�todo que realiza a opera��o de inser��o de nova empresa 
	 * @return String de Navega��o utilizada no arquivo faces-config.xml
	 */
	public String persisteEmpresa(){

		//Inicializa a vari�vel de navega��o
		String navegacao = null;

		try{

			//caso n�o existam erros, determina o m�todo apropriado e realiza persist�ncia
			if (!validaCampos())
			{

				if(empresa.getUsuario().getCodigo() != null){ 
					//invoca m�todo de atualiza��o
					empresa.atualizar(empresa);
					navegacao = "EMPRESA";
					InfoMensagem.mensInfo("Atualiza��o de Cadastro Efetuada com Sucesso!");
				}  else {
					//invoca m�todo de cadastro para registrar empresa no sistema
					empresa.inserir(empresa);
					navegacao = "cadOk";
				}

			}

		}catch (Exception ex){
			navegacao = "falhaInterna";
			ex.printStackTrace();
		}
		//Retorna a p�gina de navega��o usada no arquivo faces-config.xml
		return navegacao;
	}



	private boolean validaCampos() throws Exception{

		boolean isDuplicado = false;	

		//efetua valida��o de nome da Empresa
		if(empresa.validarRazaoSocialDuplicada(empresa.getNomeEmpresa()) != null){
			isDuplicado = true;

			//Retorna mensagem de erro na p�gina de cadastro caso o nome esteja duplicado
			InfoMensagem.mensInfo("cad_empresas:razao_social", sMENSAGEM_NOME_DUP);
		}
		
		//Efetua a valida��o do CNPJ
		if (empresa.validarCNPJDuplicado(empresa.getCnpj())!= null){
			isDuplicado = true;

			//Retorna mensagem de erro na p�gina de cadastro caso o CNPJ esteja duplicado
			InfoMensagem.mensInfo("cad_empresas:cnpj", sMENSAGEM_CNPJ_DUP);
		}
		
		//Efetua a valida��o de login
		if (empresa.getUsuario().validarNickNameDuplicado(empresa.getUsuario().getCodigo(),
														  empresa.getUsuario().getNickName())){
			isDuplicado = true;

			//Retorna mensagem de erro na p�gina de cadastro caso o Login esteja duplicado
			InfoMensagem.mensInfo("cad_empresas:usuario", sMENSAGEM_LOGIN_DUP);
		}

		return isDuplicado;
	}	



	/**
	 * M�todo respons�vel por definir o tipo de a��o est� sendo executada, 
	 * verifica se objeto instanciado contem id, significando que se trata
	 * de uma atualiza��o.
	 */
	private void defineAcao(){

		try {
			
			//limpa c�digo na sess�o para evitar vest�gios
			SessaoJSF.setCodigoSessao(0); 

			//Recupera o usuario da sessao
			Usuario usuario = (Usuario) SessaoJSF.recuperarItemSessao("usuarioLogado");

			//Caso exista usuario, trata-se de uma atualizacao de dados da empresa
			if(usuario != null){

				//recupero informa��es do objeto baseado no id encontrado na sess�o
				this.setEmpresa(this.empresa.selecionarPorId(usuario.getCodigo()));
				
				//Ajusta a exibicao 
				this.ajustaInfoConta();

			} else {
				//Seta as informacoes necessarias indicando um novo cadastro de empresa
				this.empresa.setUsuario(new Usuario());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	/**
	 * Ajusta informa��es da empresa para exibi��o
	 */
	private void ajustaInfoConta(){

		try {

			//Seleciona atividades cadastradas para a empresa corrente
			empresa.selecionarAtividades(empresa.getUsuario().getCodigo());

			numAtividades = empresa.getListaAtividade().size();

			//Se o numero de atividades for maior do que zero, exibe a quantidade
			if(numAtividades > 0){
				infoQtdeAtividades = String.valueOf(numAtividades);					
			}
			
			//verifica a exist�ncia de solicita��es
			this.verificaSolicita��es();
			
			campoSomenteLeitura = true;

			//ajusto informa��es para exibi��o correta
			FormataObjetoLayout.formataContaEmpresa(empresa);			

		}catch (Exception e) {
			infoQtdeAtividades = "Erro ao acessar a lista";
			e.printStackTrace();
		}
	}
	
	/**
	 * M�todo respons�vel por verificar exist�ncia de solicita��es
	 * para as atividades da empresa
	 * 
	 * @throws Exception
	 */
	private void verificaSolicita��es() throws Exception{
		
		try{
		this.atividade = new Atividade();
		
		String mensagem = "Voc� recebeu uma nova solicita��o, verifique o gerenciamento de parcerias";
		
		//busca por solicita��es pendentes
		this.atividade.selecionaSolicitacoes(null, empresa.getUsuario().getCodigo());		
		
		//verifica se busca retornou resultados
		if(this.atividade.getSolicitacoes().isEmpty())				
			this.mensagemSolicitacao = MSG_PARCERIA;
		else
			this.mensagemSolicitacao = mensagem;
	
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//getters e setters

	public String getQtdAtividades() {
		return infoQtdeAtividades;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	private void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public String getMensagemSolicitacao() {
		return mensagemSolicitacao;
	}


	public void setRelatorioSelecionado(int relatorioSelecionado) {
		this.relatorioSelecionado = relatorioSelecionado;
	}


	public int getRelatorioSelecionado() {
		return relatorioSelecionado;
	}

	public boolean iscampoSomenteLeitura() {
		return campoSomenteLeitura;
	}
}
