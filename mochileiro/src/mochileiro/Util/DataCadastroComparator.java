package mochileiro.Util;

import java.util.Comparator;
import java.util.Date;

import mochileiro.Modelo.Atividade;

/**
 * @author Thiago
 * @projeto mochileiro
 * @Data 31/01/2010 @Hora 14:07:06
 * @Descri��o: Classe utilit�ria utilizada para ordenar elementos de um arraylist<Atividades>
 * 			   implementa interface comparator para a tarefa de ordena��o

 */
public class DataCadastroComparator implements Comparator<Atividade> {

	/**
	 * M�todo respons�vel pela compara��o entre datas de cadastro
	 */
	public int compare(Atividade atv1, Atividade atv2) {
		//invoco m�todo formatador para criar objetos do tipo Date
		Date data1 = atv1.getDataCadastro().formatadorData(atv1.getDataCadastro().dataCompleta(false), Data.PADRAO_DATA_BR);
        Date data2 = atv2.getDataCadastro().formatadorData(atv2.getDataCadastro().dataCompleta(false), Data.PADRAO_DATA_BR);
        
        //caso datas n�o sejam inv�lidas
        if(data1 == null || data2 == null ) {
            throw new NullPointerException();
        }
        
        //comparo datas e retorno resultado
        return data1.compareTo(data2);
	}	
	
}
