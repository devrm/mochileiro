package mochileiro.Util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import mochileiro.Modelo.Usuario;

/**
 * 
 * @projeto mochileiro
 * @Data 06/12/2009 @Hora 09:52:46
 * @Descrição: Classe responsável por verificar o acesso restrito as páginas do sitema
 */

public class FiltroSeguranca implements Filter {
	private Usuario usuarioLogado;

	public static final String MENSAGEM_AUTENTICACAO = "Acesso Restrito! Entre com o usuario e a senha";

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain aChain) throws IOException, ServletException {

		//Captura a sessao
		HttpSession sessao = ((HttpServletRequest) request).getSession();

		//Captura o atributo usuario da sessao recuperada
		usuarioLogado = (Usuario) sessao.getAttribute("usuarioLogado");

		//Checa a existencia da sessao do usuario
		if (usuarioLogado != null){    	
			if(sessao.getAttribute("mensagem") != null)
				sessao.removeAttribute("mensagem");
			
			aChain.doFilter(request, response);
		}
		else {

			sessao.setAttribute("mensagem", MENSAGEM_AUTENTICACAO);
			((HttpServletResponse)response).sendRedirect("../login/index.xhtml");
		}
	}

	public void init(FilterConfig arg0) throws ServletException {

	}

	public void destroy() {

	}

}
