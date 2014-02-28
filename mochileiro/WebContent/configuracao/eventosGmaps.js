    
    var map;
    var gdir;
    var geocoder = null;
    var addressMarker;
    var locale = "pt_BR";
    
    //função responsável por criar objetos GoogleMaps e inicializar
    //processo de criação do mapa
    function initialize() {    
      	
      	//desabilita botão ao iniciar serviço de rota
   	 	document.getElementById('botao').disabled = true;
      	
      //verifica se browser é compatível com a funcionalidade	
      if(GBrowserIsCompatible()) {      
        
        //cria objeto map seta trecho html que que servirá como container para o mapa 
        map = new GMap2(document.getElementById("googleMaps"));
        
        //adiciona controles de zoom
        map.addControl(new GSmallMapControl());
        
        map.addControl(new GMapTypeControl());
        
        //cria objeto directions e seta trecho html que servirá como container para a rota
        gdir = new GDirections(map, document.getElementById("directions"));
        
        //seta eventos de escuta, função verificaErro executa captura de status
        //da operação
        GEvent.addListener(gdir, "load", onGDirectionsLoad);        
        GEvent.addListener(gdir, "error", verificaErro);
            	
    	//lê de campos hidden endereços que serão utilizados na rota
        setDirections(document.getElementById("from").value, document.getElementById("to").value, locale);                 
      }      
    }
    
    //função Google API, responsável por carregar o mapa baseado nos endereços
    //informados
    function setDirections(fromAddress, toAddress, locale) {
      gdir.load("from: " + fromAddress + " to: " + toAddress,
                { "locale": locale });
    }
      
    //recebe o retorno de objeto directions e verica status
    //da operação, em caso de erro envia mensagem ao usuário    
    function verificaErro(){    
   	  	     
     if (gdir.getStatus().code == G_GEO_UNKNOWN_ADDRESS) {
       alert("Não foi encontrada localização geográfica para um dos endereços especificados.\n Código de erro: " + gdir.getStatus().code);
     }
     else if (gdir.getStatus().code == G_GEO_SERVER_ERROR){
       alert("A geração dos pontos especificados falhou por uma falha na requisição.\n Código de erro: " + gdir.getStatus().code);
     }
     else if (gdir.getStatus().code == G_GEO_MISSING_QUERY){
       alert("Falta(m) parâmetro(s), ou o(s) mesmo(s) está(ão) inválido(s), para o processamento da requisição.\n Código de erro: " + gdir.getStatus().code);       
     } 
     else if (gdir.getStatus().code == G_GEO_BAD_KEY){
       alert("Erro na chave de utilização da API. \n Código de erro: " + gdir.getStatus().code);
    }
     else if (gdir.getStatus().code == G_GEO_BAD_REQUEST){
       alert("Erro no processamento da requisição.\n Código de erro: " + gdir.getStatus().code);
     }
     else {
     	alert("Erro desconhecido");
     }
     
   }   
    
    //função utilizada caso evento ouvinte retorne operação ok
    function onGDirectionsLoad(){
     	//habilita botão
   	 	document.getElementById('botao').disabled = false;
     }    
 