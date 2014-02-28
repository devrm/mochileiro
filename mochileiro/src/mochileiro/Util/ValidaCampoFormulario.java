package mochileiro.Util;

/**
 * @project mochileiro
 * 05/09/2009
 * @Descrição: Classe responsável pela validação dos campo CEP dos forms de
 * 			   cadastro de viajante e empresa
 * 
 * @CasoDeUuso: Manter Empresas e Manter Viajantes
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;



//Implementa interface Validator do JSF	 
public class ValidaCampoFormulario implements Validator {

	//Declara dois atributos contendo a expressão regular e a mensagem de erro padrão (pode ser redefinida na view)
	private String expressaoRegular = "";
	private String msgErro = "";

	/**
	 * Implementa o método validate da interface
	 * @param context
	 * @param componente
	 * @param objeto
	 * @throws ValidatorException
	 */
	public void validate(FacesContext context, UIComponent componente,
			Object objeto) throws ValidatorException {

		//Casting necessário para utilizar as informações enviadas pela view
		String valorEntrada = (String) objeto;

		//Resgata o ID do componente 
		String componenteView = componente.getId();

		//Determina qual dos campos será validado com base no id dos inputs na View
		if (componenteView.equals("cnpj")) {				
			this.msgErro = "CNPJ deve conter 14 dígitos numéricos";
			this.expressaoRegular = "[0-9]{14}";
		}

		if (componenteView.equals("cpf")) {				
			this.msgErro = "CPF deve conter 11 dígitos numéricos";
			this.expressaoRegular = "[0-9]{11}";
		}

		if (componenteView.equals("cep")) {				
			this.msgErro = "CEP deve conter 8 digitos";
			this.expressaoRegular = "[0-9]{8}";
		}

		if (componenteView.equals("ddd")) {				
			this.msgErro = "DDD inválido";
			this.expressaoRegular = "[0-9]{2}";
		}

		if (componenteView.equals("telefone")) {				
			this.msgErro = "Telefone inválido";
			this.expressaoRegular = "[0-9]{8}";
		}

		if (componenteView.equals("email")) {				
			this.msgErro = "E-mail inválido";
			this.expressaoRegular =".+@.+\\.[a-zA-Z]+";
		}

		if (componenteView.equals("usuario")) {				
			this.msgErro = "Login inválido";
			this.expressaoRegular = "([a-zA-Z]|[0-9]){3,25}";
		}

		if (componenteView.equals("senha")) {				
			this.msgErro = "Minimo 6 caracteres";
			this.expressaoRegular = "([a-zA-Z]|[0-9]){6,25}";
		}

		//validação de custo, aceitando somente números
		//e vírgulas ou pontos exemplo: 000.000,00
		if(componenteView.equals("preco")){
			this.msgErro = "Digite um valor no formato 000.000,00";
			this.expressaoRegular = "[0-9 \\. \\,]{0,10}";
		}

		//Utiliza método da classe Regex para compilar a expressão regular em seguida utiliza 
		//Classe Matcher para realizar comparação da expressão com os dados enviados pela view
		Pattern p = Pattern.compile(expressaoRegular);
		Matcher m = p.matcher(valorEntrada);

		boolean matchFound = m.matches();

		//Caso a condicional retorne TRUE significa que o valor obtido da view está irregular
		if (!matchFound) {
			//Instância um Objeto Message,define mensagem retorna exceção com 
			//mensagem para exibição conforme componente capturado em componenteView 
			FacesMessage message = new FacesMessage();

			//insere mensagem de erro
			message.setDetail(msgErro);
			message.setSummary(msgErro);

			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(message);
		}

	}

}

