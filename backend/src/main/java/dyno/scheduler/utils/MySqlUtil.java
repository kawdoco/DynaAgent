/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dyno.scheduler.utils;

import dyno.scheduler.datamodels.DataModelEnums;
import dyno.scheduler.datamodels.DataModelEnums.StoredProcedures;

/**
 *
 * @author Prabash
 */
public class MySqlUtil
{

    public static String getStorageName(DataModelEnums.DataModelType dataModel)
    {
        switch (dataModel)
        {
            case ShopOrder:
                return getDbName() + "shop_order_tab";
            case ShopOrderOperation:
                return getDbName() + "shop_order_operation_tab";
            case WorkCenter:
                return getDbName() + "work_center_tab";
            case WorkCenterAllocationFinite:
                return getDbName() + "work_center_op_alloc_finite_tab";
            case PartTab:
                return getDbName() + "part_tab";
            case PartUnavailabilityTab:
                return getDbName() + "part_unavailability_tab";
            case WorkCenterInterruptionsTab:
                return getDbName() + "work_center_interruptions_tab";
            default:
                return "";
        }
    }

    public static String getDbName()
    {
        return "dynoschedule_test.";
    }

    public static String getStoredProcedureName(StoredProcedures procedure)
    {
        switch (procedure)
        {
            case OperationScheduledTimeBlockFinite:
                return "get_operation_scheduled_time_block_finite";
            case InterruptedOperaitonDetails:
                return "get_interrupted_operation_details";
            case UnscheduledOrders:
                return "get_unscheduled_orders";
            case ScheduledOrders:
                return "get_scheduled_orders";
            case UnschedledOperationWorkCenters:
                return "get_unscheduled_operation_workcenters";
            case AllOperationsOrdered:
                return "get_all_shop_order_operations_ordered";
            case ByOrderNoOperationsOrdered:
                return "get_shop_order_operations_ordered";
            case LowerPriorityBlockerShopOrders:
                return "get_lower_priority_blocker_shop_orders";
            case ReplacePrecedingOperationIDs:
                return "replace_preceding_op_ids";
            case MakeAvailableTempUnavailableLocationsFinite:
                return "make_available_temp_unavailable_locations_finite_tab";
            case PartUnavailabilityDetails:
                return "get_part_unavailability_details";
            case WorkCenterInterruptions:
                return "get_work_center_interruptions";
            case AffectedOperationsByPartUnavailability:
                return "get_affected_operations_by_part_unavailability";
            case ChangeOperationStatusToUnschedule:
                return "change_operation_status_to_unschedule";
            case ScheduledOrdersByWorkCenters:
                return "get_scheduled_orders_by_work_centers";
            case ScheduledOperationsByWorkCenters:
                return "get_scheduled_operations_by_work_centers";
            case WorkCenterDetails:
                return "get_work_center_details";
            default:
                return "";
        }
    }
}
