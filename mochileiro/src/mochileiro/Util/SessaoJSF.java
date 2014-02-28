package mochileiro.Util;


import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author $h@rk
 * @projeto mochileiro
 * @Data 17/03/2010 @Hora 23:34:56
 * @Descrição: Classe utilitária responsável por interagir com as sessões
 * 			   JSF e intermediar as requisições de sessão
 */

public class SessaoJSF {
	
	
	private static int codigoSessao = 0;
	

	/**
	 * método utilitário para resgatar sessão jsf
	 * 
	 * @return
	 */
	public static HttpSession recuperaSessao(){

		//Recupera o contexto jsf
		FacesContext fc = FacesContext.getCurrentInstance();
		//Recupera a sessão atual
		return (HttpSession) fc.getExternalContext().getSession(true);
	}
	
	
	public static void removeItemSessao(String nomeItem){
	
		HttpSession sessao = recuperaSessao();
		
		if(sessao.getAttribute(nomeItem) != null)
			sessao.removeAttribute(nomeItem);		
	}
	
	/**
	 * retorna response ServLet para manipulação
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
	 * Método responsável por recuperar um item na sessão
	 * baseado no parâmetro recebido como nome
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
	 * Método responsável por reter o valor do código
	 * resgatado da sessão entre requisições
	 * 
	 * @param itemSessao
	 * @return
	 */
	public static int getCodigoSessao(String nome){
		
		
		String itemSessao = FacesContext.getCurrentInstance().getExternalContext()
        			    	.getRequestParameterMap().get(nome);
		
		if(itemSessao != null){
			//guarda código em variável estática, operação necessária
			//pois entre os requests o valor da sessão é perdido
			codigoSessao = Integer.parseInt(itemSessao);
		}
		
		return codigoSessao;
	}
	
	/**
	 * Método utilitário responsável por criar a sessão do usuário
	 * caso ocorram problemas, capturo a sessão e assumo falso para
	 * a operação
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
	 * Método responsável por recuperar a sessão 
	 * eliminar mensagens que por ventura existam na 
	 * mesma e em seguida finalizar a sessão de login 
	 * do usuário
	 */
	public static void finalizaSessao(){		

		HttpSession sessao = (HttpSession) recuperarItemSessao("mensagem");

		//elimino mensagens que existam na sessão
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




