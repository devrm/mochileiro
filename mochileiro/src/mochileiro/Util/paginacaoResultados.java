package mochileiro.Util;

import javax.faces.component.html.HtmlDataTable;

/**
 * 
 * @author Thiago
 * @projeto mochileiro
 * @Data 25/01/2010 @Hora 14:15:40
 * @Descri��o: Classe utilit�ria respons�vel por realizar a pagina��o dos resultados buscados no banco de dados
 */
public class paginacaoResultados {

    private HtmlDataTable dataTable = null;
    private int maxPorPagina = 0;

    //m�todos de a��o respons�veis pela nevega��o pelos registros
	
    public void primeiraPagina() {
        dataTable.setFirst(0);
    }

    public void paginaAnterior() {
        dataTable.setFirst(dataTable.getFirst() - dataTable.getRows());
    }

    public void proximaPagina() {
        dataTable.setFirst(dataTable.getFirst() + dataTable.getRows());
    }

    public void ultimaPagina() {
        int count = dataTable.getRowCount();
        int linhas = dataTable.getRows();
        dataTable.setFirst(count - ((count % linhas != 0) ? count % linhas : linhas));
    }
    
    public int getPaginaAtual() {
        int linha = dataTable.getRows();
        int primeira = dataTable.getFirst();
        int count = dataTable.getRowCount();
        return (count / linha) - ((count - primeira) / linha) + 1;
    }

    public int getTotalPaginas() {
        int linha = dataTable.getRows();
        int count = dataTable.getRowCount();
        return (count / linha) + ((count % linha != 0) ? 1 : 0);
    }


    
    /**
     * M�todo utilit�rio respons�vel por definir quantidade de registros paginados e n�mero
     * de linhas por p�gina
     * 
     * @param quantidadeRegistros
     * @param linhasPorPagina
     */
	public void setMaxPorPagina(int quantidadeRegistros){
		this.maxPorPagina = quantidadeRegistros;
	}

	public int getMaxPorPagina() {
		return maxPorPagina;
	}

	public HtmlDataTable getDataTable() {
		return dataTable;
	}

	public void setDataTable(HtmlDataTable dataTable) {
		this.dataTable = dataTable;
	}
}
