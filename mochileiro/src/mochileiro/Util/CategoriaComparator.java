package mochileiro.Util;

import java.util.Comparator;

import mochileiro.Modelo.Atividade;

/**
 * @author Thiago
 * @projeto mochileiro
 * @Data 31/01/2010 @Hora 13:47:34
 * @Descri��o: Classe utilit�ria utilizada para ordenar elementos de um arraylist<Atividades>
 * 			   implementa interface comparator para a tarefa de ordena��o
 */
public class CategoriaComparator implements Comparator<Atividade> {
	
	/**
	 * Realizo compara��o entre c�digos das categorias para ordena��o
	 * utilizo c�digos das categorias como chave de ordena��o, assim otimizo
	 * a opera��o ao contr�rio de usar o nome da categoria como chave de ordena��o
	 * 
	 * por fim, realizo uma ordena��o alfab�tica
	 * 
	 */
	public int compare(Atividade atv1, Atividade atv2) {
		 
		int iComparacao = 0;
		
		//capturo o c�digo referente a categoria para compara��es
		int codCategoria1 = atv1.getCategoria().getCodigo();  
		int codCategoria2 = atv2.getCategoria().getCodigo(); 	
		
		//ordeno pelo c�digo da categoria
		if(codCategoria1 > codCategoria2)
			//ordeno em ordem alfab�tica
			iComparacao = atv1.getCategoria().getNome().compareTo(atv2.getCategoria().getNome());
		//ordeno por c�digo da categoria
		else if(codCategoria1 == codCategoria2)
			//ordeno por ordem alfab�tica
			iComparacao = atv1.getCategoria().getNome().compareTo(atv2.getCategoria().getNome());
		//ordeno por c�digo da categoria
		else if(codCategoria1 < codCategoria2)
			//ordeno por ordem alfab�tica
			iComparacao = atv1.getCategoria().getNome().compareTo(atv2.getCategoria().getNome());
		
		return iComparacao;
	}

}
