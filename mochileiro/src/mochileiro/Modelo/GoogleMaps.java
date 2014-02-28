package mochileiro.Modelo;

import java.io.IOException;
import java.net.InetAddress;


/**
 * @author $h@rk
 * @projeto mochileiro
 * @Data 29/03/2010 @Hora 20:15:48
 * @Descrição: classe responsável por controlar informações referente as rotas do
 * 				GoogleMaps
 */
public class GoogleMaps {

	//objeto utilizado para resgatar a rota
	private Viajante viajante;

	//String utilizada como retorno de erros
	private String   erroMaps;

	//carrega objetos representando atividade origem e destino
	private Rota rotaMaps;

	//atributos utilizados na visualização da rota
	private String origem = "Av. Paulista - São Paulo";

	private String destino = "Praça da Sé, São Paulo";

	private String dataRota = "01/07/2006";

	private String nomeOrigem ="";

	private String nomeDestino ="";

	public static String ROTA_SELECIONADA = "objMaps";

	private  final String semRotasPesquisadas = " Não existem rotas pesquisada recentemente. " +
												" Para visualizar uma rota, selecione duas atividades " +
												" em sua lista de favoritos ou selecione o seu endereço " +
												" e uma atividade destino  e acesse  a opção 'Gerar Rota!'";

	private final String erroConexao = 			" Não foi possível estabelecer conexão com o serviço de Mapas. " +
												" Essa falha pode ocorrer caso não exista conectividade com o " +
												" serviço de rotas utilizado pela aplicação. " +
												" Tente novamente em alguns instantes";

	public GoogleMaps(Viajante viajante){

		this.viajante = viajante;	
	}



	/**
	 * Método responsável por verificar se existe conectividade 
	 * entre a aplicação e o serviço de mapas
	 * 
	 * @return
	 * @throws IOException
	 */
	public boolean testeConexao(){
		boolean isConexao= false;
		int timeout = 300;  
		InetAddress endereco;

		try {
			//seta endereço que servirá de teste de conectividade
			endereco = InetAddress.getByName("www.google.com.br");

			//caso teste não estoure o tempo estipulado para resposta
			//significa que existe conexão
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
			//informações do banco
			if(this.rotaMaps == null)
				//pesquisa por rotas cadastradas
				this.rotaMaps = this.viajante.selecionarRota(this.viajante.getUsuario().getCodigo());

		} catch(Exception e){
			e.printStackTrace();
		}

		return this.configuraRota();
	}

	/**
	 * Método responsável por adequar informações para
	 * visualização
	 * @return
	 */
	private Rota configuraRota(){

		//caso exista rota válida...		
		if(this.rotaMaps != null){

			//seta ceps de origem, destino, data e nome em atributos do objeto
			//para exibição
			//verificação de null adicionada pois foi identificado que atributo
			//pode ser anulado após uma atualização de cadastro
			if(this.rotaMaps.getAtividadeOrigem().getCodigoAtividade() > 0){

				this.origem = this.rotaMaps.getAtividadeOrigem().getEndereco().getCep();
				this.nomeOrigem = this.rotaMaps.getAtividadeOrigem().getNomeAtividade();

			} else {

				this.origem 	= this.viajante.getEndereco().getCep();
				this.nomeOrigem = "Endereço viajante";
			}			

			this.destino = this.rotaMaps.getAtividadeDestino().getEndereco().getCep();

			this.nomeDestino = this.rotaMaps.getAtividadeDestino().getNomeAtividade();

			this.dataRota = this.rotaMaps.getDataCadastroRota().dataCompleta(true);

		} else {

			//caso não existam rotas, seta mensagem padrão
			//mapa será carregado com informações originais setadas 
			//nos atributos origem e destino
			this.erroMaps = semRotasPesquisadas;			
		}

		return rotaMaps;
	}

	/**
	 * método responsável por persistir informações sobre a rota
	 * selecionada pelo viajante
	 * caso não exista atividade origem seta zero para esse campo
	 * 
	 * @throws Exception
	 */
	public void salvarRota() throws Exception{

		//extrai códigos das atividades encontradas na sesão
		Integer codigoOrigem =   this.rotaMaps.getAtividadeOrigem().getCodigoAtividade();
		Integer codigoDestino =  this.rotaMaps.getAtividadeDestino().getCodigoAtividade();
		Integer codigoViajante = this.viajante.getUsuario().getCodigo();

		//caso não exista código de origem seta zero
		//com isso será carregado posteriormente o endereço do viajante
		if(codigoOrigem == null){
			codigoOrigem = 0;
		}

		//persiste códigos da rota selecionados (caso endereço viajante seja origem, seta zero) 
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
