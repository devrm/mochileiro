	
	/**
	 * @author Thiago
	 * @projeto mochileiro
	 * @Data 23/10/2009 @Hora 19:21
	 * @Descrição: Script responsável por comandar ações no formulário de atividades
	 */
 
	/**
	* Função responsável por receber Array JSF contendo informações das categorias cadastradas 
	* no banco de dados e identificar descrição compatível com código da seleção no combobox
	* @param arraylist<String>, option value
	* @see Class MBAtualizaEmpresa, Class Categoria
	*/	
	function setColecao(arrayJSF, opt){
		
		var str = new String(arrayJSF);			
		
		//"explode" variavel, criando um array tendo 
		//o elemento split como divisor do conteúdo
		var strNew = str.split("|,");
		
		for(var cont=0; cont<strNew.length; cont+=3){
			
			//realiza comparação entre o elemento do vetor 
			//e o valor passado pelo option do combobox
		    if(parseInt(strNew[cont]) == parseInt(opt)) {
		       
		       //invoca function de exibição da descrição, 
		       //passando os elementos que serão mostrados na tela
		       exibeDescricao(strNew[cont+1], opt);
		       
		       //invoca function que determina exibição 
		       //de elementos ocultos no formulário
		      // periodoEvento(strNew[cont+2]);	       

		      cont = strNew.length;
		    
		    }else{			    	    
		    	//caso contrário anula exibição de elementos
		    	exibeDescricao(null, null);
		    	//periodoEvento(null);	       
		    }
		}	
	}
	
	/**
	* Função responsável por exibir descrição da categoria selecionada no combobox
	* também identifica se tipo selecionado faz parte de um serviço, exibindo 
	* formulário complementar para o usuário
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
	
	/* Função responsável por exibir ou ocultar div correspondente ao período de disponibilidade das
	 * atividades que serão cadastradas no sistema, essas informações só estão disponíveis para eventos
	 *
	 * @param tipoAtividade 
	 */
	
	function periodoEvento(tipoAtividade){
		
		//define variável com nome da sessão que será exibida/ocultada
		var selecao = document.getElementById("eventos");
		
		//compara elemento recebido como parâmetro e condição "evento"
		if (tipoAtividade == "evento") {										
			//caso o resultado seja verdadeiro, muda a propriedade 
			//display da div tornando a mesma visível ao usuário
			selecao.style.display = "block";
				
		} else{				
			//caso contrário oculta a div
			selecao.style.display = "none";		
		}
	}
	
	function desabilitaPreco(){
	
	 //guarda elemento da view na variável
	 var input = document.getElementById("cadatividade:preco");	 
	 var checkbox = document.getElementById("cadatividade:gratis");
	   	
  	//caso valor seja verdadeiro, significa que elemento está desabilitado
  	if (checkbox.checked){ 
		//atualiza field input habilitando para edição
		input.disabled = true;		
   	}else{
   		//caso contrário, desabilita campo
  		check.disabled = false; 
  }          
}


	//função responsável por confirmar junto ao usuário
	//a continuidade das operações solicitas
	//ok retorna true, cancel retorna false
	function operacao(codigo){
		
		var mensagem = '';
		switch(codigo){
			case 1:
				mensagem = 'Confirma a exclusão da Parceria?';
				break;
				 
			case 2:
				mensagem = 'Ao aceitar uma nova solicitação qualquer parceria existente será excluída\n'+
						   'Deseja realizar nova parceria?'; 	
				break;
									   
			case 3:
				mensagem = 'Confirma a exclusão da Solicitação?';	
				break;	
							
			case 4:
				mensagem = 'Confirma a exclusão da Atividade?';
				break; 
		}		
		
		if(confirm(mensagem))
			return true;
		else
			return false;				
	}
	
	/**
	*Carrega pop-up de comentários
	*
	**/
	function popUp() {
		
		//configura itens da janela
		configPopUp="height=280,width=250,status=yes,toolbar=no,menubar=no,location=no,scrollbars=no, top=100, left=200";
		
		//realiza chamada da janela e seta página alvo			
		window.open('menu_rel.xhtml','',configPopUp); 			
	}
	
	
