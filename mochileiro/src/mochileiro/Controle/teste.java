package mochileiro.Controle;

import java.text.NumberFormat;
import java.text.ParseException;

public class teste {

	
	
	/**
	 * @param args
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws ParseException {
		
		String valor = "1250,0";
		
		if(valor.contains(",")){
			valor = valor.replace(",", ".");
			System.out.println(valor);
		}
		
		int indicePrimeiroPonto = valor.indexOf(".");
		
		if(indicePrimeiroPonto != valor.lastIndexOf(".")){
			
			String part1 = valor.substring(0, indicePrimeiroPonto);
			String part2 = valor.substring(indicePrimeiroPonto+1, valor.length());
			valor = part1+part2;			
		}
		
		Double valor2 = Double.parseDouble(valor);	
		
		NumberFormat nf = NumberFormat.getInstance();
		
		System.out.println(nf.format(valor2));
		
		System.out.println(valor2);
		
		//adiciona duas casas decimais a string
		String precoFormatado = String.format("%.2f", valor2);
		
		System.out.println(precoFormatado);

	}

}
