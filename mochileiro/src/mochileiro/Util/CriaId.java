package mochileiro.Util;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author $h@rk
 * @projeto mochileiro
 * @Data 01/05/2010 @Hora 20:49:50
 * @Descrição: classe utilitária criada com o intuito de minimizar o erro de Component ID 
 * 			   gerado pelo JSF (objeto deve ficar no contexto application)
 */
public class CriaId {

	private ArrayList<String> listaId = new ArrayList<String>(); 
	private String id;


	/**
	 * Gera uma chave aleatória baseado nos milisegundos e 
	 * concatena com um caracter após 
	 * @return
	 */
	private  void geraID(){
		Random rand = new Random(System.currentTimeMillis());
		boolean unico = true;

		while(unico){
			int num = rand.nextInt(999);

			if(num % 2 == 0)
				id = String.valueOf("R"+num);
			 else 
				id = String.valueOf("T"+num);
			
			if(!listaId.contains(id)){
				listaId.add(id);
				unico = false;
			}
		}
	}


	public String getId() {
		this.geraID();
		return id;
	}
}
