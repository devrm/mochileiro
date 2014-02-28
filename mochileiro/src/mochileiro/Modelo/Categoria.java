package mochileiro.Modelo;

import java.util.HashMap;

import mochileiro.Modelo.DAO.CategoriaDAO;

/**
 * @author Thiago
 * @projeto mochileiro
 * @Data 15/11/2009 @Hora 18:38:36
 * @Descri��o:
 */
public class Categoria{
	 private int codigo;
	 private String nome;
	 private String descricao;
	 private String tipo;
	 private Integer adicao;
	  
	 
	public Categoria() {} 
	
	/**
	 * Construtor sobrecarregado
	 * @param codigo
	 * @param categoria
	 * @param descricao
	 */
	public Categoria(int codigo, String categoria, String descricao, String tipo) {

		this.codigo = codigo;
		this.nome = categoria;
		this.descricao = descricao;
		this.tipo = tipo;		
	}	
	
	/**
	 * Construtor utilizado na gera��o do relat�rio
	 * @param codigo
	 * @param categoria
	 * @param descricao
	 * @param tipo
	 * @param adicao
	 */
	public Categoria(int codigo, String categoria, String descricao, String tipo, Integer adicao) {

		this.codigo = codigo;
		this.nome = categoria;
		this.descricao = descricao;
		this.tipo = tipo;
		this.adicao = adicao;
	}	

	
	/**
	 * M�todo respons�vel por requisitar informa��es da entidade categoria ao DAO
	 * @return list<Categoria>
	 * @throws Exception
	 */

	public HashMap<Integer,Categoria> exibeCategorias() throws Exception{		
		
		//Invoca m�todo de busca de dados
		//caso ocorra algum erro lan�a uma exce��o
		//que ser� tratado no Menaged Bean		
	return CategoriaDAO.selecionar();	
	}
	
	/**
	 * M�todo respons�vel por invocar DAO retornando registros utilizados
	 * no relat�rio de categorias
	 * 
	 * @return
	 * @throws Exception
	 */
	public HashMap<Integer,Categoria> selecionaCatRelatorio() throws Exception{
		return CategoriaDAO.selecionarRelatorio();	
	}

	
	
	public int getCodigo() {
		return codigo;
	}


	public String getDescricao() {
		return descricao;
	}

	public String getTipo() {
		return tipo;
	}

	public String getNome() {
		return nome;
	}

	public void setCodigo(int codigo){
		this.codigo = codigo;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getAdicao() {
		return adicao;
	}
}
