package com.pb.stratus.controller.print.pdf;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PregeneratedTableDataTest
{
    
    private PregeneratedTableData tableData;
    
    @Before
    public void setUp()
    {
        tableData = new PregeneratedTableData(3, 4);
    }
    
    @Test
    public void shouldReturnDimensionsGivenAtConstruction()
    {
        TableData td = new PregeneratedTableData(11, 23);
        assertEquals(11, td.getNumberOfRows());
        assertEquals(23, td.getNumberOfColumns());
    }
    
    @Test
    public void shouldNotAllowOneDimensionalTableData()
    {
        try
        {
            new PregeneratedTableData(0, 1);
            fail("No IllegalArgumentException thrown");
        }
        catch (IllegalArgumentException x)
        {
            //expected
        }
        try
        {
            new PregeneratedTableData(1, 0);
            fail("No IllegalArgumentException thrown");
        }
        catch (IllegalArgumentException x)
        {
            //expected
        }
    }

    @Test
    public void getCellDataShouldReturnPreviouslySetValue()
    {
        int row = 2;
        int col = 3;
        Object expected = new Object();
        Object actual = tableData.getCellData(row, col);
        assertNull(actual);
        tableData.setCellData(row, col, expected);
        assertEquals(expected, tableData.getCellData(row, col));
    }
    
    @Test
    public void shouldNotAllowSettingCellDataOutOfBounds()
    {
        try
        {
            tableData.setCellData(3, 1, new Object());
            fail("No IllegalArgumentException thrown");
        }
        catch (IllegalArgumentException x)
        {
            //expected
        }
        try
        {
            tableData.setCellData(1, 4, new Object());
            fail("No IllegalArgumentException thrown");
        }
        catch (IllegalArgumentException x)
        {
            //expected
        }
        try
        {
            tableData.setCellData(-1, 1, new Object());
            fail("No IllegalArgumentException thrown");
        }
        catch (IllegalArgumentException x)
        {
            //expected
        }
        try
        {
            tableData.setCellData(1, -1, new Object());
            fail("No IllegalArgumentException thrown");
        }
        catch (IllegalArgumentException x)
        {
            //expected
        }
    }
    
    @Test
    public void shouldNotAllowGettingCellDataOutOfBounds()
    {
        try
        {
            tableData.getCellData(3, 1);
            fail("No IllegalArgumentException thrown");
        }
        catch (IllegalArgumentException x)
        {
            //expected
        }
        try
        {
            tableData.getCellData(1, 4);
            fail("No IllegalArgumentException thrown");
        }
        catch (IllegalArgumentException x)
        {
            //expected
        }
        try
        {
            tableData.getCellData(-1, 1);
            fail("No IllegalArgumentException thrown");
        }
        catch (IllegalArgumentException x)
        {
            //expected
        }
        try
        {
            tableData.getCellData(1, -1);
            fail("No IllegalArgumentException thrown");
        }
        catch (IllegalArgumentException x)
        {
            //expected
        }
    }

}
