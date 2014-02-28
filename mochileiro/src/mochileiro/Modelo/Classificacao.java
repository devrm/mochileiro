package mochileiro.Modelo;

import java.util.ArrayList;

import mochileiro.Modelo.DAO.ClassificacaoDAO;
import mochileiro.Util.Data;

/**
 * @author Thiago
 * @projeto mochileiro
 * @Data 06/02/2010 @Hora 10:02:55
 * @Descrição: Classe responsável pela classificação das atividades
 */
public class Classificacao {

	private int codigo;
	private int codigoAtividade;
	private int codigoViajante;
	private String nickViajante;
	private boolean nota = true;
	private String comentarioViajante;
	private String comentarioLista;
	
	private Data dataComentario;
	
	public  final String NOTA_POSITIVA = "Boa";
	public  final String NOTA_NEGATIVA = "Ruim";
	
	private String notaExtenso;


	public Classificacao(){
		this.dataComentario = new Data();
	}
	
	public void insereClassificacao(Atividade atividade, Usuario usuario)	throws Exception {

		//populo objeto classificação 
		this.codigoAtividade = atividade.getCodigoAtividade();
		this.codigoViajante = usuario.getCodigo();
		//envia objeto classificação populado para o DAO
		ClassificacaoDAO.insereClassificao(this);		
	}

	public ArrayList<Classificacao> selecionaClassificacao(int codigoAtividade) throws Exception{

		//retorna Coleção com objetos classificação
		return ClassificacaoDAO.selecionaClassificao(codigoAtividade);
	}	

	// getters e setters
	public int getCodClassificacao() {
		return codigo;
	}
	public void setCodClassificacao(int codClassificacao) {
		this.codigo = codClassificacao;
	}
	public int getCodAtividade() {
		return codigoAtividade;
	}
	public void setCodAtividade(int codAtividade) {
		this.codigoAtividade = codAtividade;
	}
	public int getCodViajante() {
		return codigoViajante;
	}
	public void setCodViajante(int codViajante) {
		this.codigoViajante = codViajante;
	}
	public boolean getNota() {
		return nota;
	}
	public void setNota(boolean nota) {
		this.nota = nota;
	}
	public String getComentarioViajante() {
		return comentarioViajante;
	}
	public void setComentarioViajante(String comentarioViajante) {
		this.comentarioViajante = comentarioViajante;
	}

	public String getNotaExtenso() {
		return notaExtenso;
	}

	public void setNotaExtenso(String notaExtenso) {
		this.notaExtenso = notaExtenso;
	}

	public String getNickViajante() {
		return nickViajante;
	}

	public void setNickViajante(String nickViajante) {
		this.nickViajante = nickViajante;
	}

	public Data getDataComentario() {
		return dataComentario;
	}

	public String getComentarioLista() {
		return comentarioLista;
	}

	public void setComentarioLista(String comentarioLista) {
		this.comentarioLista = comentarioLista;
	}



}
