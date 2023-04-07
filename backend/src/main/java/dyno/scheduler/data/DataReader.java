/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dyno.scheduler.data;

import dyno.scheduler.datamodels.*;
import java.util.List;
import java.util.stream.Collectors;
import org.joda.time.DateTime;

/**
 *
 * @author Prabash
 */
public class DataReader
{
    private static List<ShopOrderModel> shopOrderDetails;
    private static List<ShopOrderOperationModel> shopOrderOperationDetails;
    private static List<WorkCenterModel> workCenterDetails;
    private static List<WorkCenterOpAllocModel> workCenterOpAllocDetails;

    /**
     * this method will return all the list of shop orders available
     * @param refresh send true to refresh available data
     * @return list of shop orders
     */
    public synchronized static List<ShopOrderModel> getShopOrderDetails(boolean refresh)
    {
        if (shopOrderDetails == null || refresh)
        {
            // get shop order details 
            populateShopOrderDetails();
            // get shop order operation details
            populateShopOrderOperationDetails();

            // for each shop order, add the related shop order operation details
            shopOrderDetails.forEach((shopOrder) ->
            {
                List<ShopOrderOperationModel> relatedOperations = shopOrderOperationDetails.stream()
                        .filter(op -> op.getOrderNo().equals(shopOrder.getOrderNo()))
                        .collect(Collectors.toList());
                shopOrder.setOperations(relatedOperations);
                shopOrder.assignEstimatedLatestFinishTimeForOperations();
            });
        }
        return shopOrderDetails;
    }

    public synchronized static List<ShopOrderOperationModel> getShopOrderOperationDetails(boolean refresh)
    {
        if (shopOrderOperationDetails == null || refresh)
        {
            populateShopOrderOperationDetails();
        }
        return shopOrderOperationDetails;
    }

    public synchronized static List<WorkCenterModel> getWorkCenterDetails(boolean refresh)
    {
        if (workCenterDetails == null || refresh)
        {
            populateWorkCenterDetails();
        }
        return workCenterDetails;
    }

    public synchronized static List<WorkCenterOpAllocModel> getWorkCenterOpAllocDetails(boolean refresh)
    {
        if (workCenterOpAllocDetails == null || refresh)
        {
            populateWorkCenterOpAllocDetails();
        }
        return workCenterOpAllocDetails;
    }

    private static void populateShopOrderDetails()
    {
        shopOrderDetails = (List<ShopOrderModel>) DataFactory.getDataReadManagerInstance().getData(DataModelEnums.DataModelType.ShopOrder);
    }

    private static void populateShopOrderOperationDetails()
    {
        shopOrderOperationDetails = (List<ShopOrderOperationModel>) DataFactory.getDataReadManagerInstance().getData(DataModelEnums.DataModelType.ShopOrderOperation);
    }

    private static void populateWorkCenterDetails()
    {
        workCenterDetails = (List<WorkCenterModel>) DataFactory.getDataReadManagerInstance().getData(DataModelEnums.DataModelType.WorkCenter);
    }

    private static void populateWorkCenterOpAllocDetails()
    {
        workCenterOpAllocDetails = (List<WorkCenterOpAllocModel>) DataFactory.getDataReadManagerInstance().getData(DataModelEnums.DataModelType.WorkCenterAllocationFinite);
    }
    
    public static List<ShopOrderOperationModel> getSubsequentOperations(ShopOrderOperationModel shopOrderOperation)
    {
        return DataFactory.getDataReadManagerInstance().getSubsequentOperations(shopOrderOperation);
    }
    
    public static List<OperationScheduleTimeBlocksDataModel> getOperationScheduledTimeBlockDetails(int operationId)
    {
        return DataFactory.getDataReadManagerInstance().getOperationScheduledTimeBlockDetails(operationId);
    }
    
    public static List<InterruptedOpDetailsDataModel> getInterruptedOperationDetails(DateTime interruptionStartDate, DateTime interruptionStartTime, DateTime interruptionEndDate, DateTime interruptionEndTime, String workCenterNo)
    {
        return DataFactory.getDataReadManagerInstance().getInterruptedOperationDetails(interruptionStartDate, interruptionStartTime, interruptionEndDate, interruptionEndTime, workCenterNo);
    }
    
    public static List<ShopOrderModel> getUnscheduledOrders()
    {
        return DataFactory.getDataReadManagerInstance().getUnscheduledShopOrders();
    }
    
    public static List<ShopOrderModel> getScheduledOrders(int skip, int take)
    {
        return DataFactory.getDataReadManagerInstance().getScheduledShopOrders(skip, take);
    }
    
    public static List<WorkCenterModel> getUnscheduledOpWorkCenters()
    {
        return DataFactory.getDataReadManagerInstance().getUnscheduledOperationWorkCenters();
    }
    
    public static List<ShopOrderModel> getLowerPriorityBlockerShopOrders(DateTime fromDate, DateTime fromTime, String workCenterType, double priority)
    {
        return DataFactory.getDataReadManagerInstance().getLowerPriorityBlockerShopOrders(fromDate, fromTime, workCenterType, priority);
    }
    
    public static WorkCenterModel getWorkCenterByPrimaryKey(String workCenterNo)
    {
        return DataFactory.getDataReadManagerInstance().getWorkCenterByPrimaryKey(workCenterNo);
    }
    
    public static ShopOrderModel getShopOrderByPrimaryKey(String orderNo)
    {
        return DataFactory.getDataReadManagerInstance().getShopOrderByPrimaryKey(orderNo);
    }
    
    public static List<PartModel> getPartDetails()
    {
        return DataFactory.getDataReadManagerInstance().getPartDetails();
    }
    
    public static PartModel getPartDetailsByPartNo(String partNo)
    {
        return DataFactory.getDataReadManagerInstance().getPartDetailsByPartNo(partNo);
    }
    
    public static List<PartUnavailabilityModel> getPartUnavailabilityDetailsByPartNo(String partNo)
    {
        return DataFactory.getDataReadManagerInstance().getPartUnavailabilityDetailsByPartNo(partNo);
    }
    
    public static List<WorkCenterInterruptionsModel> getWorkCenterInterruptionsByWorkCenter(String workCenter)
    {
        return DataFactory.getDataReadManagerInstance().getWorkCenterInterruptionsDetails(workCenter);
    }
    
    public static List<ShopOrderOperationModel> getAffectedOperationsByPartUnavailabiility(String partNo, DateTime unavailabilityStartDate, DateTime unavailabilityStartTime, DateTime unavailabilityEndDate, DateTime unavailabilityEndTime)
    {
        return DataFactory.getDataReadManagerInstance().getAffectedOperationsByPartUnavailabiility(partNo, unavailabilityStartDate, unavailabilityStartTime, unavailabilityEndDate, unavailabilityEndTime);
    }
    
    public static List<ShopOrderModel> getScheduledOrdersByWorkCentre(String workCentres)
    {
        return DataFactory.getDataReadManagerInstance().getScheduledOrdersByWorkCenters(workCentres);
    }
    
    public static List<ShopOrderOperationModel> getScheduledOperationsByWorkCentre(String workCentres)
    {
        return DataFactory.getDataReadManagerInstance().getScheduledOperationsByWorkCenters(workCentres);
    }
    
    public static List<WorkCenterModel> getWorkCenterDetails(int skip, int take)
    {
        return DataFactory.getDataReadManagerInstance().getWorkCenters(skip, take);
    }
}
