package mochileiro.Util;


import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author $h@rk
 * @projeto mochileiro
 * @Data 17/03/2010 @Hora 23:34:56
 * @Descri��o: Classe utilit�ria respons�vel por interagir com as sess�es
 * 			   JSF e intermediar as requisi��es de sess�o
 */

public class SessaoJSF {
	
	
	private static int codigoSessao = 0;
	

	/**
	 * m�todo utilit�rio para resgatar sess�o jsf
	 * 
	 * @return
	 */
	public static HttpSession recuperaSessao(){

		//Recupera o contexto jsf
		FacesContext fc = FacesContext.getCurrentInstance();
		//Recupera a sess�o atual
		return (HttpSession) fc.getExternalContext().getSession(true);
	}
	
	
	public static void removeItemSessao(String nomeItem){
	
		HttpSession sessao = recuperaSessao();
		
		if(sessao.getAttribute(nomeItem) != null)
			sessao.removeAttribute(nomeItem);		
	}
	
	/**
	 * retorna response ServLet para manipula��o
	 * @param resposta
	 * @return 
	 */
	public static HttpServletResponse getResponseServlet(){		
		
		//recupera contexto
		FacesContext context = FacesContext.getCurrentInstance();	
		HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
		
		return response;
	}	

	/**
	 * M�todo respons�vel por recuperar um item na sess�o
	 * baseado no par�metro recebido como nome
	 */
	public static Object recuperarItemSessao(String item){
		Object itemSessao = null;
		try {
		
			itemSessao = recuperaSessao().getAttribute(item);

		}catch(Exception e){
			itemSessao = null;
		}
		
		return itemSessao;
	}

	/**
	 * M�todo respons�vel por reter o valor do c�digo
	 * resgatado da sess�o entre requisi��es
	 * 
	 * @param itemSessao
	 * @return
	 */
	public static int getCodigoSessao(String nome){
		
		
		String itemSessao = FacesContext.getCurrentInstance().getExternalContext()
        			    	.getRequestParameterMap().get(nome);
		
		if(itemSessao != null){
			//guarda c�digo em vari�vel est�tica, opera��o necess�ria
			//pois entre os requests o valor da sess�o � perdido
			codigoSessao = Integer.parseInt(itemSessao);
		}
		
		return codigoSessao;
	}
	
	/**
	 * M�todo utilit�rio respons�vel por criar a sess�o do usu�rio
	 * caso ocorram problemas, capturo a sess�o e assumo falso para
	 * a opera��o
	 * 
	 * @param obj
	 * @return
	 */
	public static boolean setParametroSessao(Object obj, String nomeSessao){

		boolean isCriada = true;

		try{
			//criarParametroSessao(obj, nomeSessao);

			HttpSession session = recuperaSessao();
			
			if(session != null)
				session.setAttribute(nomeSessao, obj);

		} catch(Exception e) {
			e.printStackTrace();
			isCriada = false;
		}
		return isCriada;
	}
	
	/**
	 * M�todo respons�vel por recuperar a sess�o 
	 * eliminar mensagens que por ventura existam na 
	 * mesma e em seguida finalizar a sess�o de login 
	 * do usu�rio
	 */
	public static void finalizaSessao(){		

		HttpSession sessao = (HttpSession) recuperarItemSessao("mensagem");

		//elimino mensagens que existam na sess�o
		if(sessao != null)
			sessao.removeAttribute("mensagem");

		//finalizo a sessao do usuario
		sessao = (HttpSession) recuperaSessao();
		sessao.invalidate();	
	}

	public static void setCodigoSessao(int codigoSessao) {
		SessaoJSF.codigoSessao = codigoSessao;
	}	
}




