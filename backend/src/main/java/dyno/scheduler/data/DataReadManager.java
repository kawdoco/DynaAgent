/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dyno.scheduler.data;

import dyno.scheduler.datamodels.*;
import dyno.scheduler.datamodels.DataModelEnums.DataModelType;
import java.util.List;
import org.joda.time.DateTime;

/**
 *
 * @author Prabash
 */
public abstract class DataReadManager
{
    public abstract List<? extends DataModel> getData(DataModelType dataModel);

    protected abstract List<ShopOrderModel> getShopOrderData(String storageName);

    protected abstract List<ShopOrderOperationModel> getShopOrderOperationData(String storageName);

    protected abstract List<WorkCenterModel> getWorkCenterData(String storageName);

    protected abstract List<WorkCenterOpAllocModel> getWorkCenterOpAllocData(String storageName);
    
    /**
     * Get the subsequent operations of a given operation ordered by operation sequence
     * @param shopOrderOperation operation to find the subsequent operations
     * @return list of all the subsequent operations
     */
    protected abstract List<ShopOrderOperationModel> getSubsequentOperations(ShopOrderOperationModel shopOrderOperation);
    
    /**
     * Get the operation scheduled time block details
     * @param operationId
     * @return  
     */
    protected abstract List<OperationScheduleTimeBlocksDataModel> getOperationScheduledTimeBlockDetails(int operationId);
    
    /**
     * Get interrupted operation details of a work center
     * @param interruptionStartDate
     * @param interruptionStartTime
     * @param interruptionEndDate
     * @param interruptionEndTime
     * @param workCenterNo
     * @return 
     */
    protected abstract List<InterruptedOpDetailsDataModel> getInterruptedOperationDetails(DateTime interruptionStartDate, DateTime interruptionStartTime, DateTime interruptionEndDate, DateTime interruptionEndTime, String workCenterNo);
    
    /**
     * Get Unscheduled Shop Orders List
     * @return a list of unscheduled orders
     */
    protected abstract List<ShopOrderModel> getUnscheduledShopOrders();
    
    /**
     * Get scheduled shop orders list
     * @param skip
     * @param take
     * @return 
     */
    protected abstract List<ShopOrderModel> getScheduledShopOrders(int skip, int take);
    
    /**
     * Get Work Centres related unscheduled operations
     * @return unscheduled operations' work centers list
     */
    protected abstract List<WorkCenterModel> getUnscheduledOperationWorkCenters();
    
    /**
     * Get Shop Order Operations by Providing the Shop Order No.
     * Operations will be ordered according to their operation sequence and preceding operation id combination
     * @param orderNo
     * @return ordered operations list
     */
    protected abstract List<ShopOrderOperationModel> getShopOrderOperationsByOrderNo(String orderNo);
    
    protected abstract List<ShopOrderModel> getLowerPriorityBlockerShopOrders(DateTime fromDate, DateTime fromTime, String workCenterType, Double currentPriority);
    
    protected abstract WorkCenterModel getWorkCenterByPrimaryKey(String workCenterNo);
    
    /**
     * get shop order details by providing the order no
     * @param orderNo
     * @return 
     */
    protected abstract ShopOrderModel getShopOrderByPrimaryKey(String orderNo);
    
    protected abstract List<PartModel> getPartDetails();
    
    protected abstract PartModel getPartDetailsByPartNo(String partNo);
    
    protected abstract List<PartUnavailabilityModel> getPartUnavailabilityDetailsByPartNo(String partNo);
    
    protected abstract List<WorkCenterInterruptionsModel> getWorkCenterInterruptionsDetails(String workCenterNo);
    
    protected abstract List<ShopOrderOperationModel> getAffectedOperationsByPartUnavailabiility(String partNo, DateTime unavailabilityStartDate, DateTime unavailabilityStartTime, DateTime unavailabilityEndDate, DateTime unavailabilityEndTime);
    
    protected abstract List<ShopOrderModel> getScheduledOrdersByWorkCenters(String workCenters);
    
    protected abstract List<ShopOrderOperationModel> getScheduledOperationsByWorkCenters(String workCenters);
    
    protected abstract List<WorkCenterModel> getWorkCenters(int skip, int take);
}
