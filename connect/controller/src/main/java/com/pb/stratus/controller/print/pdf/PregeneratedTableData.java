package com.pb.stratus.controller.print.pdf;

import com.pb.stratus.core.util.ObjectUtils;
import uk.co.graphdata.utilities.contract.Contract;

public class PregeneratedTableData implements TableData
{
    Object[][] data;
    
    public PregeneratedTableData(int rows, int cols)
    {
        Contract.pre(rows > 0, "Number of rows must be positive");
        Contract.pre(cols > 0, "Number of columns must be positive");
        data = new Object[rows][cols];
    }

    public Object getCellData(int row, int col)
    {
        validateIndexes(row, col);
        return data[row][col];
    }

    public int getNumberOfColumns()
    {
        return data[0].length;
    }

    public int getNumberOfRows()
    {
        return data.length;
    }
    
    /**
     * Sets the data of a given cell
     * 
     * @param row the row of the cell. Must be between 0 incl. and 
     *        {@link #getNumberOfRows()} 
     * @param col the col of the cell. Must be between 0 incl. and 
     *        {@link #getNumberOfColumns()()} 
     * @param cellData the cell data. Any previously assigned table data at the
     *        same location will be discarded. To unset the value of a cell
     *        use <code>null</code>.
     */
    public void setCellData(int row, int col, Object cellData)
    {
        validateIndexes(row, col);
        this.data[row][col] = cellData;
    }
    
    private void validateIndexes(int row, int col)
    {
        Contract.pre(row >= 0 && row < getNumberOfRows(), 
                "Row index out of bounds");
        Contract.pre(col >= 0 && col < getNumberOfColumns(), 
                "Column index out of bounds");
    }

    @Override
    public boolean equals(Object obj)
    {
        PregeneratedTableData that = ObjectUtils.castToOrReturnNull(
                PregeneratedTableData.class, obj);
        if (that == null)
        {
            return false;
        }
        if (this.data.length != that.data.length)
        {
            return false;
        }
        for (int i = 0; i < data.length; i++)
        {
            if (!ObjectUtils.equals(this.data[i], that.data[i]))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        // TODO Auto-generated method stub
        return super.hashCode();
    }
    
    

}
