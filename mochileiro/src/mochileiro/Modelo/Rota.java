package mochileiro.Modelo;

import mochileiro.Util.Data;

/**
 * @author $h@rk
 * @projeto mochileiro
 * @Data 21/05/2010 @Hora 23:44:58
 * @Descrição: Classe utilizada para agrupar atividades cadastradas como rotas
 */
public class Rota {
	private Integer codigoRota;
	private Integer codigoOrigem;
	private Integer codigoDestino;
	private Data    dataCadastroRota;
	
	private Atividade atividadeOrigem;
	private Atividade atividadeDestino;
	
	public final int ORIGEM = 1;
	public final int DESTINO = 2;
	
	public Rota() {
		this.dataCadastroRota = new Data();
		this.atividadeOrigem = new Atividade();
		this.atividadeDestino =  new Atividade();
	}
	
	//getters e setters
	public Integer getCodigoOrigem() {
		return codigoOrigem;
	}
	
	public void setCodigoOrigem(Integer codigoOrigem) {
		this.codigoOrigem = codigoOrigem;
	}
	
	public Integer getCodigoDestino() {
		return codigoDestino;
	}
	
	public void setCodigoDestino(Integer codigoDestino) {
		this.codigoDestino = codigoDestino;
	}
	
	public Data getDataCadastroRota() {
		return dataCadastroRota;
	}
	
	public void setDataCadastroRota(Data dataCadastroRota) {
		this.dataCadastroRota = dataCadastroRota;
	}

	public Atividade getAtividadeOrigem() {
		return atividadeOrigem;
	}

	public void setAtividadeOrigem(Atividade atividadeOrigem) {
		this.atividadeOrigem = atividadeOrigem;
	}

	public Atividade getAtividadeDestino() {
		return atividadeDestino;
	}

	public void setAtividadeDestino(Atividade atividadeDestino) {
		this.atividadeDestino = atividadeDestino;
	}

	public Integer getCodigoRota() {
		return codigoRota;
	}

	public void setCodigoRota(Integer codigoRota) {
		this.codigoRota = codigoRota;
	}
	
	
}
