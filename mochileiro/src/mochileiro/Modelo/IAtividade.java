package mochileiro.Modelo;

import java.util.ArrayList;

import mochileiro.Util.Data;

public interface IAtividade {

	//getters e setters
	public abstract Integer getCodigoAtividade();
	
	public abstract void setCodigoAtividade(Integer codigoAtividade);
	
	public abstract String getNomeAtividade();

	public void setNomeLista(String nomeLista);
	
	public String getNomeLista();

	public void setDescricaoLista(String descricaoLista);
	
	public String getDescricaoLista();
	
	public abstract Categoria getCategoria();

	public abstract Data getDataCadastro();
	
	public void setComentarios(String comentarios);
	
	public String getComentarios();
	
	public ArrayList<Classificacao> getListaComentarios();
	
	public void selecionaComentarios(Integer codigoAtividade) throws Exception;

	public abstract String getDescricao();

}