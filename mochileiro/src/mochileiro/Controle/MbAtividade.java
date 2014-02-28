package mochileiro.Controle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;

import mochileiro.Modelo.Atividade;
import mochileiro.Modelo.Categoria;
import mochileiro.Modelo.Usuario;
import mochileiro.Util.Data;
import mochileiro.Util.FormataObjetoLayout;
import mochileiro.Util.InfoMensagem;
import mochileiro.Util.SessaoJSF;

import org.apache.myfaces.trinidad.model.UploadedFile;

/**
 * 
 * @author Thiago
 * @projeto mochileiro
 * @Data 30/10/2009 @Hora 17:22:46
 * @Descri��o: Classe respons�vel por cadastrar atividades das empresas no sistema 
 */
public class MbAtividade{	

	private UploadedFile arquivo;
	private Atividade atividade;
	private List<SelectItem> listaSelect;
	private ArrayList<String> jsArray;
	private Usuario usuario;
	private Categoria categoria;
	private HashMap<Integer,Categoria> catList;

	public MbAtividade(){		

		atividade = new Atividade();

		//defino o tipo de opera��o que ser� realizada
		//cadastro ou atualiza��o
		this.defineAcao();
		
		
	}

	public String persisteAtividade(){

		//componente usado para mensagem de erro e String de navega��o
		String navegacao = "";
		String sMensagem = "";

		try {

			if(validaCampos()){

				//Verifica se foi informada alguma imagem para realiza��o do upload

				if (this.arquivo != null) {
					realizaUpload();
				} else {
					atividade.setImagem(Atividade.IMAGEM_PADRAO);
				}
				
				//caso atividade possua pre�o, formata campo antes
				//da persist�ncia
				if(!atividade.isSemCusto()){					
					
					String precoFormatado = atividade.getPreco();
					
					precoFormatado = FormataObjetoLayout.formataPreco(atividade.getPreco());
					
					atividade.setPreco(precoFormatado);
				}

				//verifica o tipo de a��o e invoco o m�todo apropriado
				if(atividade.getCodigoAtividade() != null){
					atividade.atualiza(atividade);
					sMensagem = "Atualiza��o de atividade efetuada com Sucesso!";

				}else{
					//seta c�digo da empresa na atividade
					atividade.setCodigoEmpresa(usuario.getCodigo());
					atividade.cadastra(atividade);
					sMensagem = "Cadastro de atividade efetuado com Sucesso!";
				}
				navegacao = "EMPRESA";
				//mensagem de resultado positivo
				InfoMensagem.mensInfo(sMensagem);

				//limpo objeto para evitar vest�gios
				atividade = null;
			}

		} catch (Exception e) {

			navegacao = "falhaInterna";
			//limpo objeto para evitar vest�gios
			atividade = null;

			e.printStackTrace();
		}

		return navegacao;
	}

	/**
	 * M�todo respons�vel por validar as informa��es
	 * da atividade antes da persist�ncia
	 * @return
	 * @throws Exception 
	 */
	private boolean validaCampos() throws Exception{

		String sComponenteErro = "cadatividade";
		boolean isValido = true;

		if(this.atividade.getCategoria().getCodigo() == 0){
			isValido = false;
			//lan�a mensagem de erro informando o usu�rio da obrigatoriedade
			//de selecionar uma categoria no combo de atividades
			InfoMensagem.mensInfo(sComponenteErro+":tipo_atividade", "Selecione uma atividade");
		}	

		//verifica se nome da atividade est� duplicado
		if(atividade.selecionarPorNome(atividade.getNomeAtividade())){
			isValido = false;
			InfoMensagem.mensInfo(sComponenteErro+":nome_atv", "Nome j� cadastrado no sistema");
		}
		//verifica n�mero m�ximo de caracteres digitados
		else if(atividade.getDescricao().length() > 1191){
			isValido = false;
			InfoMensagem.mensInfo(sComponenteErro+":descricao", "Digite no m�ximo 1191 caracteres");				
		}

		//caso n�o seja digitado um valor no campo pre�o...
		if(this.atividade.getPreco() == null || this.atividade.getPreco().equals("")){
			//...ou n�o tenha sido selecionada op��o sem custo seta vari�vel de controle 
			//com valor falso
			if(!this.atividade.isSemCusto()){
				isValido = false;
				InfoMensagem.mensInfo(sComponenteErro+":preco", "Digite um valor ou selecione a op��o 'Atividade sem Custo'");
			} else { 
				//se op��o sem custo est� selecionada, limpo campo pre�o
				this.atividade.setPreco("");
			}
		} 

		//resgata datas do per�odo de disponibilidade para manipula��o
		Data dtInicio = this.atividade.getDataInicial();
		Data dtFim	  = this.atividade.getDataFinal();

		//caso datas estejam preenchidas realiza valida��o ignorando categoria
		if (!dtInicio.dataCompleta(false).equals("") || !dtFim.dataCompleta(false).equals("")){

			String mensagemErro = "";

			//verifica se s�o datas v�lidas
			if(!dtInicio.validaDataHora(dtInicio.dataCompleta(false), Data.PADRAO_DATA_BR)){				
				isValido = false;
				mensagemErro = "Data de disponibilidade inicial � inv�lida!";

			} else if(!dtFim.validaDataHora(dtFim.dataCompleta(false), Data.PADRAO_DATA_BR)){
				isValido = false;
				mensagemErro = "Data de disponibilidade final � inv�lida!";
			} 

			//caso n�o existam erros anteriores, realiza compara��o das datas
			if(isValido){

				//limpa campo de mensagem para evitar vest�gios
				mensagemErro = "";

				//invoca m�todo de compara��o de datas e recebe c�digo da opera��o
				int erroComparacao = dtInicio.comparaDatas(dtInicio, dtFim);

				//caso data final seja menor que data inicial
				if(erroComparacao == Data.DT_INICIAL_MAIOR_FINAL){
					mensagemErro = "Data de disponibilidade final n�o deve ser inferior a data inicial";
					isValido = false;
				}

				//caso data final seja menor que data atual
				else if(erroComparacao == Data.DT_FINAL_MENOR_ATUAL){
					mensagemErro = "Data de disponibilidade final n�o deve ser inferior a data atual";
					isValido = false;
				}
			}			

			//lan�a mensagem informando usu�rio
			InfoMensagem.mensInfo(sComponenteErro+":data", mensagemErro);

			//caso datas estejam vazias, verifica se categoria � um evento	
		} else if (dtInicio.dataCompleta(false).equals("") && dtFim.dataCompleta(false).equals("")){

			//extrai categoria da cole��o baseado no c�digo enviado pela view
			Categoria temp = catList.get(this.atividade.getCategoria().getCodigo());

			if(temp != null){

				//caso op��o escolhida seja um evento e como campos data est�o nulos
				//informa ao usu�rio sobre o erro
				if(temp.getTipo().equalsIgnoreCase("evento")){
					InfoMensagem.mensInfo(sComponenteErro+":data", "Per�odo de disponibilidade obrigat�rio para a categoria selecionada");
					isValido = false;
				}
			}
		}

		//campo hora � opcional, por�m caso exista preenchimento, realiza-se a valida��o
		if(!dtInicio.horaCompleta(false).equals("") || !dtFim.horaCompleta(false).equals("")){

			//verifica se horas s�o v�lidas
			if(!dtInicio.validaDataHora(dtInicio.horaCompleta(false), Data.PADRAO_HORA_BR)){
				isValido = false;
				InfoMensagem.mensInfo(sComponenteErro+":horafuncionamento", "Hor�rio inicial � inv�lido!");

			} else if(!dtFim.validaDataHora(dtFim.horaCompleta(false), Data.PADRAO_HORA_BR)){
				isValido = false;
				InfoMensagem.mensInfo(sComponenteErro+":horafuncionamento", "Hor�rio final � inv�lido!");			
			}
		}

		return isValido;
	}


	public void realizaUpload() {
		try {
			//Recupera o contexto servlet
			FacesContext context = FacesContext.getCurrentInstance();

			//Recupera o caminho f�sico da imagem
			ServletContext servletContext = (ServletContext)context.getExternalContext().getContext();

			if (arquivo != null) {
				InputStream stream = arquivo.getInputStream();
				int tamanhoArquivo = (int) arquivo.getLength();
				byte[] buffer 	   =  new byte[(int) tamanhoArquivo];
				String nomeArquivo =	separaNomeArquivo(arquivo.getFilename());

				if (! salvarImagem(buffer, stream, nomeArquivo, tamanhoArquivo, servletContext.getRealPath("/imagens")))
					InfoMensagem.mensInfo("cadatividade:imagem", "Falha no carregamento da Imagem!");
			} 

		}catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private boolean salvarImagem(byte[] buffer, InputStream stream,
			String nomeArquivo, int tamanhoArquivo, String realPath) throws Exception {
		boolean isSalvar        = true;
		FileOutputStream outPut = null;
		File fArquivo           = null;
		try {
			String caminhoImagem = realPath + "\\" + nomeArquivo;

			fArquivo = new File(caminhoImagem);

			outPut   = new FileOutputStream(fArquivo);


			while (isSalvar) {
				int contador = stream.read(buffer, 0, tamanhoArquivo);

				if (contador == -1) 
					break;

				outPut.write(buffer, 0, contador);
			}
			atividade.setImagem(nomeArquivo);

		}catch (Exception ex) {
			isSalvar = false;
			ex.printStackTrace();
		} finally {
			outPut.flush();
			outPut.close();
			stream.close();
		}
		return isSalvar;
	}

	private String separaNomeArquivo(String nomeArquivo) {

		if (nomeArquivo.lastIndexOf("\\") >= -1) {
			nomeArquivo = nomeArquivo.substring(nomeArquivo.lastIndexOf("\\") + 1);
		} else if (nomeArquivo.lastIndexOf("/") >= -1) {
			nomeArquivo = nomeArquivo.substring(nomeArquivo.lastIndexOf("/") + 1);
		}
		return nomeArquivo;
	}

	/**
	 * M�todo respons�vel por definir o tipo de a��o est� sendo executada, 
	 * verifica se objeto inst�nciado cont�m id, significando que se trata
	 * de uma atualiza��o.
	 * 
	 * obs.: atributo resgatado pelo "biding" criado pelo componente
	 * setProperty JSF (verificar view)
	 */
	private void defineAcao(){

		try {
			//Recupera o usuario da sessao
			this.setUsuario((Usuario) SessaoJSF.recuperarItemSessao("usuarioLogado"));

			//recupera o c�digo da atividade na sess�o
			int codigoAtividade = SessaoJSF.getCodigoSessao("id");

			//Caso exista usuario, trata-se de uma atualizacao de dados
			if(usuario != null && codigoAtividade > 0){

				//atualiza informa��es do objeto atividade 
				this.setAtividade(atividade.selecionarPorId(codigoAtividade));

				//invoca m�todo de formata��o dos campos
				FormataObjetoLayout.formataExibicaoAtividade(atividade, FormataObjetoLayout.PERSISTENCIA);

			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	

	/**
	 * M�todo respons�vel por carregar categorias na view
	 * @return
	 */
	private List<SelectItem> carregaCategorias(){

		this.categoria = new Categoria();

		//Array JSF usado para preencher combobox
		listaSelect = new ArrayList<SelectItem>();

		try {
			//recupera informa��es persistidas no banco de dados
			catList = categoria.exibeCategorias();

			//caso exista retorno de dados do BD
			if(catList != null){

				//cria array usado pelo Javascript
				jsArray = new ArrayList<String>();

				listaSelect.add(new SelectItem(0, "Selecione"));				

				//itera sobre array catList...
				for(Categoria cat : catList.values()){					
					//alimentando array selectItem 
					listaSelect.add(new SelectItem(cat.getCodigo(), cat.getNome()));
					//alimenta array usado pelo javascript
					jsArray.add("['"+cat.getCodigo()+"|', '"+cat.getDescricao()+"|' , '"+cat.getTipo()+"|']");
				}
			}
		}catch (Exception ex){	    
			listaSelect.add(new SelectItem(0, "Selecione"));	
			ex.printStackTrace();
		}
		return listaSelect;
	}


	/**
	 * Getter respons�vel por carregar lista de categorias
	 * @return
	 */
	public List<SelectItem> getListaSelect() {

		//carrega lista de categorias
		//alimentando SelectItem
		this.carregaCategorias();

		return listaSelect;
	}


	public ArrayList<String> getJsArray() {
		return jsArray;
	}

	public Atividade getAtividade() {
		return atividade;
	}

	private void setAtividade(Atividade atividade) {
		this.atividade = atividade;
	}

	private void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	public UploadedFile getArquivo() {
		return arquivo;
	}

}

