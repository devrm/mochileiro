package mochileiro.Util;

/**
 * @Autor rams
 * @Projeto mochileiro
 * @Data 12/06/2009 @Hora 14:33:02
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Conexao{

    private static Connection con = null;

  /**
   * Retorna a conexão que será usada para efetuar operações com o BD
   * @return
   */
  public static Connection getConexao() throws SQLException, ClassNotFoundException{
    Class.forName("org.postgresql.Driver");
    con = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/mochileirodb","postgres","postgres");
    return con;
  }

  /**
   * Finaliza o resultset e a sessão com o BD para evitar possíveis redíduos nos componentes
   * @param pStmt Statement a ser fechado
   * @param rs    ResultSet a ser fechado
   */
  public static void finalizaOperacaoBD(PreparedStatement pStmt, ResultSet rs) throws SQLException
  {
	if(pStmt !=null)
		pStmt.close();
  
    if(rs != null)
    	rs.close();
    
    if(con != null)
     con.close();    
}
  
  public static void finalizaOperacaoBD(PreparedStatement pStmt) throws SQLException
  {
		if(pStmt !=null)
			pStmt.close();
		
		if(con != null)
		 con.close();  	
 }
}
