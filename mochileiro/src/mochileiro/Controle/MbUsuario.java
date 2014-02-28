package mochileiro.Controle;

import mochileiro.Modelo.GoogleMaps;
import mochileiro.Modelo.Usuario;
import mochileiro.Util.InfoMensagem;
import mochileiro.Util.SessaoJSF;


/**
 * @author $h@rk
 * @projeto mochileiro
 * @Data 18/03/2010 @Hora 19:45:50
 * @Descri��o: Classe de gerenciamento do usu�rio
 */

public class MbUsuario{

	private Usuario usuario;
	private int logado;

	public MbUsuario(){

		//instancia objeto Usu�rio que ser� alimentado pela View
		this.usuario = new Usuario();

		//verifica por erros na sess�o
		this.verificaMensagemSessao();
	}

	/**
	 * M�todo de valida��o do Usu�rio, retorna string de navega��o
	 * @return String
	 */
	public String validarUsuario(){	
		String autenticacao = "autenticacaoFalhou",	
		mensagem = "";

		try{
			//tento realizar login do usu�rio
			int codigoErro = usuario.autenticar(usuario.getNickName(), usuario.getSenha());

			if(codigoErro == 0){
				autenticacao = usuario.getTipo().toUpperCase();
			} 			
			else if(codigoErro == Usuario.SENHA_INVALIDA){
				mensagem = "Senha inv�lida";
			}
			else if(codigoErro == Usuario.USUARIO_INVALIDO){
				mensagem = "Usuario n�o encontrado";
			}

			//...lan�a mensagem de erro para a view informando o erro (caso exista) 
			InfoMensagem.mensErro(mensagem);

		} catch (Exception ex){
			this.usuario = null;
			autenticacao = "falhaInterna";
			ex.printStackTrace();
		}
		return autenticacao;
	}

	/**
	 * M�todo respons�vel por capturar a mensagem de erro caso ocorra uma tentativa de 
	 * acesso � uma �rea restrita do sistema.
	 */
	public void verificaMensagemSessao(){

		String msgSessao = "";
		try{
		//verifica exist�ncia de mensagens na sessao 
		msgSessao = (String)SessaoJSF.recuperarItemSessao("mensagem");

		//Caso tenha sido tentado o acesso nas p�ginas restritas sem autentica��o, 
		//mostra a mensagem avisando o usu�rio
		if (msgSessao != null){
			InfoMensagem.mensErro(msgSessao);
		}
		
		} catch (Exception e){}
	}


	public String validaSessaoUsuario(){
		String sResultado = "logout";

		try{ 
			//verifica se usu�rio j� est� logado no sistema
			//captura o usu�rio para manipula��o
			Usuario userSession = (Usuario) SessaoJSF.recuperarItemSessao("usuarioLogado");

			//define tipo como string de navega��o
			//valida��o necess�ria para evitar null pointer
			if(userSession != null){
				sResultado = userSession.getTipo().toUpperCase();				
				
				//caso se trate de um viajante, verifica menagedBean
				if(sResultado.equalsIgnoreCase("VIAJANTE")){							
					SessaoJSF.removeItemSessao(GoogleMaps.ROTA_SELECIONADA);
				}
			}

		} catch (Exception e) {

			e.printStackTrace();
			sResultado = "falhaInterna";
		}
		return sResultado;
	}

	/**
	 * M�todo respons�vel por definir navega��o nas p�ginas
	 * Caso usu�rio esteja logado, envio o mesmo para o seu painel
	 * caso contr�rio envio para as p�ginas de cadastro
	 * @return
	 */	
	public String getRotaViajante(){

		String rota = this.validaSessaoUsuario(); 

		if(rota.equalsIgnoreCase("logout")){
			rota = "CADVIAJANTE";		
		
		} 
				
		return rota;
	}


	public String getRotaEmpresa(){

		String rota = this.validaSessaoUsuario(); 

		if(rota.equalsIgnoreCase("logout"))
			rota = "CADEMPRESA";

		return rota;
	}

	/**
	 * M�todo respons�vel pelo logout do usu�rio, retorna String
	 * de navega��o redirecionando o usu�rio para a p�ginal de login
	 * da aplica��o
	 */
	public String logout() {

		//Elimina poss�vels mensagens e finaliza a sess�o
		SessaoJSF.finalizaSessao();
		return "logout";
	}
	
	
	/**
	 * M�todo respons�vel por determinar qual usu�rio est� logado
	 * @return
	 */
	public int getLogado(){

		if(this.validaSessaoUsuario().equalsIgnoreCase("EMPRESA")){
			this.logado  = 1;
		} else if(this.validaSessaoUsuario().equalsIgnoreCase("VIAJANTE")){
			this.logado = 2;
		} else {
			this.logado = 0;
		}

		return logado;
	}

	

	public Usuario getUsuario() {
		return usuario;
	}
}
