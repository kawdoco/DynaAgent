/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dyno.scheduler.datamodels;

/**
 *
 * @author Prabash
 */
public class DataModelEnums
{
    /**
     * species the priority of a shop order
     */
    public enum ShopOrderPriority
    {
        Critical(5),
        High(4),
        Medium(3),
        Low(2),
        Trivial(1);
        
        private final int value;

        public int getValue()
        {
            return value;
        }
        private ShopOrderPriority(int value)        
        {
            this.value = value;
        }
    }
    
    public enum InerruptionType
    {
        PartUnavailable(-2),
        Interruption(-1),
        Normal(0);
        
        private final int value;

        public int getValue()
        {
            return value;
        }
        
        private InerruptionType(int value)        
        {
            this.value = value;
        }
    }

    public enum ShopOrderScheduleStatus
    {
        Scheduled,
        PartiallyScheduled,
        Unscheduled
    }

    public enum ShopOrderSchedulingDirection
    {
        Backward,
        Forward,
    }

    public enum ShopOrderStatus
    {
        Created,
        Started,
        InProgress,
        Delivered,
        Closed,
        Cancelled
    }
    
    public enum OperationStatus
    {
        Created,
        Scheduled,
        Unscheduled,
        Interrupted,
        Reserved,
        Issued,
        InProgress,
        Completed,
        Cancelled
    }
    
    public enum DataModelType
    {
        ShopOrder,
        ShopOrderOperation,
        WorkCenter,
        WorkCenterAllocationFinite,
        PartTab,
        PartUnavailabilityTab,
        WorkCenterInterruptionsTab
    }
    
    public enum TimeBlockParamType
    {
        TimeBlockValue,
        TimeBlockName
    }
    
    public enum StoredProcedures
    {
        OperationScheduledTimeBlockFinite,
        InterruptedOperaitonDetails,
        UnscheduledOrders,
        ScheduledOrders,
        UnschedledOperationWorkCenters,
        AllOperationsOrdered,
        ByOrderNoOperationsOrdered,
        LowerPriorityBlockerShopOrders,
        ReplacePrecedingOperationIDs,
        MakeAvailableTempUnavailableLocationsFinite,
        PartUnavailabilityDetails,
        WorkCenterInterruptions,
        AffectedOperationsByPartUnavailability,
        ChangeOperationStatusToUnschedule,
        ScheduledOrdersByWorkCenters,
        ScheduledOperationsByWorkCenters,
        WorkCenterDetails,
    }
}
