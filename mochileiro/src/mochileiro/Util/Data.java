package mochileiro.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author $h@rk
 * @projeto mochileiro2
 * @Data 16/03/2010 @Hora 21:50:39
 * @Descri��o: Classe utilit�ria de manipula��o de datas
 */
public class Data {

	private String dia;
	private String mes;
	private String ano;

	//atributo utilizado para exibi��o na view
	private String dataFormatada;

	private String diaSemana;

	private String horas;
	private String minutos;

	//atributo utilizado para exibi��o na view
	private String horaFormatada;

	public static final String DATA_INICIO_SISTEMA_YYYYMMDD = "19001231";
	public static final String DATA_FIM_SISTEMA_YYYYMMDD 	= "21001231";
	public static final String PADRAO_DATA_BR 				= "ddMMyyyy";	
	@SuppressWarnings("unused")
	public static final String PADRAO_HORA_BR = "HHmm";	
	public static final  int	DTS_IGUAIS = 0;
	public static final  int    DT_INICIAL_MAIOR_FINAL = 1;
	public static final  int    DT_FINAL_MAIOR_ATUAL = 2;
	public static final  int    DT_FINAL_MENOR_ATUAL = 3;
	public static final  int 	DATA_PADRAO_BD			   = 0;
	public static final  int    DATA_PADRAO_BARRAS		   = 1;
	public static final  int    DATA_PADRAO_SEM_FORMATACAO = 2;

	/**
	 * M�todo respons�vel por verificar se uma data � v�lida
	 * utiliza o padr�o ddMMyyyy como formata��o v�lida
	 * 
	 * @param data
	 * @return
	 */
	public boolean validaDataHora(String tempo, String padrao){

		boolean isFormatoValido = false;

		//realiza formata��o de campo
		if(this.formatadorData(tempo, padrao)!= null)
			isFormatoValido = true;			

		return isFormatoValido;
	}


	/**
	 * M�todo respons�vel por comparar duas datas, os par�metros de retorno s�o:
	 *
	 * 0 - datas iguais
	 * 1 - data inicial � maior que data final
	 * 2 - data final � maior que data atual
	 * 3 - data final � menor que data atual
	 * 4 - erro na conversao das datas
	 * @return int
	 */
	public int comparaDatas(Data dataInicial, Data dataFinal){

		int resultadoComparacao = -1;		

		try{

			//aplico formatador e crio objeto Date, false necess�rio pois formato n�o aceita barras
			Date dtInicial  = formatadorData(dataInicial.dataCompleta(false), PADRAO_DATA_BR);
			Date dtFinal 	= formatadorData(dataFinal.dataCompleta(false),   PADRAO_DATA_BR);
			Date dtAtual 	= new Date();


			//remove horas para evitar problemas na compara��o
			dtInicial 	= this.removerHora(dtInicial);
			dtFinal 	= this.removerHora(dtFinal);
			dtAtual 	= this.removerHora(dtAtual);

			if(dtInicial.after(dtFinal))
				resultadoComparacao = DT_INICIAL_MAIOR_FINAL;

			else if(dtInicial.equals(dtFinal))
				resultadoComparacao = DTS_IGUAIS;

			else if(dtFinal.after(dtAtual))
				resultadoComparacao = DT_FINAL_MAIOR_ATUAL;	

			else if(dtFinal.before(dtAtual))
				resultadoComparacao = DT_FINAL_MENOR_ATUAL;

		} catch(Exception e) {			
			//caso ocorra exce��o, ent�o datas par�metros s�o inv�lidas
			//seto c�digo de erro para retorno
			resultadoComparacao = 4;
		}
		return resultadoComparacao;
	}


	/**
	 * M�todo respons�vel por formatar uma String em
	 * uma data ou hora, par�metro formato define o tipo de
	 * formata��o, retorna null em caso de hora ou data inv�lidas
	 */
	public Date formatadorData(String data, String formato){

		Date formatacao = null;

		//crio padr�o de formata��o
		SimpleDateFormat format = new SimpleDateFormat(formato);

		//impede arredondamentos, caso ocorra lan�a uma exce��o
		format.setLenient(false);

		try{
			//cria objeto Date com valor enviado
			formatacao = format.parse(data);

		} catch(ParseException e){
			formatacao = null;
		}

		return formatacao; 
	}	

	/**
	 * M�todo utilit�rio que limpa horas das datas
	 * @param data
	 * @return Date
	 */
	private Date removerHora(Date data) { 		

		GregorianCalendar in = new GregorianCalendar();  
		if(data != null){
			in.setTime(data);  
			int dia = in.get(GregorianCalendar.DATE);  
			int mes = in.get(GregorianCalendar.MONTH);  
			int ano = in.get(GregorianCalendar.YEAR);  

			GregorianCalendar out = new GregorianCalendar();  
			out.set(ano, mes, dia, 0, 0, 0);  
			out.set(GregorianCalendar.MILLISECOND, 0);  

			return out.getTime();
		} else {
			return data;
		}
	}

	/**
	 * formata data no padr�o utilizado pelo banco de dados
	 * yyyy-MM-dd
	 * @return
	 */
	public String getDataBD(){

		//verifico se data n�o � nula
		String dataBD = null;
		if(!this.dia.equals(""))
			dataBD = this.ano+"-"+this.mes+"-"+this.dia;

		return dataBD;		
	}
	
	/**
	 * formata hora no padr�o utilizado pelo banco de dados
	 * 
	 * @return
	 */
	public String getHoraBD(){

		//verifico se hora n�o � nula
		String horaBD = null;
		if(!this.horas.equals(""))
			horaBD = this.horas+":"+this.minutos;

		return horaBD;		
	}
	

	
	
	public String getDataSemFormatacao(){

		//verifico se data n�o � nula
		String dataSemFormato = null;
		if(this.dia != null){
			
		  if(!this.dia.equals(""))
			dataSemFormato = this.ano+this.mes+this.dia;
		else
			dataSemFormato = "";
		}  
		return dataSemFormato;		
	}

	/**
	 * formata data do banco para padr�o ddMMyyyy
	 * @param dataBD
	 */
	public void setDataBR(String dataBD){

		//limpa String de caracteres especiais, express�o regular indica 
		//que tudo que n�o for n�meros deve ser substituido
		if(dataBD != null){
			//atrav�s da express�o regular limpa qualquer elemento diferente
			//de n�meros da string
			dataBD	 = 	dataBD.replaceAll("[^0-9]", "").toString();	
			this.ano =  dataBD.substring(0, 4);
			this.mes =  dataBD.substring(4, 6);
			this.dia =  dataBD.substring(6, 8);			
		}
	}

	public void setHoraBR(String horaBD){

		if(horaBD != null){
			//atrav�s da express�o regular limpo qualquer elemento diferente
			//de n�meros da string
			horaBD	 	  =  horaBD.replaceAll("[^0-9]", "").toString();	
			this.horas    =	 horaBD.substring(0, 2);
			this.minutos  =  horaBD.substring(2, 4);
		}
	}

	/**
	 * Retorna String correspondente a data no formato desejado
	 * Ex.: Data com barras - 15/02/2010
	 * 		Data sem barras - 15022010
	 */
	public String dataCompleta(boolean isBarras){

		String data = this.dia;

		//verifica��o necess�ria para evitar NullPointer
		if(data != null){
			if (isBarras)
				data = this.dia+"/"+this.mes+"/"+ano;
			else 
				data = this.dia+this.mes+this.ano;
		} else {
			data = "";
		}

		return data;
	}

	/**
	 * Retorna String concatenando ponto caso par�metro seja true
	 * ou apenas string caso par�metro seja false
	 * @param isPontos
	 * @return
	 */
	public String horaCompleta(boolean isPontos){

		String tempo = this.horas;

		//verifica��o necess�ria para evitar NullPointer
		if((tempo != null) && (!tempo.equals(""))){
			if(isPontos)
				tempo = this.horas+":"+this.minutos;
			else 
				tempo = this.horas+this.minutos;
		} 
		
		return tempo;
	}

	//getters e setters
	public String getDia() {
		return dia;
	}

	public void setDia(String dia) {
		this.dia = dia;
	}

	public String getMes() {
		return mes;
	}

	public void setMes(String mes) {
		this.mes = mes;
	}

	public String getAno() {
		return ano;
	}

	public void setAno(String ano) {
		this.ano = ano;
	}

	public String getDataFormatada() {
		return dataFormatada;
	}

	public void setDataFormatada(String dataFormatada) {
		this.dataFormatada = dataFormatada;
	}

	public String getDiaSemana() {
		return diaSemana;
	}

	public void setDiaSemana(String diaSemana) {
		this.diaSemana = diaSemana;
	}

	public String getHoras() {
		return horas;
	}

	public void setHoras(String horas) {
		this.horas = horas;
	}

	public String getMinutos() {
		return minutos;
	}

	public void setMinutos(String minutos) {
		this.minutos = minutos;
	}

	public String getHoraFormatada() {
		return horaFormatada;
	}

	public void setHoraFormatada(String horaFormatada) {
		this.horaFormatada = horaFormatada;
	}

}
