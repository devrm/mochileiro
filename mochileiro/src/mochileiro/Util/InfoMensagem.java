/**
 */
package mochileiro.Util;



import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * @author Thiago
 * @Descri��o:  Classe utilit�ria para exibi��o de mensagens no sistema
 */
public class InfoMensagem {

  
  public static void mensInfo(String message) {
    mensagem(message, FacesMessage.SEVERITY_INFO);
  }


  public static void mensErro(String message) {
    mensagem(message, FacesMessage.SEVERITY_ERROR);
  }

  /**
   * M�todo de mensagem sobrecarregado para possibilitar a passagem do componente do formul�rio, sendo assim,
   * possibilitando a visualiza��o do erro de cada componente
   * @param aComponente
   * @param message
   */
  public static void mensInfo(String aComponente, String message) {

    FacesMessage menssagem = new FacesMessage(message);
    
    FacesContext.getCurrentInstance().addMessage(aComponente, menssagem);
    
    mensagem(message, FacesMessage.SEVERITY_ERROR);
  }

  
  public static void mensagem(String message, 
      FacesMessage.Severity severity) {
	  
    FacesContext.getCurrentInstance().
    addMessage(null, new FacesMessage(severity, message, null));
  }

}

