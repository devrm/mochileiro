package mochileiro.Controle;

import mochileiro.Modelo.Usuario;
import mochileiro.Modelo.Viajante;
import mochileiro.Util.FormataObjetoLayout;
import mochileiro.Util.InfoMensagem;
import mochileiro.Util.SessaoJSF;

/**
 * 
 * @author David
 * @projeto mochileiro
 * @Data 01/09/2009
 * @Hora 22:20:18
 * @Descri��o: Managed Bean respons�vel por gerenciar persist�ncia dos viajantes
 * 
 */

public class MbViajante {

	private Viajante viajante;
	
	private static final String MENSAGEM_CPF_DUP 		= "CPF j� cadastrado!";
	private static final String MENSAGEN_LOGIN_DUP 		= "Login j� cadastrado!";
	
	//atributo utilizado para travar edi��o
	//de campos na view
	private boolean campoSomenteLeitura = false;

	
	public MbViajante() {

		this.viajante = new Viajante();	

		//Define a acao tomada caso ja exista um usuario na sessao
		this.defineAcao();
	}


	/**
	 * Cadastra/atualiza viajante no sistema
	 * @return
	 */
	public String persisteViajante() {

		String resultado = "";

		boolean isInformacaoDuplicada = false;

		try {
			//Efetua a valida��o de login
			if (viajante.getUsuario().validarNickNameDuplicado(viajante.getUsuario().getCodigo(),
					viajante.getUsuario().getNickName())){
				isInformacaoDuplicada = true;

				//Retorna mensagem de erro na p�gina de cadastro caso o Login
				//esteja duplicado
				InfoMensagem.mensInfo("cad_viajantes:usuario", MENSAGEN_LOGIN_DUP);
			}

			//Efetua a valida��o do CPF
			if (viajante.selecionarPorCpf(viajante.getCpf())) {
				isInformacaoDuplicada = true;

				// Retorna mensagem de erro na p�gina de cadastro caso o CPF
				// esteja duplicado
				InfoMensagem.mensInfo("cad_viajantes:cpf", MENSAGEM_CPF_DUP);
			}

			//caso n�o existam erros, realiza persist�ncia
			if (!isInformacaoDuplicada) {

				//caso n�o possua c�digo, ent�o trata-se de um cadastro
				if(viajante.getUsuario().getCodigo() == null){

					viajante.inserir(this.viajante);
					resultado = "cadOk";

					//Finaliza o objeto para evitar vestigios na sessao
					this.viajante = null;

				} else {
					//Atualiza o viajante e exibe mensagem de alerta no painel
					viajante.atualizar(this.viajante);
				
					InfoMensagem.mensInfo("alertaPainel","Atualiza��o de Cadastro Efetuada com Sucesso!");
					resultado = "VIAJANTE";
				}
			}

		} catch (Exception ex) {
			resultado = "falhaInterna";
			ex.printStackTrace();
		}
		return resultado;
	}

	/**
	 * M�todo respons�vel por definir o tipo de a��o est� sendo executada, 
	 * verifica se objeto instanciado contem id, significando que se trata
	 * de uma atualiza��o.
	 */
	private String defineAcao(){
		String navegacao = "";

		try {

			//Recupera o usuario da sessao
			Usuario usuario = (Usuario) SessaoJSF.recuperarItemSessao("usuarioLogado");

			//Caso exista usuario, trata-se de uma atualizacao de dados do viajante
			if(usuario != null){

				//recupera informa��es do objeto baseado no id encontrado na sess�o
				this.setViajante(this.viajante.selecionarPorId(usuario.getCodigo()));
				
				//trava campo somente leitura
				this.campoSomenteLeitura = true;
				
				//ajusta informa��es para exibi��o no formul�rio (caso se trate de uma atualiza��o)
				FormataObjetoLayout.formataContaViajante(viajante);

			} else {
				//Seta as informacoes necessarias indicando um novo cadastro de viajante
				this.viajante.setUsuario(new Usuario());
			}

		} catch (Exception ex) {
			navegacao = "falhaInterna";
			ex.printStackTrace();
		}
		return navegacao;
	}


	//getters e setters
	private void setViajante(Viajante viajante) {
		this.viajante = viajante;
	}

	public Viajante getViajante() {
		return viajante;
	}


	public boolean isCampoSomenteLeitura() {
		return campoSomenteLeitura;
	}
}