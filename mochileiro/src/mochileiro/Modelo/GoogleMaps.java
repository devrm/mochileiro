package mochileiro.Modelo;

import java.io.IOException;
import java.net.InetAddress;


/**
 * @author $h@rk
 * @projeto mochileiro
 * @Data 29/03/2010 @Hora 20:15:48
 * @Descri��o: classe respons�vel por controlar informa��es referente as rotas do
 * 				GoogleMaps
 */
public class GoogleMaps {

	//objeto utilizado para resgatar a rota
	private Viajante viajante;

	//String utilizada como retorno de erros
	private String   erroMaps;

	//carrega objetos representando atividade origem e destino
	private Rota rotaMaps;

	//atributos utilizados na visualiza��o da rota
	private String origem = "Av. Paulista - S�o Paulo";

	private String destino = "Pra�a da S�, S�o Paulo";

	private String dataRota = "01/07/2006";

	private String nomeOrigem ="";

	private String nomeDestino ="";

	public static String ROTA_SELECIONADA = "objMaps";

	private  final String semRotasPesquisadas = " N�o existem rotas pesquisada recentemente. " +
												" Para visualizar uma rota, selecione duas atividades " +
												" em sua lista de favoritos ou selecione o seu endere�o " +
												" e uma atividade destino  e acesse  a op��o 'Gerar Rota!'";

	private final String erroConexao = 			" N�o foi poss�vel estabelecer conex�o com o servi�o de Mapas. " +
												" Essa falha pode ocorrer caso n�o exista conectividade com o " +
												" servi�o de rotas utilizado pela aplica��o. " +
												" Tente novamente em alguns instantes";

	public GoogleMaps(Viajante viajante){

		this.viajante = viajante;	
	}



	/**
	 * M�todo respons�vel por verificar se existe conectividade 
	 * entre a aplica��o e o servi�o de mapas
	 * 
	 * @return
	 * @throws IOException
	 */
	public boolean testeConexao(){
		boolean isConexao= false;
		int timeout = 300;  
		InetAddress endereco;

		try {
			//seta endere�o que servir� de teste de conectividade
			endereco = InetAddress.getByName("www.google.com.br");

			//caso teste n�o estoure o tempo estipulado para resposta
			//significa que existe conex�o
			if (!endereco.isReachable(timeout)){
				isConexao = true;
			} 
		}catch (Exception ex) {
			this.erroMaps = erroConexao;
		}
		return isConexao;
	} 


	public Rota carregaRota(){

		try{

			//caso atributo esteja nulo, tenta recuperar
			//informa��es do banco
			if(this.rotaMaps == null)
				//pesquisa por rotas cadastradas
				this.rotaMaps = this.viajante.selecionarRota(this.viajante.getUsuario().getCodigo());

		} catch(Exception e){
			e.printStackTrace();
		}

		return this.configuraRota();
	}

	/**
	 * M�todo respons�vel por adequar informa��es para
	 * visualiza��o
	 * @return
	 */
	private Rota configuraRota(){

		//caso exista rota v�lida...		
		if(this.rotaMaps != null){

			//seta ceps de origem, destino, data e nome em atributos do objeto
			//para exibi��o
			//verifica��o de null adicionada pois foi identificado que atributo
			//pode ser anulado ap�s uma atualiza��o de cadastro
			if(this.rotaMaps.getAtividadeOrigem().getCodigoAtividade() > 0){

				this.origem = this.rotaMaps.getAtividadeOrigem().getEndereco().getCep();
				this.nomeOrigem = this.rotaMaps.getAtividadeOrigem().getNomeAtividade();

			} else {

				this.origem 	= this.viajante.getEndereco().getCep();
				this.nomeOrigem = "Endere�o viajante";
			}			

			this.destino = this.rotaMaps.getAtividadeDestino().getEndereco().getCep();

			this.nomeDestino = this.rotaMaps.getAtividadeDestino().getNomeAtividade();

			this.dataRota = this.rotaMaps.getDataCadastroRota().dataCompleta(true);

		} else {

			//caso n�o existam rotas, seta mensagem padr�o
			//mapa ser� carregado com informa��es originais setadas 
			//nos atributos origem e destino
			this.erroMaps = semRotasPesquisadas;			
		}

		return rotaMaps;
	}

	/**
	 * m�todo respons�vel por persistir informa��es sobre a rota
	 * selecionada pelo viajante
	 * caso n�o exista atividade origem seta zero para esse campo
	 * 
	 * @throws Exception
	 */
	public void salvarRota() throws Exception{

		//extrai c�digos das atividades encontradas na ses�o
		Integer codigoOrigem =   this.rotaMaps.getAtividadeOrigem().getCodigoAtividade();
		Integer codigoDestino =  this.rotaMaps.getAtividadeDestino().getCodigoAtividade();
		Integer codigoViajante = this.viajante.getUsuario().getCodigo();

		//caso n�o exista c�digo de origem seta zero
		//com isso ser� carregado posteriormente o endere�o do viajante
		if(codigoOrigem == null){
			codigoOrigem = 0;
		}

		//persiste c�digos da rota selecionados (caso endere�o viajante seja origem, seta zero) 
		this.viajante.salvarRota(codigoViajante, codigoOrigem, codigoDestino);		
	}




	public String getErroMaps() {
		return erroMaps;
	}

	public void setViajante(Viajante viajante) {
		this.viajante = viajante;
	}


	public Rota getRotaMaps() {
		return rotaMaps;
	}

	public String getOrigem() {
		return origem;
	}

	public String getDestino() {
		return destino;
	}

	public String getDataRota() {
		return dataRota;
	}

	public String getNomeOrigem() {
		return nomeOrigem;
	}

	public String getNomeDestino() {
		return nomeDestino;
	}

	public void setRotaMaps(Rota rotaMaps) {
		this.rotaMaps = rotaMaps;
	}
}
