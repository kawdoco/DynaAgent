/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dyno.scheduler.data;

import dyno.scheduler.datamodels.*;
import dyno.scheduler.utils.ExcelUtil;
import dyno.scheduler.utils.GeneralSettings;
import dyno.scheduler.utils.LogUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.joda.time.DateTime;

/**
 *
 * @author Prabash
 */
public class ExcelReadManager extends DataReadManager
{

    /**
     * read the Excel file and return the set of respective object according to
     * DataModelType
     *
     * @param dataModelType the type of the DataModel that should be read
     * @return will allow returning a list that contains a set of object derived
     * by DataModel
     */
    @Override
    public List<? extends DataModel> getData(DataModelEnums.DataModelType dataModelType)
    {
        try
        {
            String excelSheetName = ExcelUtil.getStorageName(dataModelType);
            switch (dataModelType)
            {
                case ShopOrder:
                {
                    return getShopOrderData(excelSheetName);
                }
                case ShopOrderOperation:
                {
                    return getShopOrderOperationData(excelSheetName);
                }
                case WorkCenter:
                {
                    return getWorkCenterData(excelSheetName);
                }
                case WorkCenterAllocationFinite:
                {
                    return getWorkCenterOpAllocData(excelSheetName);
                }
                default:
                {
                    return null;
                }

            }
        } catch (Exception ex)
        {
            LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
            return null;
        }
    }


    @Override
    protected List<ShopOrderModel> getShopOrderData(String storageName)
    {
        List<ShopOrderModel> shopOrders = new ArrayList<>();
        try
        {
            Sheet shopOrderSheet = new ExcelReader(GeneralSettings.getDefaultExcelFile()).readExcelSheet(storageName);

            for (Row row : shopOrderSheet)
            {
                //skip the header row of the excel
                if (row.getRowNum() == 0)
                {
                    continue;
                }

                ShopOrderModel shopOrder = new ShopOrderModel().getModelObject(row);
                shopOrders.add(shopOrder);
            }
        } catch (IOException | InvalidFormatException ex)
        {
            LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
        }

        return shopOrders;
    }

    @Override
    protected List<ShopOrderOperationModel> getShopOrderOperationData(String storageName)
    {
        List<ShopOrderOperationModel> shopOrderOperations = new ArrayList<>();
        try
        {
            Sheet shopOrderOperationsSheet = new ExcelReader(GeneralSettings.getDefaultExcelFile()).readExcelSheet(storageName);

            for (Row row : shopOrderOperationsSheet)
            {
                //skip the header row of the excel
                if (row.getRowNum() == 0)
                {
                    continue;
                }

                ShopOrderOperationModel shopOrderOperation = new ShopOrderOperationModel().getModelObject(row);
                shopOrderOperations.add(shopOrderOperation);
            }
        } catch (IOException | InvalidFormatException ex)
        {
            LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
        }
        return shopOrderOperations;
    }

    @Override
    protected List<WorkCenterModel> getWorkCenterData(String storageName)
    {
        List<WorkCenterModel> workCenters = new ArrayList<>();
        try
        {
            Sheet workCenterSheet = new ExcelReader(GeneralSettings.getDefaultExcelFile()).readExcelSheet(storageName);

            for (Row row : workCenterSheet)
            {
                //skip the header row of the excel
                if (row.getRowNum() == 0)
                {
                    continue;
                }

                WorkCenterModel workCenter = new WorkCenterModel().getModelObject(row);
                workCenters.add(workCenter);
            }
        } catch (IOException | InvalidFormatException ex)
        {
            LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
        }
        return workCenters;
    }

    @Override
    protected List<WorkCenterOpAllocModel> getWorkCenterOpAllocData(String storageName)
    {
        List<WorkCenterOpAllocModel> workCenterOpAllocs = new ArrayList<>();
        try
        {
            Sheet workCenterOpAllocSheet = new ExcelReader(GeneralSettings.getDefaultExcelFile()).readExcelSheet(storageName);

            for (Row row : workCenterOpAllocSheet)
            {
                //skip the header row of the excel
                if (row.getRowNum() == 0)
                {
                    continue;
                }

                WorkCenterOpAllocModel workCenterOpAlloc = new WorkCenterOpAllocModel().getModelObject(row);
                workCenterOpAllocs.add(workCenterOpAlloc);
            }
        } catch (IOException | InvalidFormatException ex)
        {
            LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
        }
        return workCenterOpAllocs;
    }

    @Override
    protected List<ShopOrderOperationModel> getSubsequentOperations(ShopOrderOperationModel shopOrderOperation)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected List<OperationScheduleTimeBlocksDataModel> getOperationScheduledTimeBlockDetails(int operationId)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected List<InterruptedOpDetailsDataModel> getInterruptedOperationDetails(DateTime interruptionStartDate, DateTime interruptionStartTime, DateTime interruptionEndDate, DateTime interruptionEndTime, String workCenterNo)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected List<ShopOrderModel> getUnscheduledShopOrders()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected List<WorkCenterModel> getUnscheduledOperationWorkCenters()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected List<ShopOrderOperationModel> getShopOrderOperationsByOrderNo(String orderNo)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected List<ShopOrderModel> getLowerPriorityBlockerShopOrders(DateTime fromDate, DateTime fromTime, String workCenterType, Double currentPriority)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected WorkCenterModel getWorkCenterByPrimaryKey(String workCenterNo)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected ShopOrderModel getShopOrderByPrimaryKey(String orderNo)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected List<PartModel> getPartDetails()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected PartModel getPartDetailsByPartNo(String partNo)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected List<PartUnavailabilityModel> getPartUnavailabilityDetailsByPartNo(String partNo)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected List<WorkCenterInterruptionsModel> getWorkCenterInterruptionsDetails(String workCenterNo)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected List<ShopOrderOperationModel> getAffectedOperationsByPartUnavailabiility(String partNo, DateTime interruptionStartDate, DateTime interruptionStartTime, DateTime interruptionEndDate, DateTime interruptionEndTime)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected List<ShopOrderModel> getScheduledShopOrders(int skip, int take)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected List<ShopOrderModel> getScheduledOrdersByWorkCenters(String workCenters)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected List<ShopOrderOperationModel> getScheduledOperationsByWorkCenters(String workCenters)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected List<WorkCenterModel> getWorkCenters(int skip, int take)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
