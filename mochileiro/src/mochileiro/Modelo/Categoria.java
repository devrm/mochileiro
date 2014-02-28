package mochileiro.Modelo;

import java.util.HashMap;

import mochileiro.Modelo.DAO.CategoriaDAO;

/**
 * @author Thiago
 * @projeto mochileiro
 * @Data 15/11/2009 @Hora 18:38:36
 * @Descrição:
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
	 * Construtor utilizado na geração do relatório
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
	 * Método responsável por requisitar informações da entidade categoria ao DAO
	 * @return list<Categoria>
	 * @throws Exception
	 */

	public HashMap<Integer,Categoria> exibeCategorias() throws Exception{		
		
		//Invoca método de busca de dados
		//caso ocorra algum erro lança uma exceção
		//que será tratado no Menaged Bean		
	return CategoriaDAO.selecionar();	
	}
	
	/**
	 * Método responsável por invocar DAO retornando registros utilizados
	 * no relatório de categorias
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
