/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dyno.scheduler.data;

import dyno.scheduler.datamodels.DataModel;
import dyno.scheduler.datamodels.DataModelEnums;
import dyno.scheduler.datamodels.PartModel;
import dyno.scheduler.datamodels.PartUnavailabilityModel;
import dyno.scheduler.datamodels.ShopOrderModel;
import dyno.scheduler.datamodels.ShopOrderOperationModel;
import dyno.scheduler.datamodels.WorkCenterInterruptionsModel;
import dyno.scheduler.datamodels.WorkCenterModel;
import dyno.scheduler.datamodels.WorkCenterOpAllocModel;
import dyno.scheduler.utils.DateTimeUtil;
import dyno.scheduler.utils.ExcelUtil;
import dyno.scheduler.utils.GeneralSettings;
import dyno.scheduler.utils.LogUtil;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author Prabash
 */
public class ExcelWriteManager extends DataWriteManager
{

    private String xlsxFilePath;
    private FileInputStream inputStream;

    @Override
    public boolean addData(List<? extends DataModel> dataList, DataModelEnums.DataModelType dataModelType)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean updateData(List<? extends DataModel> dataList, DataModelEnums.DataModelType dataModelType)
    {
        try
        {
            xlsxFilePath = GeneralSettings.getDefaultExcelFile();
            String excelSheetName = ExcelUtil.getStorageName(dataModelType);
            switch (dataModelType)
            {
                case ShopOrder:
                {
                    return updateShopOrderData((List<ShopOrderModel>) dataList, excelSheetName);
                }
                case ShopOrderOperation:
                {
                    return updateShopOrderOperationData((List<ShopOrderOperationModel>) dataList, excelSheetName);
                }
                case WorkCenter:
                {
                    return updateWorkCenterData((List<WorkCenterModel>) dataList, excelSheetName);
                }
                case WorkCenterAllocationFinite:
                {
                    return updateWorkCenterOpAllocData((List<WorkCenterOpAllocModel>) dataList, excelSheetName);
                }
                default:
                {
                    return false;
                }

            }
        } catch (Exception ex)
        {
            LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public boolean addShopOrderData(List<ShopOrderModel> dataList, String storageName)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addShopOrderOperationData(List<ShopOrderOperationModel> dataList, String storageName)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addWorkCenterData(List<WorkCenterModel> dataList, String storageName)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addWorkCenterOpALlocData(List<WorkCenterOpAllocModel> workCenterOpAllocs, String storageName)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean updateShopOrderData(List<ShopOrderModel> dataList, String storageName)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean updateShopOrderOperationData(List<ShopOrderOperationModel> shopOrderOperations, String storageName)
    {
        try
        {
            // Obtain a workbook from the excel file
            inputStream = new FileInputStream(xlsxFilePath);
            // Create a DataFormatter to format and get each cell's value as String
            int totalColumnCount = 0;
            try (Workbook workbook = WorkbookFactory.create(inputStream))
            {
                // Create a DataFormatter to format and get each cell's value as String
                DataFormatter dataFormatter = new DataFormatter();
                DateTimeFormatter dateFormat = DateTimeUtil.getDateFormat();
                DateTimeFormatter timeFormat = DateTimeUtil.getTimeFormat();
                
                // Get Sheet at index 0
                Sheet sheet = workbook.getSheet(storageName);

                for (ShopOrderOperationModel shopOrderOperation : shopOrderOperations)
                {
                    for (Row row : sheet)
                    {
                        //skip the header row of the excel
                        if (row.getRowNum() == 0)
                        {
                            Iterator<Cell> iterator = row.cellIterator();
                            while (iterator.hasNext())
                            {
                                iterator.next();
                                totalColumnCount++;
                            }
                            continue;
                        }
                        
                        if (dataFormatter.formatCellValue(row.getCell(1)).equals(Integer.toString(shopOrderOperation.getOperationId())))
                        {
                            for (int i = 0; i < totalColumnCount; i++)
                            {
                                Cell cell = row.getCell(i);
                                if (cell == null)
                                {
                                    cell = row.createCell(i);
                                }
                                
                                // Update the cell's value
                                switch(i)
                                {
                                    case 0: cell.setCellValue(shopOrderOperation.getOrderNo()); break;
                                    case 1: cell.setCellValue(shopOrderOperation.getOperationId()); break;
                                    case 2: cell.setCellValue(shopOrderOperation.getOperationNo()); break;
                                    case 3: cell.setCellValue(shopOrderOperation.getOperationDescription()); break;
                                    case 4: cell.setCellValue(shopOrderOperation.getOperationSequence()); break;
                                    case 5: cell.setCellValue(shopOrderOperation.getPrecedingOperationId()); break;
                                    case 6: cell.setCellValue(shopOrderOperation.getWorkCenterRuntimeFactor()); break;
                                    case 7: cell.setCellValue(shopOrderOperation.getWorkCenterRuntime()); break;
                                    case 8: cell.setCellValue(shopOrderOperation.getLaborRuntimeFactor()); break;
                                    case 9: cell.setCellValue(shopOrderOperation.getLaborRunTime()); break;
                                    case 10: cell.setCellValue(shopOrderOperation.getOpStartDate().toString(dateFormat)); break;
                                    case 11: cell.setCellValue(shopOrderOperation.getOpStartTime().toString(timeFormat)); break;
                                    case 12: cell.setCellValue(shopOrderOperation.getOpFinishDate().toString(dateFormat)); break;
                                    case 13: cell.setCellValue(shopOrderOperation.getOpFinishTime().toString(timeFormat)); break;
                                    case 14: cell.setCellValue(shopOrderOperation.getQuantity()); break;
                                    case 15: cell.setCellValue(shopOrderOperation.getWorkCenterType()); break;
                                    case 16: cell.setCellValue(shopOrderOperation.getWorkCenterNo()); break;
                                    case 17: cell.setCellValue(shopOrderOperation.getOperationStatus().toString()); break;
                                    default: break;
                                }
                            }
                        }
                    }
                }

                inputStream.close();
                // Write the output to the file
                try (FileOutputStream fileOut = new FileOutputStream(xlsxFilePath))
                {
                    workbook.write(fileOut);
                    // Closing the workbook
                }
            }

        } catch (FileNotFoundException ex)
        {
            LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
            return false;
        } catch (IOException | InvalidFormatException | EncryptedDocumentException ex)
        {
            LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
            return false;
        } finally
        {
            try
            {
                inputStream.close();
            } catch (IOException ex)
            {
                LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
                return false;
            }
        }

        return true;
    }
    

    @Override
    public boolean updateWorkCenterData(List<WorkCenterModel> dataList, String storageName)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean updateWorkCenterOpAllocData(List<WorkCenterOpAllocModel> workCenterOpAllocs, String storageName)
    {
        try
        {
            // Obtain a workbook from the excel file
            inputStream = new FileInputStream(xlsxFilePath);
            // Create a DataFormatter to format and get each cell's value as String
            try (Workbook workbook = WorkbookFactory.create(inputStream))
            {
                // Create a DataFormatter to format and get each cell's value as String
                DataFormatter dataFormatter = new DataFormatter();
                DateTimeFormatter dateFormat = DateTimeUtil.getDateFormat();
                // Get Sheet at index 0
                Sheet sheet = workbook.getSheet(storageName);

                workCenterOpAllocs.forEach((workCenterOpAlloc) ->
                {
                    for (Row row : sheet)
                    {
                        //skip the header row of the excel
                        if (row.getRowNum() == 0)
                        {
                            continue;
                        }
                        
                        if (dataFormatter.formatCellValue(row.getCell(0)).equals(workCenterOpAlloc.getWorkCenterNo())
                                && dateFormat.parseDateTime(dataFormatter.formatCellValue(row.getCell(1))).equals(workCenterOpAlloc.getOperationDate()))
                        {
                            SortedSet<String> keys = new TreeSet<>(workCenterOpAlloc.getTimeBlockAllocation().keySet());

                            keys.forEach((String key) ->
                            {
                                int currentOp = workCenterOpAlloc.getTimeBlockAllocation().get(key);
                                if (currentOp != 0)
                                {
                                    int index = getTimeBlockExcelIndex(key);
                                    // Get the Cell at index 2 from the above row
                                    Cell cell = row.getCell(index);

                                    // Create the cell if it doesn't exist
                                    if (cell == null)
                                    {

                                        cell = row.createCell(index);
                                    }

                                    // Update the cell's value
                                    cell.setCellValue(currentOp);
                                }
                            });
                        }

                    }
                });

                inputStream.close();
                // Write the output to the file
                try (FileOutputStream fileOut = new FileOutputStream(xlsxFilePath))
                {
                    workbook.write(fileOut);
                    // Closing the workbook
                }
            }

        } catch (FileNotFoundException ex)
        {
            LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
            return false;
        } catch (IOException | InvalidFormatException | EncryptedDocumentException ex)
        {
            LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
            return false;
        } finally
        {
            try
            {
                inputStream.close();
            } catch (IOException ex)
            {
                LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean unscheduleOperations(List<ShopOrderOperationModel> operationsList, String storageName)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean unscheduleAllOperationsFrom(ShopOrderOperationModel operation, String storageName)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean interruptWorkCenter(String workCenterNo, DateTime startTime, DateTime endTime)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int addShopOrderOperation(ShopOrderOperationModel shopOrderOperation, String storageName)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean replacePrecedingOperationId(int precedingOperationId, int replacedById, int exceptOpId, String orderNo)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean changeOperationStatus(int operationId, DataModelEnums.OperationStatus operationStatus)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean removeOperationScheduleData(int operationId, DataModelEnums.OperationStatus operationStatus)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean changeShopOrderScheduleData(String orderNo, DataModelEnums.ShopOrderScheduleStatus scheduleStatus, DateTime startDate, DateTime finishDate)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean makeAvailableTempUnavailableTimeblocks(String workCenterNo)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addPartDetails(PartModel partDetails, String storageName)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean updatePartDetails(PartModel partDetails, String storageName)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addPartUnavailabilityDetails(PartUnavailabilityModel partUnavailabilityDetail, String storageName)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean updatePartUnavailabilityDetails(PartUnavailabilityModel partUnavailabilityDetail, String storageName)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addWorkCenterInterruptionDetails(WorkCenterInterruptionsModel workCenterInterruptionDetail, String storageName)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean updateWorkCenterInterruptionDetails(WorkCenterInterruptionsModel workCenterInterruptionDetail, String storageName)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean changeOperationStatusToUnschedule(String orderNo)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * returns the column index related to the specific timeBlock
     *
     * @param timeBlock timeBlocValue
     * @return excelIndex
     */
    private static int getTimeBlockExcelIndex(String timeBlock)
    {
        int index = -1;
        int startingIndex = 2;
        switch (timeBlock)
        {
            case "TB1":
                index = startingIndex;
                break;
            case "TB2":
                index = startingIndex + 1;
                break;
            case "TB3":
                index = startingIndex + 2;
                break;
            case "TB4":
                index = startingIndex + 3;
                break;
            case "TB5":
                index = startingIndex + 4;
                break;
            case "TB6":
                index = startingIndex + 5;
                break;
            case "TB7":
                index = startingIndex + 6;
                break;
            case "TB8":
                index = startingIndex + 7;
                break;
            default:
                break;
        }

        return index;
    }
}
