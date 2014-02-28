package mochileiro.Util;

import java.text.ParseException;
import java.util.HashMap;

import mochileiro.Modelo.Atividade;
import mochileiro.Modelo.Empresa;
import mochileiro.Modelo.IAtividade;
import mochileiro.Modelo.Viajante;

import com.sun.xml.internal.ws.util.StringUtils;

/**
 * @author Thiago
 * @projeto mochileiro
 * @Data 14/02/2010 @Hora 10:47:07
 * @Descri��o: M�todo utilit�rio respons�vel por formatar informa��es do Objeto
 * 			   para exibi��o na view
 */
public class FormataObjetoLayout {

	public final static int LAYOUT_LISTA 	= 0;
	public final static int LAYOUT_COMPLETO = 1;
	public final static int PERSISTENCIA	= 2;

	private final static String sNAO_INFORMADO = "N�o informado";
	
	
	/**
	 * @overload
	 * 
	 * 
	 * 
	 * @param colecaoAtividade
	 * @param iView
	 */
	public static void formataExibicaoAtividade(HashMap<Integer, Atividade> colecaoAtividade, int iView){
		
		for(Atividade obj : colecaoAtividade.values())
		formataExibicaoAtividade(obj, iView);
		
	}

	/**
	 * Interface p�blica para formata��o de atividades adequando as mesmas
	 * ao layout determinado pelo par�metro iView
	 * @param obj
	 * @param iView
	 * @return
	 */
	public static void formataExibicaoAtividade(Atividade obj, int iView){

		if(iView == LAYOUT_LISTA)	
			formataLista(obj);

		else if(iView == LAYOUT_COMPLETO)
			formataExibicaoCompleta(obj);

		else if(iView == PERSISTENCIA)
			formataAtualizacao(obj);
	}
	
	
	/**
	 * M�todo respons�vel pela formata��o dos campos 
	 * para exibi��o no painel do viajante
	 * @param viajante
	 */
	public static void formataContaViajante(Viajante viajante){

		//formata��o do campo telefone
		viajante.setDDD(getDddFormatado(viajante.getTelefone()));
		viajante.setTelefone(getTelFormatado(viajante.getTelefone()));

		//formata data para exibi��o no padr�o dd/MM/yyyy
		viajante.getDataCadastro().setDataFormatada(viajante.getDataCadastro().dataCompleta(true));
		
		//captula��o dos campos
		captulacao(viajante.getNome());
		captulacao(viajante.getUsuario().getNickName());

	}
	
	/**
	 * M�todo respons�vel pela formata��o dos campos 
	 * para exibi��o no painel da empresa
	 * @param empresa
	 */
	public static void formataContaEmpresa(Empresa empresa){

		//formata��o do campo telefone
		empresa.setDddTel(getDddFormatado(empresa.getTelefone()));
		empresa.setTelefone(getTelFormatado(empresa.getTelefone()));

		//formata data para exibi��o no padr�o dd/MM/yyyy
		empresa.getDataCadastro().setDataFormatada(empresa.getDataCadastro().dataCompleta(true));
		
		//captula��o dos campos
		empresa.setNomeEmpresa(captulacao(empresa.getNomeEmpresa()));
		empresa.getUsuario().setNickName(captulacao(empresa.getUsuario().getNickName()));
		
		//formata nomes longos
		empresa.setNomeEmpresa(captulacao(limitaCampo(empresa.getNomeEmpresa(), 32)));
		empresa.getUsuario().setNickName(limitaCampo(empresa.getUsuario().getNickName(), 32));
	}
	
	

	/**
	 * M�todo respons�val por formatar parceiros/solicita��es
	 * @param obj
	 * @param limitaNome
	 */
	public static void formataParceriaSolicitacao(IAtividade obj, int limiteNome, int limiteDescricao){
		
		Data dataCadastro = obj.getDataCadastro();
			
		//formata data de cadastro da atividade principal
		dataCadastro.setDataFormatada(dataCadastro.dataCompleta(true));	
		
		//formata data do parceiro
		dataCadastro.setDataFormatada(dataCadastro.dataCompleta(true));	
		
		//conta a quantidade de recomenda��es (n�o distingue negativas ou positivas)
		if(obj.getListaComentarios() != null)
			obj.setComentarios(String.valueOf(obj.getListaComentarios().size()));			
		else 
			obj.setComentarios(String.valueOf(0));		
		
		//captula nome da categoria
		obj.getCategoria().setNome(captulacao(obj.getCategoria().getNome()));
		
		//formata nomes longos e captula
		obj.setNomeLista(captulacao(limitaCampo(obj.getNomeAtividade(), limiteNome)));	

		obj.setDescricaoLista(limitaCampo(obj.getDescricao(), limiteDescricao));
		
	}
	

	/**
	 * M�todo respons�vel por adequar os campos para exibi��o na tela de 
	 * atualiza��o das atividades
	 * 
	 * @param obj
	 * @return
	 */
	private static void formataAtualizacao(Atividade obj){

		//extraio objetos data para manipula��o, como existe ref�ncia
		//ao objeto original, qualquer altera��o ter� efeito em cascata
		Data dtInicio = obj.getDataInicial();
		Data dtFim	  = obj.getDataFinal();

		//manipula��o do campo pre�o		
		verificaCusto(obj, PERSISTENCIA);

		//verifica se dia da Semana apresenta mensagens	da view ou est� nulo
		//nessa situa��o seta valor em branco para os mesmos
		if(dtInicio.getDiaSemana() == null || dtInicio.getDiaSemana().equals(sNAO_INFORMADO)){

			dtInicio.setDiaSemana("");
			dtFim.setDiaSemana("");
		}

		//verifica se horas/minutos apresentam mensagens da view ou est�o nulas
		//nessa situa��o seta valor em branco para as mesmas
		if(dtInicio.getHoraFormatada()== null || dtInicio.getHoraFormatada().equals(sNAO_INFORMADO)){

			dtInicio.setHoraFormatada(null);
			dtFim.setHoraFormatada(null);

		} 

		//verifica se datas apresentam mensagens da view ou est�o nulas
		//nessa situa��o seta valor em branco para as mesmas
		//par�metro false define uma chamada sem barras
		if(dtInicio.dataCompleta(false).equals("") || dtInicio.dataCompleta(false).equals(sNAO_INFORMADO)){

			dtInicio.setDataFormatada("");
			dtFim.setDataFormatada("");
		}
	}

	/**
	 * M�todo respons�vel por adequar as informa��es exibidas na 
	 * view de visualiza��o das atividades das atividades
	 * 
	 * @param obj
	 * @return
	 */
	private static void formataExibicaoCompleta(Atividade obj){

		//extraio objetos data para manipula��o, como existe ref�ncia
		//ao objeto original, qualquer altera��o ter� efeito em cascata

		Data dtInicio = obj.getDataInicial();
		Data dtFim	  = obj.getDataFinal();

		//formata��o no campo descri��o, trocando quebra de linha
		//por elemento <p> HTML
		obj.setDescricaoLista(quebraLinha(obj.getDescricao(), true));
		
		obj.setNomeLista(limitaCampo(obj.getNomeAtividade(), 55));
		
				
		//verifica a exist�ncia de horas de funcionamento
		//caso contr�rio, envio mensagem para view		
		if(dtInicio.horaCompleta(false) == null){

			dtInicio.setHoraFormatada(sNAO_INFORMADO);
			dtFim.setHoraFormatada("");

		}else{

			//invoco m�todo de formata��o da hora (HH:mm) e concateno string ao campo
			dtInicio.setHoraFormatada(dtInicio.horaCompleta(true)+"  �s ");
			//invoco m�todo formatador e seto no campo hora
			dtFim.setHoraFormatada(dtFim.horaCompleta(true));			 
		}

		//verifico a exist�ncia de datas de funcionamento
		//caso contr�rio, envio mensagem para view
		if(dtInicio.dataCompleta(false).equals("") ){

			dtInicio.setDataFormatada(sNAO_INFORMADO);
			//evito nullPointer
			dtFim.setDataFormatada("");

		}else{			
			//caso existam datas, formato para exibi��o na view
			
			//invoco m�todo de formata��o e concateno caracter para exibi��o na view
			dtInicio.setDataFormatada(dtInicio.dataCompleta(true)+" a ");
			dtFim.setDataFormatada(dtFim.dataCompleta(true));
		}

		//verifico a exist�ncia de dias de funcionamento
		//caso contr�rio, envio mensagem para view
		if(dtInicio.getDiaSemana() == null || dtInicio.getDiaSemana().equals("")){

			dtInicio.setDiaSemana(sNAO_INFORMADO);
			dtFim.setDiaSemana("");

		} else {

			//invoco m�todo de formata��o e concateno caracter para exibi��o na view
			//invocando m�todo de captula��o
			
			dtInicio.setDiaSemana(captulacao(dtInicio.getDiaSemana())+" a ");
			dtFim.setDiaSemana(captulacao(dtFim.getDiaSemana()));
		}		
		
		//formata campo custo
		verificaCusto(obj, LAYOUT_COMPLETO);
		
		//inicia formata��o do parceiro caso o mesmo exista
		if(obj.getParceiro() != null){
			
			String nomeParceiro = obj.getParceiro().getNomeAtividade();
			
			//adequa o nome do parceiro para exibi��o na view
			nomeParceiro = limitaCampo(nomeParceiro, 20);
			
			//adequa descri��o do parceiro para exibi��o na view
			obj.getParceiro().setNomeLista(nomeParceiro);
			
			String descricaoParceiro = obj.getParceiro().getDescricao();
			
			descricaoParceiro = limitaCampo(descricaoParceiro, 50);
			
			obj.getParceiro().setDescricaoLista(descricaoParceiro);
						
		}
	}

	

	/**
	 * M�todo respons�vel por adequar as informa��es exibidas na lista de exibi��o 
	 * das atividades
	 * 
	 * @param obj
	 * @return
	 * @throws ParseException
	 */
	private static void formataLista(IAtividade obj){

		//ajusto nome da atividade para limite aceito pela view e aplico
		//captula��o, seto nome atualizado no objeto, preservando descri��o original
		obj.setNomeLista(FormataObjetoLayout.captulacao(FormataObjetoLayout.limitaCampo(obj.getNomeAtividade(), 53)));

		//captula��o nome categoria
		obj.getCategoria().setNome(FormataObjetoLayout.captulacao(obj.getCategoria().getNome()));

		//ajusto nome da atividade para limite aceito pela view e aplico
		//captula��o seto descri��o atualizada no objeto, preservando descri��o original
		obj.setDescricaoLista(FormataObjetoLayout.captulacao(FormataObjetoLayout.limitaCampo(obj.getDescricao(), 140)));
	}

	/**
	 * M�todo utilir�rio, utilizado para capitular primeira letra da palavra (par�grafo)
	 * 
	 * @param sDado
	 * @return
	 */
	public static String captulacao(String sDado){

		String dado = sDado.toLowerCase(); 
		return StringUtils.capitalize(dado);
	}

	/**
	 * Formata quebra de linha do texto trocando quebras de linha java 
	 * por elementos html, booleano define fluxo da substitui��o
	 * 
	 * @param sTexto
	 * @param isFormata
	 * @return
	 */
	private static String quebraLinha(String sTexto, boolean isFormata){

		String sQuebraLinha = "\n\r";
		String sHTML = "</p><p>";

		if(isFormata){
			sTexto = sTexto.replace(sQuebraLinha, sHTML);
		}
		else{
			sTexto = sTexto.replace(sHTML, sQuebraLinha);			
		}

		return sTexto;	
	}	

	/**
	 * M�todo respons�vel por limitar o n�mero de caracteres de
	 * um campo texto
	 * 
	 * @param sTexto
	 * @param iLimite
	 * @return
	 */
	public static String limitaCampo(String sTexto, int iLimite){

		if(sTexto.length() > iLimite)					
			sTexto =  sTexto.substring(0, iLimite);			

		return sTexto;
	}

	/**
	 * M�todo respons�vel por formatar campo custo conforme 
	 * opera��o em par�metro iView
	 *  
	 * @param obj
	 */
	private static void verificaCusto(Atividade obj, int iView){
		
		System.out.println(obj.getPreco());
		
		//caso objeto n�o possua custo
		if( obj.getPreco() == null || obj.getPreco().equals("")){
			
			//se layout for de visualiza��o
			if(iView == LAYOUT_COMPLETO){
				//seta mensagem informativa
				obj.setPreco("Sem Custo");

			//caso layout seja de grava��o	
			} else if(iView == PERSISTENCIA){
				
				obj.setPreco("");
				//seto booleano da view como true
				obj.setSemCusto(true);
			}
		//caso conte�do seja igual a "sem custo" limpa campo, pois se
		//trata de persist�ncia	
		} else if(obj.getPreco().trim().equalsIgnoreCase("Sem Custo")){
			//seto booleano da view como true
			obj.setSemCusto(true);
			obj.setPreco("");
		
		//caso possua valor e esteja em layout completo, 
		//formata exibi��o do pre�o 	
		} else if((!obj.getPreco().isEmpty()) && (iView == LAYOUT_COMPLETO)){
			
			//elimina pontos para evitar exce��o de m�ltiplos pontos
			obj.setPreco(formataPreco(obj.getPreco()));
			
			//converte em Double
			Double precoDouble = Double.parseDouble(obj.getPreco()); 
			
			//utiliza double para formatar valor
			obj.setPreco(String.format("%.2f", precoDouble));
			
		}
	}


	/**
	 * M�todo utilit�rio respons�vel por formatar a string �nica de telefone
	 * persistida no banco de dados em duas strings para exibi��o na view
	 * @return 
	 */
	public static String getDddFormatado(String TelCompleto){

		String ddd = "";
		//pega os dois primeiros caracteres e insere em DDD
		if(TelCompleto != null)
			ddd = TelCompleto.substring(0,2);

		return ddd; 
	}
	
	public static String getTelFormatado(String TelCompleto){

		String telefone = "";
		//pege os oito seguintes caractes e insere em telefone
		if(TelCompleto != null)
			telefone = TelCompleto.substring(2, TelCompleto.length());

		return telefone;
	}
	
	/**
	 * M�todo respons�vel por transformar uma String em valor
	 * v�lido para persist�ncia ex: 120 - 120.00 || 1.250,00 - 1250.00
	 * 
	 * @param valor
	 * @param simboloMonetario
	 */
	public static String formataPreco(String valor){
		
		if(valor != null ){
			
			//verifica se existem v�rgulas no valor
			//e substiui por ponto
			if(valor.contains(",")){
				valor = valor.replace(",", ".");
				System.out.println(valor);
			}
			
			int indicePrimeiroPonto = valor.indexOf(".");
			
			//caso exista mais de um ponto na string, ent�o...
			if(indicePrimeiroPonto != valor.lastIndexOf(".")){
				
				//elimina primeiro ponto da string
				String part1 = valor.substring(0, indicePrimeiroPonto);
				String part2 = valor.substring(indicePrimeiroPonto+1, valor.length());
				valor = part1+part2;			
			}
			
			//convers�o para double para manter formato 
			//(caso n�o passe pelas valida��es anteriores)			
			Double valor2 = Double.parseDouble(valor);
			
			//retorna String com resultado
			valor = String.valueOf(valor2);			
		}
		
		return ""+valor;
	}
	
}
