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
 * @Descrição: Classe responsável por cadastrar atividades das empresas no sistema 
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

		//defino o tipo de operação que será realizada
		//cadastro ou atualização
		this.defineAcao();
		
		
	}

	public String persisteAtividade(){

		//componente usado para mensagem de erro e String de navegação
		String navegacao = "";
		String sMensagem = "";

		try {

			if(validaCampos()){

				//Verifica se foi informada alguma imagem para realização do upload

				if (this.arquivo != null) {
					realizaUpload();
				} else {
					atividade.setImagem(Atividade.IMAGEM_PADRAO);
				}
				
				//caso atividade possua preço, formata campo antes
				//da persistência
				if(!atividade.isSemCusto()){					
					
					String precoFormatado = atividade.getPreco();
					
					precoFormatado = FormataObjetoLayout.formataPreco(atividade.getPreco());
					
					atividade.setPreco(precoFormatado);
				}

				//verifica o tipo de ação e invoco o método apropriado
				if(atividade.getCodigoAtividade() != null){
					atividade.atualiza(atividade);
					sMensagem = "Atualização de atividade efetuada com Sucesso!";

				}else{
					//seta código da empresa na atividade
					atividade.setCodigoEmpresa(usuario.getCodigo());
					atividade.cadastra(atividade);
					sMensagem = "Cadastro de atividade efetuado com Sucesso!";
				}
				navegacao = "EMPRESA";
				//mensagem de resultado positivo
				InfoMensagem.mensInfo(sMensagem);

				//limpo objeto para evitar vestígios
				atividade = null;
			}

		} catch (Exception e) {

			navegacao = "falhaInterna";
			//limpo objeto para evitar vestígios
			atividade = null;

			e.printStackTrace();
		}

		return navegacao;
	}

	/**
	 * Método responsável por validar as informações
	 * da atividade antes da persistência
	 * @return
	 * @throws Exception 
	 */
	private boolean validaCampos() throws Exception{

		String sComponenteErro = "cadatividade";
		boolean isValido = true;

		if(this.atividade.getCategoria().getCodigo() == 0){
			isValido = false;
			//lança mensagem de erro informando o usuário da obrigatoriedade
			//de selecionar uma categoria no combo de atividades
			InfoMensagem.mensInfo(sComponenteErro+":tipo_atividade", "Selecione uma atividade");
		}	

		//verifica se nome da atividade está duplicado
		if(atividade.selecionarPorNome(atividade.getNomeAtividade())){
			isValido = false;
			InfoMensagem.mensInfo(sComponenteErro+":nome_atv", "Nome já cadastrado no sistema");
		}
		//verifica número máximo de caracteres digitados
		else if(atividade.getDescricao().length() > 1191){
			isValido = false;
			InfoMensagem.mensInfo(sComponenteErro+":descricao", "Digite no máximo 1191 caracteres");				
		}

		//caso não seja digitado um valor no campo preço...
		if(this.atividade.getPreco() == null || this.atividade.getPreco().equals("")){
			//...ou não tenha sido selecionada opção sem custo seta variável de controle 
			//com valor falso
			if(!this.atividade.isSemCusto()){
				isValido = false;
				InfoMensagem.mensInfo(sComponenteErro+":preco", "Digite um valor ou selecione a opção 'Atividade sem Custo'");
			} else { 
				//se opção sem custo está selecionada, limpo campo preço
				this.atividade.setPreco("");
			}
		} 

		//resgata datas do período de disponibilidade para manipulação
		Data dtInicio = this.atividade.getDataInicial();
		Data dtFim	  = this.atividade.getDataFinal();

		//caso datas estejam preenchidas realiza validação ignorando categoria
		if (!dtInicio.dataCompleta(false).equals("") || !dtFim.dataCompleta(false).equals("")){

			String mensagemErro = "";

			//verifica se são datas válidas
			if(!dtInicio.validaDataHora(dtInicio.dataCompleta(false), Data.PADRAO_DATA_BR)){				
				isValido = false;
				mensagemErro = "Data de disponibilidade inicial é inválida!";

			} else if(!dtFim.validaDataHora(dtFim.dataCompleta(false), Data.PADRAO_DATA_BR)){
				isValido = false;
				mensagemErro = "Data de disponibilidade final é inválida!";
			} 

			//caso não existam erros anteriores, realiza comparação das datas
			if(isValido){

				//limpa campo de mensagem para evitar vestígios
				mensagemErro = "";

				//invoca método de comparação de datas e recebe código da operação
				int erroComparacao = dtInicio.comparaDatas(dtInicio, dtFim);

				//caso data final seja menor que data inicial
				if(erroComparacao == Data.DT_INICIAL_MAIOR_FINAL){
					mensagemErro = "Data de disponibilidade final não deve ser inferior a data inicial";
					isValido = false;
				}

				//caso data final seja menor que data atual
				else if(erroComparacao == Data.DT_FINAL_MENOR_ATUAL){
					mensagemErro = "Data de disponibilidade final não deve ser inferior a data atual";
					isValido = false;
				}
			}			

			//lança mensagem informando usuário
			InfoMensagem.mensInfo(sComponenteErro+":data", mensagemErro);

			//caso datas estejam vazias, verifica se categoria é um evento	
		} else if (dtInicio.dataCompleta(false).equals("") && dtFim.dataCompleta(false).equals("")){

			//extrai categoria da coleção baseado no código enviado pela view
			Categoria temp = catList.get(this.atividade.getCategoria().getCodigo());

			if(temp != null){

				//caso opção escolhida seja um evento e como campos data estão nulos
				//informa ao usuário sobre o erro
				if(temp.getTipo().equalsIgnoreCase("evento")){
					InfoMensagem.mensInfo(sComponenteErro+":data", "Período de disponibilidade obrigatório para a categoria selecionada");
					isValido = false;
				}
			}
		}

		//campo hora é opcional, porém caso exista preenchimento, realiza-se a validação
		if(!dtInicio.horaCompleta(false).equals("") || !dtFim.horaCompleta(false).equals("")){

			//verifica se horas são válidas
			if(!dtInicio.validaDataHora(dtInicio.horaCompleta(false), Data.PADRAO_HORA_BR)){
				isValido = false;
				InfoMensagem.mensInfo(sComponenteErro+":horafuncionamento", "Horário inicial é inválido!");

			} else if(!dtFim.validaDataHora(dtFim.horaCompleta(false), Data.PADRAO_HORA_BR)){
				isValido = false;
				InfoMensagem.mensInfo(sComponenteErro+":horafuncionamento", "Horário final é inválido!");			
			}
		}

		return isValido;
	}


	public void realizaUpload() {
		try {
			//Recupera o contexto servlet
			FacesContext context = FacesContext.getCurrentInstance();

			//Recupera o caminho físico da imagem
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
	 * Método responsável por definir o tipo de ação está sendo executada, 
	 * verifica se objeto instânciado contêm id, significando que se trata
	 * de uma atualização.
	 * 
	 * obs.: atributo resgatado pelo "biding" criado pelo componente
	 * setProperty JSF (verificar view)
	 */
	private void defineAcao(){

		try {
			//Recupera o usuario da sessao
			this.setUsuario((Usuario) SessaoJSF.recuperarItemSessao("usuarioLogado"));

			//recupera o código da atividade na sessão
			int codigoAtividade = SessaoJSF.getCodigoSessao("id");

			//Caso exista usuario, trata-se de uma atualizacao de dados
			if(usuario != null && codigoAtividade > 0){

				//atualiza informações do objeto atividade 
				this.setAtividade(atividade.selecionarPorId(codigoAtividade));

				//invoca método de formatação dos campos
				FormataObjetoLayout.formataExibicaoAtividade(atividade, FormataObjetoLayout.PERSISTENCIA);

			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	

	/**
	 * Método responsável por carregar categorias na view
	 * @return
	 */
	private List<SelectItem> carregaCategorias(){

		this.categoria = new Categoria();

		//Array JSF usado para preencher combobox
		listaSelect = new ArrayList<SelectItem>();

		try {
			//recupera informações persistidas no banco de dados
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
	 * Getter responsável por carregar lista de categorias
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

