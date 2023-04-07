/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dyno.scheduler.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Prabash
 */
public class ExcelReader
{
    // <editor-fold desc="Properties" >
    
    private final String XLSX_FILE_PATH;
    
    // </editor-fold>
    
    // <editor-fold desc="Read Excel sheet methods">
    
    public ExcelReader(String filePath)
    {
        XLSX_FILE_PATH = filePath;
    }

    /**
     * this method is used to read an excel file by giving the respective sheetName
     * @param sheetName sheetName that should be read
     * @return Sheet object with the read values
     * @throws IOException
     * @throws InvalidFormatException 
     */
    public Sheet readExcelSheet(String sheetName) throws IOException, InvalidFormatException
    {
        // Creating a Workbook from an Excel file (.xls or .xlsx)
        FileInputStream excelFile = new FileInputStream(new File(XLSX_FILE_PATH));
        Workbook workbook = new XSSFWorkbook(excelFile);
            
        //Workbook workbook = WorkbookFactory.create(new File(xlsxFilePath));
        // Retrieving the number of sheets in the Workbook
        System.out.println("Workbook has " + workbook.getNumberOfSheets() + " Sheets : ");

        workbook.forEach(sheet ->
        {
            System.out.println("=> " + sheet.getSheetName());
        });

        // Getting the Sheet at index zero
        Sheet sheet = workbook.getSheet(sheetName);

        // Closing the workbook
        workbook.close();
        
        return sheet;
    }
    
    /**
     * this method is used to print a read excel sheet to the console
     * @param sheet Sheet object that should be printed
     */
    public void printExcelSheet(Sheet sheet)
    {
        // Create a DataFormatter to format and get each cell's value as String
        DataFormatter dataFormatter = new DataFormatter();
        
        sheet.forEach(row ->
        {
            if (row.getRowNum() == 0)
                return;
            row.forEach(cell ->
            {
                String cellValue = dataFormatter.formatCellValue(cell);
                System.out.print(cellValue + "\t");
            });
            
            System.out.println();
        });
    }
    
    // </editor-fold>
}
