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
 * @Descrição: Managed Bean responsável por gerenciar contas das empresas
 *  
 */

public class MbEmpresa {

	//Entidades correspondentes à tela de cadastro que serão usadas para o cadastro
	private Empresa empresa;

	private static final String sMENSAGEM_CNPJ_DUP  = "CNPJ já cadastrado!";
	private static final String sMENSAGEM_LOGIN_DUP = "Login já cadastrado!";	
	private static final String sMENSAGEM_NOME_DUP = "Razao Social já cadastrada!";

	private String infoQtdeAtividades = "Lista Vazia";
	private int    numAtividades;

	private Atividade atividade;
	
	private final String MSG_PARCERIA = " Não existem solicitações pendentes, " +
	   								    " para enviar uma solicitação busque por uma atividade " +
	   								    " e ao visualizá-la, clique na opção 'Solicitar Parceria'";
	
	private final String MSG_SEM_ATIVIDADES_QUALIFICADAS = "Você não possui atividades qualificadas pelos viajantes, não será possível gerar o relatório";
	
	private final String MSG_SEM_INFORMACOES_SIS = "Não existem informações disponíveis, não será possível gerar o relatório";
	
	private String mensagemSolicitacao;

	GeraRelatorio relatorio;
	
	//identifica o relatório selecionado no combo
	private int relatorioSelecionado = 0;
	
	//atributo utilizado para travar edição
	//de campos na view
	private boolean campoSomenteLeitura = false;
	
	
	public MbEmpresa(){
		empresa = new Empresa();
		this.defineAcao();		
	}	
	
	
	/**
	 * alimenta combo com lista de relatórios
	 * @return
	 */
	public ArrayList<SelectItem> getListaRelatorios(){
		
		//cria coleção de tipos de relatório 
		ArrayList<SelectItem> relatorio = new ArrayList<SelectItem>();
		
		relatorio.add(new SelectItem(0, "  Selecione um Relatório  "));				
		relatorio.add(new SelectItem(GeraRelatorio.RELATORIO_RESUMO_EMPRESA, "Qualificações Empresa"));				
		relatorio.add(new SelectItem(GeraRelatorio.RELATORIO_RESUMO_TODAS,   "Qualificações Geral"));
		relatorio.add(new SelectItem(GeraRelatorio.RELATORIO_CATEGORIAS,     "Categorias Cadastradas"));	
		relatorio.add(new SelectItem(GeraRelatorio.RELATORIO_PARCERIAS,      "Empresas com Parceiros"));				
			
		return 	relatorio;	
	}
	
	/**
	 * Método responsável por definir o tipo de relatório que será gerado
	 * verifica atributo relatorioSelecionado setado pelo usuário na  
	 * view para identificar relatório escolhido.
	 *  
	 * Caso algum dos relatórios retorne mensagens de erro, exibe
	 * a mesma ao usuário
	 * 
	 * @return
	 */
	public String defineRelatorio(){
		
		//caso não ocorra erros, não redireciona usuário
		String navegacao = null;		
		String mensagem = null;
		
		try {			
			
			//através de um tratamento no relatório resumo é possível definir
			//se o relatório gerado terá ou não o filtro da empresa, por essa
			//razão o mesmo é invocado nas opções 1 e 2
			switch(relatorioSelecionado){
			
			case 1: //gera relatório de atividades qualificadas (com atividades da empresa)
					mensagem = this.relatorioResumo();
					
				break;
				
			case 2: //gera relatório de atividades qualificadas (utiliza todas as atividades do sistema)
					mensagem = this.relatorioResumo();
					
				break;
				
			case 3: //gera relatório de categorias mais adicionadas
					mensagem = this.relatorioCategorias();
					
				break;	
				
			case 4: //gera relatório de parcerias
				mensagem = this.relatorioParcerias();
				
			break;	
				
				default:
					mensagem = "Selecione um relatório na lista!";	
				
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
	 * Método responsável por iniciar processo de criação de
	 * relatório de categoria
	 * @return
	 * @throws Exception 
	 */
	private String relatorioCategorias() throws Exception{
		
		String mensagem = "";

			//objeto categoria necessário para invocar método de selecao
			Categoria categoria = new Categoria();
			
			//invoca método que extrai informações para relatório
			HashMap<Integer,Categoria> catRel = categoria.selecionaCatRelatorio();
			
			//verifica se existem resultados, em caso negativo, 
			//seta mensagem ao usuário, do contrário gera relatório
			if(catRel.isEmpty()){				
				mensagem = MSG_SEM_INFORMACOES_SIS;
				
			} else {
				relatorio = new GeraRelatorio(GeraRelatorio.RELATORIO_CATEGORIAS, catRel);
				relatorio.criaRelatorio();					
			}
			
			return mensagem;
		}	
	
	
	/**
	 * Gera relatório de resumo das atividades, parâmetro 
	 * enviado pela view define se serão somente as atividades da empresa
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
		//que está realizando a operação
		if(relatorioSelecionado == GeraRelatorio.RELATORIO_RESUMO_EMPRESA){
			codEmpresa  = this.empresa.getUsuario().getCodigo();
			nomeEmpresa = this.empresa.getNomeEmpresa();			
		}
		
		//seleciona atividades que compõe o relatório
		this.empresa.selecionaRelatorioResumo(codEmpresa);
		
		//caso empresa não possua atividades e a busca tenha filtro pela mesma
		//seta mensagem informando sobre a impossibilidade de gerar relatórios
		if(this.empresa.getListaAtividade().isEmpty() && (relatorioSelecionado == GeraRelatorio.RELATORIO_RESUMO_EMPRESA)){
			mensagem = MSG_SEM_ATIVIDADES_QUALIFICADAS;
		
		//caso empresa e o sistema não possuam atividades cadastradas
		//seta mensagem informando sobre a impossibilidade de gerar relatórios
		} else if(this.empresa.getListaAtividade().isEmpty() && (relatorioSelecionado == GeraRelatorio.RELATORIO_RESUMO_TODAS)){
			mensagem = MSG_SEM_INFORMACOES_SIS;
			
		} else {
			//caso exista retorno, cria relatório
			relatorio = new GeraRelatorio(nomeEmpresa, GeraRelatorio.RELATORIO_RESUMO_EMPRESA, this.empresa.getListaAtividade());			
			relatorio.criaRelatorio();
		}		
		
		return mensagem;
	}
	
	

	/**
	 * Método que realiza a operação de inserção de nova empresa 
	 * @return String de Navegação utilizada no arquivo faces-config.xml
	 */
	public String persisteEmpresa(){

		//Inicializa a variável de navegação
		String navegacao = null;

		try{

			//caso não existam erros, determina o método apropriado e realiza persistência
			if (!validaCampos())
			{

				if(empresa.getUsuario().getCodigo() != null){ 
					//invoca método de atualização
					empresa.atualizar(empresa);
					navegacao = "EMPRESA";
					InfoMensagem.mensInfo("Atualização de Cadastro Efetuada com Sucesso!");
				}  else {
					//invoca método de cadastro para registrar empresa no sistema
					empresa.inserir(empresa);
					navegacao = "cadOk";
				}

			}

		}catch (Exception ex){
			navegacao = "falhaInterna";
			ex.printStackTrace();
		}
		//Retorna a página de navegação usada no arquivo faces-config.xml
		return navegacao;
	}



	private boolean validaCampos() throws Exception{

		boolean isDuplicado = false;	

		//efetua validação de nome da Empresa
		if(empresa.validarRazaoSocialDuplicada(empresa.getNomeEmpresa()) != null){
			isDuplicado = true;

			//Retorna mensagem de erro na página de cadastro caso o nome esteja duplicado
			InfoMensagem.mensInfo("cad_empresas:razao_social", sMENSAGEM_NOME_DUP);
		}
		
		//Efetua a validação do CNPJ
		if (empresa.validarCNPJDuplicado(empresa.getCnpj())!= null){
			isDuplicado = true;

			//Retorna mensagem de erro na página de cadastro caso o CNPJ esteja duplicado
			InfoMensagem.mensInfo("cad_empresas:cnpj", sMENSAGEM_CNPJ_DUP);
		}
		
		//Efetua a validação de login
		if (empresa.getUsuario().validarNickNameDuplicado(empresa.getUsuario().getCodigo(),
														  empresa.getUsuario().getNickName())){
			isDuplicado = true;

			//Retorna mensagem de erro na página de cadastro caso o Login esteja duplicado
			InfoMensagem.mensInfo("cad_empresas:usuario", sMENSAGEM_LOGIN_DUP);
		}

		return isDuplicado;
	}	



	/**
	 * Método responsável por definir o tipo de ação está sendo executada, 
	 * verifica se objeto instanciado contem id, significando que se trata
	 * de uma atualização.
	 */
	private void defineAcao(){

		try {
			
			//limpa código na sessão para evitar vestígios
			SessaoJSF.setCodigoSessao(0); 

			//Recupera o usuario da sessao
			Usuario usuario = (Usuario) SessaoJSF.recuperarItemSessao("usuarioLogado");

			//Caso exista usuario, trata-se de uma atualizacao de dados da empresa
			if(usuario != null){

				//recupero informações do objeto baseado no id encontrado na sessão
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
	 * Ajusta informações da empresa para exibição
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
			
			//verifica a existência de solicitações
			this.verificaSolicitações();
			
			campoSomenteLeitura = true;

			//ajusto informações para exibição correta
			FormataObjetoLayout.formataContaEmpresa(empresa);			

		}catch (Exception e) {
			infoQtdeAtividades = "Erro ao acessar a lista";
			e.printStackTrace();
		}
	}
	
	/**
	 * Método responsável por verificar existência de solicitações
	 * para as atividades da empresa
	 * 
	 * @throws Exception
	 */
	private void verificaSolicitações() throws Exception{
		
		try{
		this.atividade = new Atividade();
		
		String mensagem = "Você recebeu uma nova solicitação, verifique o gerenciamento de parcerias";
		
		//busca por solicitações pendentes
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
