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
 * @Descrição: Método utilitário responsável por formatar informações do Objeto
 * 			   para exibição na view
 */
public class FormataObjetoLayout {

	public final static int LAYOUT_LISTA 	= 0;
	public final static int LAYOUT_COMPLETO = 1;
	public final static int PERSISTENCIA	= 2;

	private final static String sNAO_INFORMADO = "Não informado";
	
	
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
	 * Interface pública para formatação de atividades adequando as mesmas
	 * ao layout determinado pelo parâmetro iView
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
	 * Método responsável pela formatação dos campos 
	 * para exibição no painel do viajante
	 * @param viajante
	 */
	public static void formataContaViajante(Viajante viajante){

		//formatação do campo telefone
		viajante.setDDD(getDddFormatado(viajante.getTelefone()));
		viajante.setTelefone(getTelFormatado(viajante.getTelefone()));

		//formata data para exibição no padrão dd/MM/yyyy
		viajante.getDataCadastro().setDataFormatada(viajante.getDataCadastro().dataCompleta(true));
		
		//captulação dos campos
		captulacao(viajante.getNome());
		captulacao(viajante.getUsuario().getNickName());

	}
	
	/**
	 * Método responsável pela formatação dos campos 
	 * para exibição no painel da empresa
	 * @param empresa
	 */
	public static void formataContaEmpresa(Empresa empresa){

		//formatação do campo telefone
		empresa.setDddTel(getDddFormatado(empresa.getTelefone()));
		empresa.setTelefone(getTelFormatado(empresa.getTelefone()));

		//formata data para exibição no padrão dd/MM/yyyy
		empresa.getDataCadastro().setDataFormatada(empresa.getDataCadastro().dataCompleta(true));
		
		//captulação dos campos
		empresa.setNomeEmpresa(captulacao(empresa.getNomeEmpresa()));
		empresa.getUsuario().setNickName(captulacao(empresa.getUsuario().getNickName()));
		
		//formata nomes longos
		empresa.setNomeEmpresa(captulacao(limitaCampo(empresa.getNomeEmpresa(), 32)));
		empresa.getUsuario().setNickName(limitaCampo(empresa.getUsuario().getNickName(), 32));
	}
	
	

	/**
	 * Método responsával por formatar parceiros/solicitações
	 * @param obj
	 * @param limitaNome
	 */
	public static void formataParceriaSolicitacao(IAtividade obj, int limiteNome, int limiteDescricao){
		
		Data dataCadastro = obj.getDataCadastro();
			
		//formata data de cadastro da atividade principal
		dataCadastro.setDataFormatada(dataCadastro.dataCompleta(true));	
		
		//formata data do parceiro
		dataCadastro.setDataFormatada(dataCadastro.dataCompleta(true));	
		
		//conta a quantidade de recomendações (não distingue negativas ou positivas)
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
	 * Método responsável por adequar os campos para exibição na tela de 
	 * atualização das atividades
	 * 
	 * @param obj
	 * @return
	 */
	private static void formataAtualizacao(Atividade obj){

		//extraio objetos data para manipulação, como existe refência
		//ao objeto original, qualquer alteração terá efeito em cascata
		Data dtInicio = obj.getDataInicial();
		Data dtFim	  = obj.getDataFinal();

		//manipulação do campo preço		
		verificaCusto(obj, PERSISTENCIA);

		//verifica se dia da Semana apresenta mensagens	da view ou está nulo
		//nessa situação seta valor em branco para os mesmos
		if(dtInicio.getDiaSemana() == null || dtInicio.getDiaSemana().equals(sNAO_INFORMADO)){

			dtInicio.setDiaSemana("");
			dtFim.setDiaSemana("");
		}

		//verifica se horas/minutos apresentam mensagens da view ou estão nulas
		//nessa situação seta valor em branco para as mesmas
		if(dtInicio.getHoraFormatada()== null || dtInicio.getHoraFormatada().equals(sNAO_INFORMADO)){

			dtInicio.setHoraFormatada(null);
			dtFim.setHoraFormatada(null);

		} 

		//verifica se datas apresentam mensagens da view ou estão nulas
		//nessa situação seta valor em branco para as mesmas
		//parâmetro false define uma chamada sem barras
		if(dtInicio.dataCompleta(false).equals("") || dtInicio.dataCompleta(false).equals(sNAO_INFORMADO)){

			dtInicio.setDataFormatada("");
			dtFim.setDataFormatada("");
		}
	}

	/**
	 * Método responsável por adequar as informações exibidas na 
	 * view de visualização das atividades das atividades
	 * 
	 * @param obj
	 * @return
	 */
	private static void formataExibicaoCompleta(Atividade obj){

		//extraio objetos data para manipulação, como existe refência
		//ao objeto original, qualquer alteração terá efeito em cascata

		Data dtInicio = obj.getDataInicial();
		Data dtFim	  = obj.getDataFinal();

		//formatação no campo descrição, trocando quebra de linha
		//por elemento <p> HTML
		obj.setDescricaoLista(quebraLinha(obj.getDescricao(), true));
		
		obj.setNomeLista(limitaCampo(obj.getNomeAtividade(), 55));
		
				
		//verifica a existência de horas de funcionamento
		//caso contrário, envio mensagem para view		
		if(dtInicio.horaCompleta(false) == null){

			dtInicio.setHoraFormatada(sNAO_INFORMADO);
			dtFim.setHoraFormatada("");

		}else{

			//invoco método de formatação da hora (HH:mm) e concateno string ao campo
			dtInicio.setHoraFormatada(dtInicio.horaCompleta(true)+"  às ");
			//invoco método formatador e seto no campo hora
			dtFim.setHoraFormatada(dtFim.horaCompleta(true));			 
		}

		//verifico a existência de datas de funcionamento
		//caso contrário, envio mensagem para view
		if(dtInicio.dataCompleta(false).equals("") ){

			dtInicio.setDataFormatada(sNAO_INFORMADO);
			//evito nullPointer
			dtFim.setDataFormatada("");

		}else{			
			//caso existam datas, formato para exibição na view
			
			//invoco método de formatação e concateno caracter para exibição na view
			dtInicio.setDataFormatada(dtInicio.dataCompleta(true)+" a ");
			dtFim.setDataFormatada(dtFim.dataCompleta(true));
		}

		//verifico a existência de dias de funcionamento
		//caso contrário, envio mensagem para view
		if(dtInicio.getDiaSemana() == null || dtInicio.getDiaSemana().equals("")){

			dtInicio.setDiaSemana(sNAO_INFORMADO);
			dtFim.setDiaSemana("");

		} else {

			//invoco método de formatação e concateno caracter para exibição na view
			//invocando método de captulação
			
			dtInicio.setDiaSemana(captulacao(dtInicio.getDiaSemana())+" a ");
			dtFim.setDiaSemana(captulacao(dtFim.getDiaSemana()));
		}		
		
		//formata campo custo
		verificaCusto(obj, LAYOUT_COMPLETO);
		
		//inicia formatação do parceiro caso o mesmo exista
		if(obj.getParceiro() != null){
			
			String nomeParceiro = obj.getParceiro().getNomeAtividade();
			
			//adequa o nome do parceiro para exibição na view
			nomeParceiro = limitaCampo(nomeParceiro, 20);
			
			//adequa descrição do parceiro para exibição na view
			obj.getParceiro().setNomeLista(nomeParceiro);
			
			String descricaoParceiro = obj.getParceiro().getDescricao();
			
			descricaoParceiro = limitaCampo(descricaoParceiro, 50);
			
			obj.getParceiro().setDescricaoLista(descricaoParceiro);
						
		}
	}

	

	/**
	 * Método responsável por adequar as informações exibidas na lista de exibição 
	 * das atividades
	 * 
	 * @param obj
	 * @return
	 * @throws ParseException
	 */
	private static void formataLista(IAtividade obj){

		//ajusto nome da atividade para limite aceito pela view e aplico
		//captulação, seto nome atualizado no objeto, preservando descrição original
		obj.setNomeLista(FormataObjetoLayout.captulacao(FormataObjetoLayout.limitaCampo(obj.getNomeAtividade(), 53)));

		//captulação nome categoria
		obj.getCategoria().setNome(FormataObjetoLayout.captulacao(obj.getCategoria().getNome()));

		//ajusto nome da atividade para limite aceito pela view e aplico
		//captulação seto descrição atualizada no objeto, preservando descrição original
		obj.setDescricaoLista(FormataObjetoLayout.captulacao(FormataObjetoLayout.limitaCampo(obj.getDescricao(), 140)));
	}

	/**
	 * Método utilirário, utilizado para capitular primeira letra da palavra (parágrafo)
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
	 * por elementos html, booleano define fluxo da substituição
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
	 * Método responsável por limitar o número de caracteres de
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
	 * Método responsável por formatar campo custo conforme 
	 * operação em parâmetro iView
	 *  
	 * @param obj
	 */
	private static void verificaCusto(Atividade obj, int iView){
		
		System.out.println(obj.getPreco());
		
		//caso objeto não possua custo
		if( obj.getPreco() == null || obj.getPreco().equals("")){
			
			//se layout for de visualização
			if(iView == LAYOUT_COMPLETO){
				//seta mensagem informativa
				obj.setPreco("Sem Custo");

			//caso layout seja de gravação	
			} else if(iView == PERSISTENCIA){
				
				obj.setPreco("");
				//seto booleano da view como true
				obj.setSemCusto(true);
			}
		//caso conteúdo seja igual a "sem custo" limpa campo, pois se
		//trata de persistência	
		} else if(obj.getPreco().trim().equalsIgnoreCase("Sem Custo")){
			//seto booleano da view como true
			obj.setSemCusto(true);
			obj.setPreco("");
		
		//caso possua valor e esteja em layout completo, 
		//formata exibição do preço 	
		} else if((!obj.getPreco().isEmpty()) && (iView == LAYOUT_COMPLETO)){
			
			//elimina pontos para evitar exceção de múltiplos pontos
			obj.setPreco(formataPreco(obj.getPreco()));
			
			//converte em Double
			Double precoDouble = Double.parseDouble(obj.getPreco()); 
			
			//utiliza double para formatar valor
			obj.setPreco(String.format("%.2f", precoDouble));
			
		}
	}


	/**
	 * Método utilitário responsável por formatar a string única de telefone
	 * persistida no banco de dados em duas strings para exibição na view
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
	 * Método responsável por transformar uma String em valor
	 * válido para persistência ex: 120 - 120.00 || 1.250,00 - 1250.00
	 * 
	 * @param valor
	 * @param simboloMonetario
	 */
	public static String formataPreco(String valor){
		
		if(valor != null ){
			
			//verifica se existem vírgulas no valor
			//e substiui por ponto
			if(valor.contains(",")){
				valor = valor.replace(",", ".");
				System.out.println(valor);
			}
			
			int indicePrimeiroPonto = valor.indexOf(".");
			
			//caso exista mais de um ponto na string, então...
			if(indicePrimeiroPonto != valor.lastIndexOf(".")){
				
				//elimina primeiro ponto da string
				String part1 = valor.substring(0, indicePrimeiroPonto);
				String part2 = valor.substring(indicePrimeiroPonto+1, valor.length());
				valor = part1+part2;			
			}
			
			//conversão para double para manter formato 
			//(caso não passe pelas validações anteriores)			
			Double valor2 = Double.parseDouble(valor);
			
			//retorna String com resultado
			valor = String.valueOf(valor2);			
		}
		
		return ""+valor;
	}
	
}
