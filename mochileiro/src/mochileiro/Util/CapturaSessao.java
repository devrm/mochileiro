package mochileiro.Util;

import javax.faces.context.FacesContext;

/**
 * @author Thiago
 * @projeto mochileiro
 * @Data 15/02/2010 @Hora 16:28:17
 * @Descri��o: Classe utilit�ria para manipula��o de vari�veis na sess�o
 */
public class CapturaSessao {
	
	private static int iCodSessao = 0;
	
	/**
	 * Getter especial, respons�vel por resgatar o id da atividade da sess�o
	 */
	private static String getItemSessao(String param) {
        String sCod = (String)FacesContext.getCurrentInstance().getExternalContext()
            .getRequestParameterMap().get(param);
        return sCod;
    }
	
	private static void getCod(String sParam){

		String sCod = getItemSessao(sParam);			
			
		if(sCod != null)
			iCodSessao = Integer.parseInt(sCod);	
	}

	public static int getICodSessao(String sParam) {
		
		getCod(sParam);
		
		return iCodSessao;
	}

	public int getICodSessao() {
		return iCodSessao;
	}

	public void setICodSessao(int codSessao) {
		iCodSessao = codSessao;
	}

	
	
}
