    
    var map;
    var gdir;
    var geocoder = null;
    var addressMarker;
    var locale = "pt_BR";
    
    //fun��o respons�vel por criar objetos GoogleMaps e inicializar
    //processo de cria��o do mapa
    function initialize() {    
      	
      	//desabilita bot�o ao iniciar servi�o de rota
   	 	document.getElementById('botao').disabled = true;
      	
      //verifica se browser � compat�vel com a funcionalidade	
      if(GBrowserIsCompatible()) {      
        
        //cria objeto map seta trecho html que que servir� como container para o mapa 
        map = new GMap2(document.getElementById("googleMaps"));
        
        //adiciona controles de zoom
        map.addControl(new GSmallMapControl());
        
        map.addControl(new GMapTypeControl());
        
        //cria objeto directions e seta trecho html que servir� como container para a rota
        gdir = new GDirections(map, document.getElementById("directions"));
        
        //seta eventos de escuta, fun��o verificaErro executa captura de status
        //da opera��o
        GEvent.addListener(gdir, "load", onGDirectionsLoad);        
        GEvent.addListener(gdir, "error", verificaErro);
            	
    	//l� de campos hidden endere�os que ser�o utilizados na rota
        setDirections(document.getElementById("from").value, document.getElementById("to").value, locale);                 
      }      
    }
    
    //fun��o Google API, respons�vel por carregar o mapa baseado nos endere�os
    //informados
    function setDirections(fromAddress, toAddress, locale) {
      gdir.load("from: " + fromAddress + " to: " + toAddress,
                { "locale": locale });
    }
      
    //recebe o retorno de objeto directions e verica status
    //da opera��o, em caso de erro envia mensagem ao usu�rio    
    function verificaErro(){    
   	  	     
     if (gdir.getStatus().code == G_GEO_UNKNOWN_ADDRESS) {
       alert("N�o foi encontrada localiza��o geogr�fica para um dos endere�os especificados.\n C�digo de erro: " + gdir.getStatus().code);
     }
     else if (gdir.getStatus().code == G_GEO_SERVER_ERROR){
       alert("A gera��o dos pontos especificados falhou por uma falha na requisi��o.\n C�digo de erro: " + gdir.getStatus().code);
     }
     else if (gdir.getStatus().code == G_GEO_MISSING_QUERY){
       alert("Falta(m) par�metro(s), ou o(s) mesmo(s) est�(�o) inv�lido(s), para o processamento da requisi��o.\n C�digo de erro: " + gdir.getStatus().code);       
     } 
     else if (gdir.getStatus().code == G_GEO_BAD_KEY){
       alert("Erro na chave de utiliza��o da API. \n C�digo de erro: " + gdir.getStatus().code);
    }
     else if (gdir.getStatus().code == G_GEO_BAD_REQUEST){
       alert("Erro no processamento da requisi��o.\n C�digo de erro: " + gdir.getStatus().code);
     }
     else {
     	alert("Erro desconhecido");
     }
     
   }   
    
    //fun��o utilizada caso evento ouvinte retorne opera��o ok
    function onGDirectionsLoad(){
     	//habilita bot�o
   	 	document.getElementById('botao').disabled = false;
     }    
 