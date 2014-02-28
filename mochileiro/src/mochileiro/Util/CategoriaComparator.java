package mochileiro.Util;

import java.util.Comparator;

import mochileiro.Modelo.Atividade;

/**
 * @author Thiago
 * @projeto mochileiro
 * @Data 31/01/2010 @Hora 13:47:34
 * @Descrição: Classe utilitária utilizada para ordenar elementos de um arraylist<Atividades>
 * 			   implementa interface comparator para a tarefa de ordenação
 */
public class CategoriaComparator implements Comparator<Atividade> {
	
	/**
	 * Realizo comparação entre códigos das categorias para ordenação
	 * utilizo códigos das categorias como chave de ordenação, assim otimizo
	 * a operação ao contrário de usar o nome da categoria como chave de ordenação
	 * 
	 * por fim, realizo uma ordenação alfabética
	 * 
	 */
	public int compare(Atividade atv1, Atividade atv2) {
		 
		int iComparacao = 0;
		
		//capturo o código referente a categoria para comparações
		int codCategoria1 = atv1.getCategoria().getCodigo();  
		int codCategoria2 = atv2.getCategoria().getCodigo(); 	
		
		//ordeno pelo código da categoria
		if(codCategoria1 > codCategoria2)
			//ordeno em ordem alfabética
			iComparacao = atv1.getCategoria().getNome().compareTo(atv2.getCategoria().getNome());
		//ordeno por código da categoria
		else if(codCategoria1 == codCategoria2)
			//ordeno por ordem alfabética
			iComparacao = atv1.getCategoria().getNome().compareTo(atv2.getCategoria().getNome());
		//ordeno por código da categoria
		else if(codCategoria1 < codCategoria2)
			//ordeno por ordem alfabética
			iComparacao = atv1.getCategoria().getNome().compareTo(atv2.getCategoria().getNome());
		
		return iComparacao;
	}

}
