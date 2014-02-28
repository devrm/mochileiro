	
	/**
	 * @author Thiago
	 * @projeto mochileiro
	 * @Data 23/10/2009 @Hora 19:21
	 * @Descri��o: Script respons�vel por comandar a��es no formul�rio de atividades
	 */
 
	/**
	* Fun��o respons�vel por receber Array JSF contendo informa��es das categorias cadastradas 
	* no banco de dados e identificar descri��o compat�vel com c�digo da sele��o no combobox
	* @param arraylist<String>, option value
	* @see Class MBAtualizaEmpresa, Class Categoria
	*/	
	function setColecao(arrayJSF, opt){
		
		var str = new String(arrayJSF);			
		
		//"explode" variavel, criando um array tendo 
		//o elemento split como divisor do conte�do
		var strNew = str.split("|,");
		
		for(var cont=0; cont<strNew.length; cont+=3){
			
			//realiza compara��o entre o elemento do vetor 
			//e o valor passado pelo option do combobox
		    if(parseInt(strNew[cont]) == parseInt(opt)) {
		       
		       //invoca function de exibi��o da descri��o, 
		       //passando os elementos que ser�o mostrados na tela
		       exibeDescricao(strNew[cont+1], opt);
		       
		       //invoca function que determina exibi��o 
		       //de elementos ocultos no formul�rio
		      // periodoEvento(strNew[cont+2]);	       

		      cont = strNew.length;
		    
		    }else{			    	    
		    	//caso contr�rio anula exibi��o de elementos
		    	exibeDescricao(null, null);
		    	//periodoEvento(null);	       
		    }
		}	
	}
	
	/**
	* Fun��o respons�vel por exibir descri��o da categoria selecionada no combobox
	* tamb�m identifica se tipo selecionado faz parte de um servi�o, exibindo 
	* formul�rio complementar para o usu�rio
	* 
	* @param String Nome
	* @param Int    opt 
	*/
	function exibeDescricao(nome, opt){
		var div = document.getElementById("mensagem");
		var divDesc = document.getElementById("descricao");
		
		if(opt != null){
			div.innerHTML = nome;
			divDesc.style.display = "block";
			
		}else{
			divDesc.style.display = "none";
		}	
	}
	
	/* Fun��o respons�vel por exibir ou ocultar div correspondente ao per�odo de disponibilidade das
	 * atividades que ser�o cadastradas no sistema, essas informa��es s� est�o dispon�veis para eventos
	 *
	 * @param tipoAtividade 
	 */
	
	function periodoEvento(tipoAtividade){
		
		//define vari�vel com nome da sess�o que ser� exibida/ocultada
		var selecao = document.getElementById("eventos");
		
		//compara elemento recebido como par�metro e condi��o "evento"
		if (tipoAtividade == "evento") {										
			//caso o resultado seja verdadeiro, muda a propriedade 
			//display da div tornando a mesma vis�vel ao usu�rio
			selecao.style.display = "block";
				
		} else{				
			//caso contr�rio oculta a div
			selecao.style.display = "none";		
		}
	}
	
	function desabilitaPreco(){
	
	 //guarda elemento da view na vari�vel
	 var input = document.getElementById("cadatividade:preco");	 
	 var checkbox = document.getElementById("cadatividade:gratis");
	   	
  	//caso valor seja verdadeiro, significa que elemento est� desabilitado
  	if (checkbox.checked){ 
		//atualiza field input habilitando para edi��o
		input.disabled = true;		
   	}else{
   		//caso contr�rio, desabilita campo
  		check.disabled = false; 
  }          
}


	//fun��o respons�vel por confirmar junto ao usu�rio
	//a continuidade das opera��es solicitas
	//ok retorna true, cancel retorna false
	function operacao(codigo){
		
		var mensagem = '';
		switch(codigo){
			case 1:
				mensagem = 'Confirma a exclus�o da Parceria?';
				break;
				 
			case 2:
				mensagem = 'Ao aceitar uma nova solicita��o qualquer parceria existente ser� exclu�da\n'+
						   'Deseja realizar nova parceria?'; 	
				break;
									   
			case 3:
				mensagem = 'Confirma a exclus�o da Solicita��o?';	
				break;	
							
			case 4:
				mensagem = 'Confirma a exclus�o da Atividade?';
				break; 
		}		
		
		if(confirm(mensagem))
			return true;
		else
			return false;				
	}
	
	/**
	*Carrega pop-up de coment�rios
	*
	**/
	function popUp() {
		
		//configura itens da janela
		configPopUp="height=280,width=250,status=yes,toolbar=no,menubar=no,location=no,scrollbars=no, top=100, left=200";
		
		//realiza chamada da janela e seta p�gina alvo			
		window.open('menu_rel.xhtml','',configPopUp); 			
	}
	
	
