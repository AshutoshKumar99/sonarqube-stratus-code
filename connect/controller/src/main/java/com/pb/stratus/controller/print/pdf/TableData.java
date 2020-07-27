package com.pb.stratus.controller.print.pdf;

/**
 * An abstract representation of tabular (2-dimensional) data. Cells are 
 * located by row and column indexes. The indexes are 0-based, as it is common 
 * on the Java platform.  
 */
public interface TableData
{
    
    /**
     * @return the cell data at the given row and column.
     */
    public Object getCellData(int row, int col);
    
    
    /**
     * @return the overall number of rows of the underlying tabular data set
     */
    public int getNumberOfRows();
    
    /**
     * @return the overall number of columns of the underlying tabular data set
     */
    public int getNumberOfColumns();

}
