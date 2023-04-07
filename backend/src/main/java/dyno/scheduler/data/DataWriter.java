/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dyno.scheduler.data;

import dyno.scheduler.datamodels.DataModelEnums;
import dyno.scheduler.datamodels.DataModelEnums.OperationStatus;
import dyno.scheduler.datamodels.PartModel;
import dyno.scheduler.datamodels.PartUnavailabilityModel;
import dyno.scheduler.datamodels.ShopOrderModel;
import dyno.scheduler.datamodels.ShopOrderOperationModel;
import dyno.scheduler.datamodels.WorkCenterInterruptionsModel;
import dyno.scheduler.datamodels.WorkCenterModel;
import dyno.scheduler.datamodels.WorkCenterOpAllocModel;
import dyno.scheduler.utils.MySqlUtil;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;

/**
 *
 * @author Prabash
 */
public class DataWriter
{
    
    //<editor-fold defaultstate="collapsed" desc="Shop Order Methods">
    
    public static boolean addShopOrder(ShopOrderModel shopOrder)
    {
        List<ShopOrderModel> shopOrders = new ArrayList<>();
        shopOrders.add(shopOrder);
        return addShopOrderData(shopOrders);
    }
     
    public static boolean addShopOrderData(List<ShopOrderModel> shopOrders)
    {
        return DataFactory.getDataWriteManagerInstance().addData(shopOrders, DataModelEnums.DataModelType.ShopOrder);
    }
    
    public static boolean updateShopOrder(ShopOrderModel shopOrder)
    {
        List<ShopOrderModel> shopOrders = new ArrayList<>();
        shopOrders.add(shopOrder);
        return updateShopOrderData(shopOrders);
    }
    
    public static boolean updateShopOrderData(List<ShopOrderModel> shopOrders)
    {
        return DataFactory.getDataWriteManagerInstance().updateData(shopOrders, DataModelEnums.DataModelType.ShopOrder);
    }
    
    public static boolean changeShopOrderScheduleData(String orderNo, DataModelEnums.ShopOrderScheduleStatus scheduleStatus, DateTime startDate, DateTime finishDate)
    {
        return DataFactory.getDataWriteManagerInstance().changeShopOrderScheduleData(orderNo, scheduleStatus, startDate, finishDate);
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Shop Order Operation Methods">
    
    public static boolean addShopOrderOperationData(List<ShopOrderOperationModel> shopOrderOperations)
    {
        return DataFactory.getDataWriteManagerInstance().addData(shopOrderOperations, DataModelEnums.DataModelType.ShopOrderOperation);
    }
    
    public static int addShopOrderOperation(ShopOrderOperationModel shopOrderOperation)
    {
        String tableName = MySqlUtil.getStorageName(DataModelEnums.DataModelType.ShopOrderOperation);
        return DataFactory.getDataWriteManagerInstance().addShopOrderOperation(shopOrderOperation, tableName);
    }
    
    public static boolean updateShopOrderOperationData(List<ShopOrderOperationModel> shopOrderOperations)
    {
        return DataFactory.getDataWriteManagerInstance().updateData(shopOrderOperations, DataModelEnums.DataModelType.ShopOrderOperation);
    }
    
    public static boolean updateShopOrderOperation(ShopOrderOperationModel shopOrderOperation)
    {
        List<ShopOrderOperationModel> shopOrderOperations = new ArrayList<>();
        shopOrderOperations.add(shopOrderOperation);
        return updateShopOrderOperationData(shopOrderOperations);
    }
    
    public static boolean replacePrecedingOperationId(int precedingOperationId, int replaceById, int exceptOpId, String orderNo)
    {
        return DataFactory.getDataWriteManagerInstance().replacePrecedingOperationId(precedingOperationId, replaceById, exceptOpId, orderNo);
    }
    
    public static boolean updateOperationStatus(int operationId, OperationStatus operationStatus)
    {
        return DataFactory.getDataWriteManagerInstance().changeOperationStatus(operationId, operationStatus);
    }
    
    public static boolean removeScheduledOperationData(int operationId, OperationStatus operationStatus)
    {
        return DataFactory.getDataWriteManagerInstance().removeOperationScheduleData(operationId, operationStatus);
    }
    
    public static boolean changeOpStatusToUnschedule(String orderNo)
    {
        return DataFactory.getDataWriteManagerInstance().changeOperationStatusToUnschedule(orderNo);
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Work Center Methods">
    
    public static boolean addWorkCenter(WorkCenterModel workCenter)
    {
        List<WorkCenterModel> workCenters = new ArrayList<>();
        workCenters.add(workCenter);
        return addWorkCenterData(workCenters);
    }
    
    public static boolean addWorkCenterData(List<WorkCenterModel> workCenters)
    {
        return DataFactory.getDataWriteManagerInstance().addData(workCenters, DataModelEnums.DataModelType.WorkCenter);
    }
    
    public static boolean updateWorkCenter(WorkCenterModel workCenter)
    {
        List<WorkCenterModel> workCenters = new ArrayList<>();
        workCenters.add(workCenter);
        return updateWorkCenterData(workCenters);
    }
    
    public static boolean updateWorkCenterData(List<WorkCenterModel> workCenters)
    {
        return DataFactory.getDataWriteManagerInstance().updateData(workCenters, DataModelEnums.DataModelType.WorkCenter);
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Work Center Op Alloc Methods">
    
    public static boolean addWorkCenterOpAlloc(WorkCenterOpAllocModel workCenterOpAlloc)
    {
        List<WorkCenterOpAllocModel> workCenterOpAllocs = new ArrayList<>();
        workCenterOpAllocs.add(workCenterOpAlloc);
        return addWorkCenterOpAllocData(workCenterOpAllocs);
    }
    
    public static boolean addWorkCenterOpAllocData(List<WorkCenterOpAllocModel> workCenterOpAlloc)
    {
        return DataFactory.getDataWriteManagerInstance().addData(workCenterOpAlloc, DataModelEnums.DataModelType.WorkCenterAllocationFinite);
    }
    
    public static boolean updateWorkCenterAllocData(List<WorkCenterOpAllocModel> workCenterOpAllocations)
    {
        return DataFactory.getDataWriteManagerInstance().updateData(workCenterOpAllocations, DataModelEnums.DataModelType.WorkCenterAllocationFinite);
    }
    
    public static boolean makeAvailableTempUnavailableAllocs(String workCenterNo)
    {
        return DataFactory.getDataWriteManagerInstance().makeAvailableTempUnavailableTimeblocks(workCenterNo);
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Work Center Interruption Methods">
    
    public static boolean addWCInterruptionDetails(WorkCenterInterruptionsModel wcInterruptionDetail)
    {
        String tableName = MySqlUtil.getStorageName(DataModelEnums.DataModelType.WorkCenterInterruptionsTab);
        return DataFactory.getDataWriteManagerInstance().addWorkCenterInterruptionDetails(wcInterruptionDetail, tableName);
    }
    
    public static boolean addWCInterruptionDetails(List<WorkCenterInterruptionsModel> wcInterruptionDetails)
    {
        for (WorkCenterInterruptionsModel wcInterruptionDetail : wcInterruptionDetails)
        {
            addWCInterruptionDetails(wcInterruptionDetail);
        }
        return true;
    }
    
    public static boolean updateWCInterruptionDetails(WorkCenterInterruptionsModel wcInterruptionDetail)
    {
        String tableName = MySqlUtil.getStorageName(DataModelEnums.DataModelType.WorkCenterInterruptionsTab);
        return DataFactory.getDataWriteManagerInstance().updateWorkCenterInterruptionDetails(wcInterruptionDetail, tableName);
    }
    
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Part Details Methods">

    public static boolean addPartDetails(PartModel partDetail)
    {
        String tableName = MySqlUtil.getStorageName(DataModelEnums.DataModelType.PartTab);
        return DataFactory.getDataWriteManagerInstance().addPartDetails(partDetail, tableName);
    }
    
    public static boolean addPartDetails(List<PartModel> partDetails)
    {
        for (PartModel partDetail : partDetails)
        {
            addPartDetails(partDetail);
        }
        return true;
    }
    
    public static boolean updatePartDetails(PartModel partDetail)
    {
        String tableName = MySqlUtil.getStorageName(DataModelEnums.DataModelType.PartTab);
        return DataFactory.getDataWriteManagerInstance().updatePartDetails(partDetail, tableName);
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Part Unavailability Methods">
    
    public static boolean addPartUnavailabilityDetails(PartUnavailabilityModel partUnavailabilityDetail)
    {
        String tableName = MySqlUtil.getStorageName(DataModelEnums.DataModelType.PartUnavailabilityTab);
        return DataFactory.getDataWriteManagerInstance().addPartUnavailabilityDetails(partUnavailabilityDetail, tableName);
    }
    
    public static boolean addPartUnavailabilityDetails(List<PartUnavailabilityModel> partUnavailabilityDetails)
    {
        for (PartUnavailabilityModel partUnavailabilityDetail : partUnavailabilityDetails)
        {
            addPartUnavailabilityDetails(partUnavailabilityDetail);
        }
        return true;
    }
    
    public static boolean updatePartUnavailabilityDetails(PartUnavailabilityModel partUnavailabilityDetail)
    {
        String tableName = MySqlUtil.getStorageName(DataModelEnums.DataModelType.PartUnavailabilityTab);
        return DataFactory.getDataWriteManagerInstance().updatePartUnavailabilityDetails(partUnavailabilityDetail, tableName);
    }
    
    //</editor-fold>
}
