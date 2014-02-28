package mochileiro.Modelo;

/**
 * @author $h@rk
 * @projeto mochileiro2
 * @Data 15/03/2010 @Hora 21:51:54
 * @Descrição:
 */
public class Endereco {
	
	private String logradouro;
	private String numero;
	private String complemento;
	private String cep;
	private int    uf;
	private String bairro;
	private String cidade;
	private String telefone;	
	
	public String getLogradouro() {
		return logradouro;
	}
	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getComplemento() {
		return complemento;
	}
	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}
	public String getCep() {
		return cep;
	}
	public void setCep(String cep) {
		this.cep = cep;
	}
	public int getUf() {
		return uf;
	}
	public void setUf(int uf) {
		this.uf = uf;
	}
	public String getBairro() {
		return bairro;
	}
	public void setBairro(String bairro) {
		this.bairro = bairro;
	}
	public String getCidade() {
		return cidade;
	}
	public void setCidade(String cidade) {
		this.cidade = cidade;
	}
	public String getTelefone() {
		return telefone;
	}
	//concatenação de strings (DDD + Tel)
	public void setTelefone(String telefone) {
		this.telefone += telefone;
	}
}
