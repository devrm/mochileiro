package mochileiro.Modelo;

import mochileiro.Modelo.DAO.UsuarioDAO;
import mochileiro.Util.SessaoJSF;
/**
 * @author $h@rk
 * @projeto mochileiro
 * @Data 15/03/2010 @Hora 21:51:17
 * @Descrição: Classe responsável pelas ações da entidade usuário
 */
public class Usuario {
	
	private Integer codigo;
	private String 	tipo;
	private String 	nickName;
	private String 	senha;

	//Constantes utilizadas na validacao do usuario e senha
	public static int SENHA_INVALIDA = 1;
	public static int USUARIO_INVALIDO = 2;	
	
	//Constantes utilizadas para definir o tipo de usuario
	public static String TIPO_USUARIO_EMPRESA  = "EMPRESA";
	public static String TIPO_USUARIO_VIAJANTE = "VIAJANTE";

	public Usuario(){}
	
	
	public Usuario(Integer codigo, String tipo, String nickName, String senha) {
		super();
		this.codigo = codigo;
		this.tipo = tipo;
		this.nickName = nickName;
		this.senha = senha;
	}





	/**
	 * Método responsável por realizar login do usuário validando se o mesmo e a senha estao corretos
	 */
	public int autenticar(String nickName, String senha) throws Exception{
		
		//Inicializa-se sem erros de autenticacao
		int codigoErro = 0;

		//Verifica se existe o usuario com o nickname passado e o compara
		Usuario usuarioTemp = UsuarioDAO.selecionar(null, nickName);
		
		if(usuarioTemp == null)
			codigoErro = Usuario.USUARIO_INVALIDO;
		
		//Caso seja valido, compara se a senha está correta
		else if(!usuarioTemp.getSenha().equalsIgnoreCase(senha))
			codigoErro = Usuario.SENHA_INVALIDA;
		
		//Caso não existam erros na validação
		if(codigoErro == 0){
			
			this.setCodigo(usuarioTemp.getCodigo());
			this.setNickName(usuarioTemp.getNickName());
			this.setSenha(usuarioTemp.getSenha());
			this.setTipo(usuarioTemp.getTipo());

			//Armazena o usuario logado na sessao
			SessaoJSF.setParametroSessao(usuarioTemp, "usuarioLogado");
		}
		return codigoErro;		
	}
	
	public boolean validarNickNameDuplicado(Integer codigo, String nickName) throws Exception {
		boolean isDuplicado = false;
		Usuario usuario = selecionarPorNickName(nickName);
		if (usuario != null) {
			if (codigo != usuario.getCodigo()) {
				if (usuario.getNickName().equals(nickName))
					isDuplicado = true;
			}
		}
		else {
			isDuplicado = (usuario != null ? true : false);
		}
		
		return isDuplicado;
	}
		
	public Usuario selecionarPorNickName(String nickName) throws Exception {
		return UsuarioDAO.selecionar(null, nickName);
	}
	
	/**
	 * Seleciona o usuario passando o codigo do usuario (id) desejado
	 */
	public Usuario selecionarPorCodigo(int codigo) throws Exception {
		return UsuarioDAO.selecionar(new Integer(codigo), null);
	}

	
	public Integer getCodigo() {
		return codigo;
	}
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}

}
