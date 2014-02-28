package mochileiro.Controle;

import mochileiro.Modelo.GoogleMaps;
import mochileiro.Modelo.Usuario;
import mochileiro.Util.InfoMensagem;
import mochileiro.Util.SessaoJSF;


/**
 * @author $h@rk
 * @projeto mochileiro
 * @Data 18/03/2010 @Hora 19:45:50
 * @Descrição: Classe de gerenciamento do usuário
 */

public class MbUsuario{

	private Usuario usuario;
	private int logado;

	public MbUsuario(){

		//instancia objeto Usuário que será alimentado pela View
		this.usuario = new Usuario();

		//verifica por erros na sessão
		this.verificaMensagemSessao();
	}

	/**
	 * Método de validação do Usuário, retorna string de navegação
	 * @return String
	 */
	public String validarUsuario(){	
		String autenticacao = "autenticacaoFalhou",	
		mensagem = "";

		try{
			//tento realizar login do usuário
			int codigoErro = usuario.autenticar(usuario.getNickName(), usuario.getSenha());

			if(codigoErro == 0){
				autenticacao = usuario.getTipo().toUpperCase();
			} 			
			else if(codigoErro == Usuario.SENHA_INVALIDA){
				mensagem = "Senha inválida";
			}
			else if(codigoErro == Usuario.USUARIO_INVALIDO){
				mensagem = "Usuario não encontrado";
			}

			//...lança mensagem de erro para a view informando o erro (caso exista) 
			InfoMensagem.mensErro(mensagem);

		} catch (Exception ex){
			this.usuario = null;
			autenticacao = "falhaInterna";
			ex.printStackTrace();
		}
		return autenticacao;
	}

	/**
	 * Método responsável por capturar a mensagem de erro caso ocorra uma tentativa de 
	 * acesso à uma área restrita do sistema.
	 */
	public void verificaMensagemSessao(){

		String msgSessao = "";
		try{
		//verifica existência de mensagens na sessao 
		msgSessao = (String)SessaoJSF.recuperarItemSessao("mensagem");

		//Caso tenha sido tentado o acesso nas páginas restritas sem autenticação, 
		//mostra a mensagem avisando o usuário
		if (msgSessao != null){
			InfoMensagem.mensErro(msgSessao);
		}
		
		} catch (Exception e){}
	}


	public String validaSessaoUsuario(){
		String sResultado = "logout";

		try{ 
			//verifica se usuário já está logado no sistema
			//captura o usuário para manipulação
			Usuario userSession = (Usuario) SessaoJSF.recuperarItemSessao("usuarioLogado");

			//define tipo como string de navegação
			//validação necessária para evitar null pointer
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
	 * Método responsável por definir navegação nas páginas
	 * Caso usuário esteja logado, envio o mesmo para o seu painel
	 * caso contrário envio para as páginas de cadastro
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
	 * Método responsável pelo logout do usuário, retorna String
	 * de navegação redirecionando o usuário para a páginal de login
	 * da aplicação
	 */
	public String logout() {

		//Elimina possívels mensagens e finaliza a sessão
		SessaoJSF.finalizaSessao();
		return "logout";
	}
	
	
	/**
	 * Método responsável por determinar qual usuário está logado
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
