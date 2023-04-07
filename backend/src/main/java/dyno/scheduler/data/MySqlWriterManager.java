/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dyno.scheduler.data;

import dyno.scheduler.datamodels.DataModel;
import dyno.scheduler.datamodels.DataModelEnums;
import dyno.scheduler.datamodels.DataModelEnums.OperationStatus;
import dyno.scheduler.datamodels.DataModelEnums.ShopOrderScheduleStatus;
import dyno.scheduler.datamodels.DataModelEnums.ShopOrderStatus;
import dyno.scheduler.datamodels.PartModel;
import dyno.scheduler.datamodels.PartUnavailabilityModel;
import dyno.scheduler.datamodels.ShopOrderModel;
import dyno.scheduler.datamodels.ShopOrderOperationModel;
import dyno.scheduler.datamodels.WorkCenterInterruptionsModel;
import dyno.scheduler.datamodels.WorkCenterModel;
import dyno.scheduler.datamodels.WorkCenterOpAllocModel;
import dyno.scheduler.utils.DateTimeUtil;
import dyno.scheduler.utils.LogUtil;
import dyno.scheduler.utils.MySqlUtil;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.joda.time.DateTime;

/**
 *
 * @author Prabash
 */
public class MySqlWriterManager extends DataWriteManager
{

    @Override
    public boolean addData(List<? extends DataModel> dataList, DataModelEnums.DataModelType dataModelType)
    {
        try
        {
            String tableName = MySqlUtil.getStorageName(dataModelType);
            switch (dataModelType)
            {
                case ShopOrder:
                {
                    return addShopOrderData((List<ShopOrderModel>) dataList, tableName);
                }
                case ShopOrderOperation:
                {
                    return addShopOrderOperationData((List<ShopOrderOperationModel>) dataList, tableName);
                }
                case WorkCenter:
                {
                    return addWorkCenterData((List<WorkCenterModel>) dataList, tableName);
                }
                case WorkCenterAllocationFinite:
                {
                    return addWorkCenterOpALlocData((List<WorkCenterOpAllocModel>) dataList, tableName);
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
    public boolean updateData(List<? extends DataModel> dataList, DataModelEnums.DataModelType dataModelType)
    {
        try
        {
            String tableName = MySqlUtil.getStorageName(dataModelType);
            switch (dataModelType)
            {
                case ShopOrder:
                {
                    return updateShopOrderData((List<ShopOrderModel>) dataList, tableName);
                }
                case ShopOrderOperation:
                {
                    return updateShopOrderOperationData((List<ShopOrderOperationModel>) dataList, tableName);
                }
                case WorkCenter:
                {
                    return updateWorkCenterData((List<WorkCenterModel>) dataList, tableName);
                }
                case WorkCenterAllocationFinite:
                {
                    return updateWorkCenterOpAllocData((List<WorkCenterOpAllocModel>) dataList, tableName);
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
    public boolean addShopOrderData(List<ShopOrderModel> shopOrders, String storageName)
    {
        try
        {
            String query = "INSERT INTO " + storageName + " "
                    + "(order_no, " + "description, " + "created_date, " + "part_no, "
                    + "structure_revision, " + "routing_revision, " + "required_date, " + "start_date, "
                    + "finish_date, " + "scheduling_direction, " + "customer_no, " + "scheduling_status, "
                    + "shop_order_status, " + "priority, " + "revenue_value, " + "importance) "
                    + "VALUES "
                    + "(?, ?, ?, ?,"
                    + "?, ?, ?, ?, "
                    + "?, ?, ?, ?, "
                    + "?, ?, ?, ?)";

            for (ShopOrderModel shopOrder : shopOrders)
            {
                HashMap<Integer, Object> columnValues = new HashMap<>();
                int i = 0;
                columnValues.put(++i, shopOrder.getOrderNo());
                columnValues.put(++i, shopOrder.getDescription());
                columnValues.put(++i, DateTimeUtil.convertDatetoSqlDate(DateTime.now()));
                columnValues.put(++i, shopOrder.getPartNo());

                columnValues.put(++i, shopOrder.getStructureRevision());
                columnValues.put(++i, shopOrder.getRoutingRevision());
                columnValues.put(++i, shopOrder.getRequiredDate() != null ? DateTimeUtil.convertDatetoSqlDate(shopOrder.getRequiredDate()) : new Date(0));
                columnValues.put(++i, shopOrder.getStartDate() != null ? DateTimeUtil.convertDatetoSqlDate(shopOrder.getStartDate()) : new Date(0));

                columnValues.put(++i, shopOrder.getFinishDate() != null ? DateTimeUtil.convertDatetoSqlDate(shopOrder.getFinishDate()) : new Date(0));
                columnValues.put(++i, shopOrder.getSchedulingDirection().toString());
                columnValues.put(++i, shopOrder.getCustomerNo());
                columnValues.put(++i, ShopOrderScheduleStatus.Unscheduled.toString());

                columnValues.put(++i, ShopOrderStatus.Created.toString());
                columnValues.put(++i, shopOrder.getPriority().toString());
                columnValues.put(++i, shopOrder.getRevenueValue());
                columnValues.put(++i, shopOrder.calculateImportance());

                new MySqlWriter().WriteToTable(query, columnValues);
            }
            return true;
        } catch (Exception ex)
        {
            LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public boolean addShopOrderOperationData(List<ShopOrderOperationModel> dataList, String storageName)
    {
        try
        {
            int precedingOpId = 0;
            for (ShopOrderOperationModel shopOrderOperation : dataList.stream().sorted(new ShopOrderOperationModel()).collect(Collectors.toList()))
            {
                if (shopOrderOperation.getPrecedingOperationId() < 0)
                {
                    shopOrderOperation.setPrecedingOperationId(precedingOpId);
                }
                precedingOpId = addShopOrderOperation(shopOrderOperation, storageName);
            }
            return true;

        } catch (Exception ex)
        {
            LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public int addShopOrderOperation(ShopOrderOperationModel shopOrderOperation, String storageName)
    {
        int precedingOpId;
        String query = "INSERT INTO " + storageName + " "
                + "(operation_no," + "order_no," + "operation_description," + "operation_sequence," + "preceding_operation_id,"
                + "wc_runtime_factor," + "wc_runtime," + "labor_runtime_factor," + "labor_runtime," + "op_start_date,"
                + "op_start_time," + "op_finish_date," + "op_finish_time," + "quantity," + "work_center_type,"
                + "work_center_no," + "operation_status," + "part_no) "
                + "VALUES"
                + "(?,?,?,?,?,"
                + "?,?,?,?,?,"
                + "?,?,?,?,?,"
                + "?,?,?)";

        HashMap<Integer, Object> columnValues = new HashMap<>();
        int i = 0;
        columnValues.put(++i, shopOrderOperation.getOperationNo());
        columnValues.put(++i, shopOrderOperation.getOrderNo());
        columnValues.put(++i, shopOrderOperation.getOperationDescription());
        columnValues.put(++i, shopOrderOperation.getOperationSequence());
        columnValues.put(++i, shopOrderOperation.getPrecedingOperationId());

        columnValues.put(++i, shopOrderOperation.getWorkCenterRuntimeFactor());
        columnValues.put(++i, shopOrderOperation.getWorkCenterRuntime());
        columnValues.put(++i, shopOrderOperation.getLaborRuntimeFactor());
        columnValues.put(++i, shopOrderOperation.getLaborRunTime());

        columnValues.put(++i, shopOrderOperation.getOpStartDate() != null ? DateTimeUtil.convertDatetoSqlDate(shopOrderOperation.getOpStartDate()) : new Date(0));
        columnValues.put(++i, shopOrderOperation.getOpStartTime() != null ? DateTimeUtil.convertTimetoSqlTime(shopOrderOperation.getOpStartTime()) : new Time(0));
        columnValues.put(++i, shopOrderOperation.getOpFinishDate() != null ? DateTimeUtil.convertDatetoSqlDate(shopOrderOperation.getOpFinishDate()) : new Date(0));
        columnValues.put(++i, shopOrderOperation.getOpFinishDate() != null ? DateTimeUtil.convertTimetoSqlTime(shopOrderOperation.getOpFinishTime()) : new Time(0));

        columnValues.put(++i, shopOrderOperation.getQuantity());
        columnValues.put(++i, shopOrderOperation.getWorkCenterType());
        columnValues.put(++i, shopOrderOperation.getWorkCenterNo());
        columnValues.put(++i, shopOrderOperation.getOperationStatus().toString());
        columnValues.put(++i, shopOrderOperation.getPartNo().toString());

        precedingOpId = new MySqlWriter().WriteToTable(query, columnValues);
        return precedingOpId;
    }

    @Override
    public boolean addWorkCenterData(List<WorkCenterModel> workCenters, String storageName)
    {
        String query = "INSERT INTO " + storageName + " "
                + "(work_center_no, " + "work_center_type, " + "description, " + "work_center_capacity) "
                + "VALUES "
                + "(?, ?, ?, ? )";
        try
        {

            for (WorkCenterModel workCenter : workCenters)
            {
                HashMap<Integer, Object> columnValues = new HashMap<>();
                int i = 0;
                columnValues.put(++i, workCenter.getWorkCenterNo());
                columnValues.put(++i, workCenter.getWorkCenterType());
                columnValues.put(++i, workCenter.getWorkCenterDescription());
                columnValues.put(++i, workCenter.getWorkCenterCapacity());

                new MySqlWriter().WriteToTable(query, columnValues);
            }
            return true;
        } catch (Exception ex)
        {
            LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public boolean addWorkCenterOpALlocData(List<WorkCenterOpAllocModel> workCenterOpAllocs, String storageName)
    {
        String query = "INSERT INTO " + storageName + " " 
                + "(work_center_no, " + "operation_date, " 
                + "TB1, " + "TB2, " + "TB3, " + "TB4, " 
                + "TB5, " + "TB6, " + "TB7, " + "TB8) " 
                + "VALUES "
                + "(?, ?, "
                + "?, ?, ?, ?, "
                + "?, ?, ?, ? )";
        try
        {
            for (WorkCenterOpAllocModel workCenterOpAlloc : workCenterOpAllocs)
            {
                HashMap<Integer, Object> columnValues = new HashMap<>();
                int i = 0;
                columnValues.put(++i, workCenterOpAlloc.getWorkCenterNo());
                columnValues.put(++i, workCenterOpAlloc.getOperationDate());
                
                columnValues.put(++i, 0);
                columnValues.put(++i, 0);
                columnValues.put(++i, 0);
                columnValues.put(++i, 0);
                
                columnValues.put(++i, 0);
                columnValues.put(++i, 0);
                columnValues.put(++i, 0);
                columnValues.put(++i, 0);
                
                new MySqlWriter().WriteToTable(query, columnValues);
                
            }

            return true;
        } catch (Exception ex)
        {
            LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public boolean updateShopOrderData(List<ShopOrderModel> shopOrders, String storageName)
    {
        try
        {
            String query = "UPDATE " + storageName + " "
                    + "SET "
                    + "description = ?, "
                    + "part_no =?, "
                    + "structure_revision = ?, "
                    + "routing_revision = ?, "
                    + "required_date = ?, "
                    + "start_date = ?, "
                    + "finish_date = ?, "
                    + "scheduling_direction = ?, "
                    + "customer_no = ?, "
                    + "scheduling_status = ?, "
                    + "shop_order_status = ?, "
                    + "priority = ?, "
                    + "revenue_value = ?, "
                    + "importance = ? "
                    + "WHERE id = ?";

            for (ShopOrderModel shopOrder : shopOrders)
            {

                HashMap<Integer, Object> columnValues = new HashMap<>();
                int i = 0;
                columnValues.put(++i, shopOrder.getDescription());
                columnValues.put(++i, shopOrder.getPartNo());
                columnValues.put(++i, shopOrder.getStructureRevision());
                columnValues.put(++i, shopOrder.getRoutingRevision());
                columnValues.put(++i, shopOrder.getRequiredDate() != null ? DateTimeUtil.convertDatetoSqlDate(shopOrder.getRequiredDate()) : new Date(0));
                columnValues.put(++i, shopOrder.getStartDate() != null ? DateTimeUtil.convertDatetoSqlDate(shopOrder.getStartDate()) : new Date(0));
                columnValues.put(++i, shopOrder.getFinishDate() != null ? DateTimeUtil.convertDatetoSqlDate(shopOrder.getFinishDate()) : new Date(0));
                columnValues.put(++i, shopOrder.getSchedulingDirection().toString());
                columnValues.put(++i, shopOrder.getCustomerNo());
                columnValues.put(++i, shopOrder.getSchedulingStatus().toString());
                columnValues.put(++i, shopOrder.getShopOrderStatus().toString());
                columnValues.put(++i, shopOrder.getPriority().toString());
                columnValues.put(++i, shopOrder.getRevenueValue());
                columnValues.put(++i, shopOrder.calculateImportance());

                columnValues.put(++i, shopOrder.getId());

                new MySqlWriter().WriteToTable(query, columnValues);
            }
            return true;
        } catch (Exception ex)
        {
            LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public boolean updateShopOrderOperationData(List<ShopOrderOperationModel> dataList, String storageName)
    {
        try
        {
            String query = "UPDATE " + storageName + " "
                    + "SET "
                    + "operation_no = ?, "
                    + "order_no =?, "
                    + "operation_description = ?, "
                    + "operation_sequence = ?, "
                    + "preceding_operation_id = ?, "
                    + "wc_runtime_factor = ?, "
                    + "wc_runtime = ?, "
                    + "labor_runtime_factor = ?, "
                    + "labor_runtime = ?, "
                    + "op_start_date = ?, "
                    + "op_start_time = ?, "
                    + "op_finish_date = ?, "
                    + "op_finish_time = ?, "
                    + "quantity = ?, "
                    + "work_center_type = ?, "
                    + "work_center_no = ?, "
                    + "operation_status = ?, "
                    + "part_no = ? "
                    + "WHERE id = ?";

            for (ShopOrderOperationModel shopOrderOperation : dataList)
            {
                HashMap<Integer, Object> columnValues = new HashMap<>();
                int i = 0;
                columnValues.put(++i, shopOrderOperation.getOperationNo());
                columnValues.put(++i, shopOrderOperation.getOrderNo());
                columnValues.put(++i, shopOrderOperation.getOperationDescription());
                columnValues.put(++i, shopOrderOperation.getOperationSequence());
                columnValues.put(++i, shopOrderOperation.getPrecedingOperationId());
                columnValues.put(++i, shopOrderOperation.getWorkCenterRuntimeFactor());
                columnValues.put(++i, shopOrderOperation.getWorkCenterRuntime());
                columnValues.put(++i, shopOrderOperation.getLaborRuntimeFactor());
                columnValues.put(++i, shopOrderOperation.getLaborRunTime());
                columnValues.put(++i, shopOrderOperation.getOpStartDate() != null ? DateTimeUtil.convertDatetoSqlDate(shopOrderOperation.getOpStartDate()) : new Date(0));
                columnValues.put(++i, shopOrderOperation.getOpStartTime() != null ? DateTimeUtil.convertTimetoSqlTime(shopOrderOperation.getOpStartTime()) : new Time(0));
                columnValues.put(++i, shopOrderOperation.getOpFinishDate() != null ? DateTimeUtil.convertDatetoSqlDate(shopOrderOperation.getOpFinishDate()) : new Date(0));
                columnValues.put(++i, shopOrderOperation.getOpFinishDate() != null ? DateTimeUtil.convertTimetoSqlTime(shopOrderOperation.getOpFinishTime()) : new Time(0));
                columnValues.put(++i, shopOrderOperation.getQuantity());
                columnValues.put(++i, shopOrderOperation.getWorkCenterType());
                columnValues.put(++i, shopOrderOperation.getWorkCenterNo());
                columnValues.put(++i, shopOrderOperation.getOperationStatus().toString());
                columnValues.put(++i, shopOrderOperation.getPartNo().toString());
                columnValues.put(++i, shopOrderOperation.getOperationId());

                new MySqlWriter().WriteToTable(query, columnValues);
            }
            return true;

        } catch (Exception ex)
        {
            LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public boolean updateWorkCenterData(List<WorkCenterModel> workCenters, String storageName)
    {
       String query = "UPDATE " + storageName + " "
                    + "SET "
                    + "work_center_no = ?, " 
                    + "work_center_type = ?, " 
                    + "description = ?, " 
                    + "work_center_capacity = ? "
                    + "WHERE id = ?";
        try
        {

            for (WorkCenterModel workCenter : workCenters)
            {
                HashMap<Integer, Object> columnValues = new HashMap<>();
                int i = 0;
                columnValues.put(++i, workCenter.getWorkCenterNo());
                columnValues.put(++i, workCenter.getWorkCenterType());
                columnValues.put(++i, workCenter.getWorkCenterDescription());
                columnValues.put(++i, workCenter.getWorkCenterCapacity().toString());
                
                columnValues.put(++i, workCenter.getId());

                new MySqlWriter().WriteToTable(query, columnValues);
            }
            return true;
        } catch (Exception ex)
        {
            LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public boolean updateWorkCenterOpAllocData(List<WorkCenterOpAllocModel> dataList, String storageName)
    {
        try
        {
            for (WorkCenterOpAllocModel workCenterOpAlloc : dataList)
            {
                StringBuilder queryBuilder = new StringBuilder();
                queryBuilder.append("UPDATE ").append(storageName).append(" SET ");
                HashMap<Integer, Object> columnValues = new HashMap<>();
                int i = 0;

                int size = workCenterOpAlloc.getTimeBlockAllocation().entrySet().size();
                int counter = 1;
                for (Map.Entry<String, Integer> timeBlockEntry : workCenterOpAlloc.getTimeBlockAllocation().entrySet())
                {
                    if (counter < size)
                    {
                        queryBuilder.append(timeBlockEntry.getKey()).append(" = ?, ");
                    } else if (counter == size)
                    {
                        queryBuilder.append(timeBlockEntry.getKey()).append(" = ? ");
                    }

                    columnValues.put(++i, timeBlockEntry.getValue());
                    counter++;
                }

                queryBuilder.append("WHERE work_center_no = ? ");
                columnValues.put(++i, workCenterOpAlloc.getWorkCenterNo());

                queryBuilder.append("AND operation_date = ? ");
                columnValues.put(++i, DateTimeUtil.convertDatetoSqlDate(workCenterOpAlloc.getOperationDate()));

                new MySqlWriter().WriteToTable(queryBuilder.toString(), columnValues);
            }
            return true;
        } catch (Exception ex)
        {
            LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public boolean unscheduleOperations(List<ShopOrderOperationModel> operationsList, String storageName)
    {
        try
        {
            for (ShopOrderOperationModel shopOrderOperation : operationsList)
            {
                removeOperationScheduleData(shopOrderOperation.getOperationId(), OperationStatus.Unscheduled);
            }
            return true;
        } catch (Exception ex)
        {
            LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public boolean unscheduleAllOperationsFrom(ShopOrderOperationModel operation, String storageName)
    {
        return unscheduleOperations(DataReader.getSubsequentOperations(operation), storageName);
    }

    @Override
    public boolean interruptWorkCenter(String workCenterNo, DateTime startTime, DateTime endTime)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean replacePrecedingOperationId(int precedingOperationId, int replacedById, int exceptOpId, String orderNo)
    {
        ArrayList<Object> parameters = new ArrayList<>();
        String storedProcedure = MySqlUtil.getStoredProcedureName(DataModelEnums.StoredProcedures.ReplacePrecedingOperationIDs);
        parameters.add(precedingOperationId);
        parameters.add(replacedById);
        parameters.add(exceptOpId);
        parameters.add(orderNo);

        int result;
        try
        {
            result = new MySqlWriter().invokeUpdateStoredProcedure(storedProcedure, parameters);

        } catch (Exception ex)
        {
            LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
        }
        return true;
    }

    @Override
    public boolean changeOperationStatus(int operationId, OperationStatus operationStatus)
    {
        String tableName = MySqlUtil.getStorageName(DataModelEnums.DataModelType.ShopOrderOperation);
        try
        {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("UPDATE ").append(tableName).append(" SET ");
            HashMap<Integer, Object> columnValues = new HashMap<>();
            int i = 0;

            queryBuilder.append("operation_status = ? ");
            columnValues.put(++i, operationStatus.toString());

            queryBuilder.append("WHERE id = ? ");
            columnValues.put(++i, operationId);

            new MySqlWriter().WriteToTable(queryBuilder.toString(), columnValues);
            return true;
        } catch (Exception ex)
        {
            LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public boolean removeOperationScheduleData(int operationId, OperationStatus operationStatus)
    {
        String tableName = MySqlUtil.getStorageName(DataModelEnums.DataModelType.ShopOrderOperation);
        try
        {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("UPDATE ").append(tableName).append(" SET ");
            HashMap<Integer, Object> columnValues = new HashMap<>();
            int i = 0;

            queryBuilder.append("op_start_date = ?, ");
            columnValues.put(++i, new Date(0));

            queryBuilder.append("op_start_time = ?, ");
            columnValues.put(++i, new Time(0));

            queryBuilder.append("op_finish_date = ?, ");
            columnValues.put(++i, new Date(0));

            queryBuilder.append("op_finish_time = ?, ");
            columnValues.put(++i, new Time(0));

            queryBuilder.append("work_center_no = ?, ");
            columnValues.put(++i, "");

            queryBuilder.append("operation_status = ? ");
            columnValues.put(++i, operationStatus.toString());

            queryBuilder.append("WHERE id = ? ");
            columnValues.put(++i, operationId);

            new MySqlWriter().WriteToTable(queryBuilder.toString(), columnValues);
            return true;
        } catch (Exception ex)
        {
            LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public boolean changeShopOrderScheduleData(String orderNo, ShopOrderScheduleStatus scheduleStatus, DateTime startDate, DateTime finishDate)
    {
        Date sqlStartDate;
        Date sqlFinishDate;
        
        if (scheduleStatus.equals(ShopOrderScheduleStatus.Unscheduled))
        {
            sqlStartDate = new Date(0);
            sqlFinishDate = new Date(0);
        }
        else if (scheduleStatus.equals(ShopOrderScheduleStatus.PartiallyScheduled))
        {
            sqlStartDate = DateTimeUtil.convertDatetoSqlDate(startDate);
            sqlFinishDate =  new Date(0);
        }
        else
        {
            sqlStartDate = DateTimeUtil.convertDatetoSqlDate(startDate);
            sqlFinishDate = DateTimeUtil.convertDatetoSqlDate(finishDate);
        }
            
        String tableName = MySqlUtil.getStorageName(DataModelEnums.DataModelType.ShopOrder);
        try
        {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("UPDATE ").append(tableName).append(" SET ");
            HashMap<Integer, Object> columnValues = new HashMap<>();
            int i = 0;

            queryBuilder.append("start_date = ?, ");
            columnValues.put(++i, sqlStartDate);

            queryBuilder.append("finish_date = ?, ");
            columnValues.put(++i, sqlFinishDate);
            
            queryBuilder.append("scheduling_status = ? ");
            columnValues.put(++i, scheduleStatus.toString());

            queryBuilder.append("WHERE order_no = ? ");
            columnValues.put(++i, orderNo);

            new MySqlWriter().WriteToTable(queryBuilder.toString(), columnValues);
            return true;
        } catch (Exception ex)
        {
            LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public boolean makeAvailableTempUnavailableTimeblocks(String workCenterNo)
    {
        ArrayList<Object> parameters = new ArrayList<>();
        String storedProcedure = MySqlUtil.getStoredProcedureName(DataModelEnums.StoredProcedures.MakeAvailableTempUnavailableLocationsFinite);
        parameters.add(workCenterNo);
        int result;
        try
        {
            result = new MySqlWriter().invokeUpdateStoredProcedure(storedProcedure, parameters);

        } catch (Exception ex)
        {
            LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
        }
        return true;
    }

    @Override
    public boolean addPartDetails(PartModel partDetails, String storageName)
    {
        String query = "INSERT INTO " + storageName + " "
                + "(part_no, " + "part_description, " + "vendor) "
                + "VALUES "
                + "(?, ?, ? )";
        try
        {

            HashMap<Integer, Object> columnValues = new HashMap<>();
            int i = 0;
            columnValues.put(++i, partDetails.getPartNo());
            columnValues.put(++i, partDetails.getPartDescription());
            columnValues.put(++i, partDetails.getVendor());

            new MySqlWriter().WriteToTable(query, columnValues);
            
            return true;
        } catch (Exception ex)
        {
            LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public boolean updatePartDetails(PartModel partDetails, String storageName)
    {
        String query = "UPDATE " + storageName + " "
                    + "SET "
                    + "part_no = ?, " 
                    + "part_description = ?, " 
                    + "vendor = ? "
                    + "WHERE id = ?";
        try
        {
            HashMap<Integer, Object> columnValues = new HashMap<>();
            int i = 0;
            columnValues.put(++i, partDetails.getPartNo());
            columnValues.put(++i, partDetails.getPartDescription());
            columnValues.put(++i, partDetails.getVendor());

            columnValues.put(++i, partDetails.getId());

            new MySqlWriter().WriteToTable(query, columnValues);
            
            return true;
        } catch (Exception ex)
        {
            LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public boolean addPartUnavailabilityDetails(PartUnavailabilityModel partUnavailabilityDetail, String storageName)
    {
        String query = "INSERT INTO " + storageName + " "
                + "(part_no, " + "unavailable_from_date, " + "unavailable_from_time, " + "unavailable_to_date, " + "unavailable_to_time) "
                + "VALUES "
                + "(?, ?, ?, ?, ? )";
        try
        {

            HashMap<Integer, Object> columnValues = new HashMap<>();
            int i = 0;
            columnValues.put(++i, partUnavailabilityDetail.getPartNo());
            columnValues.put(++i, partUnavailabilityDetail.getUnavailableFromDate()!= null ? DateTimeUtil.convertDatetoSqlDate(partUnavailabilityDetail.getUnavailableFromDate()) : new Date(0));
            columnValues.put(++i, partUnavailabilityDetail.getUnavailableFromTime()!= null ? DateTimeUtil.convertTimetoSqlTime(partUnavailabilityDetail.getUnavailableFromTime()) : new Time(0));
            columnValues.put(++i, partUnavailabilityDetail.getUnavailableToDate()!= null ? DateTimeUtil.convertDatetoSqlDate(partUnavailabilityDetail.getUnavailableToDate()) : new Date(0));
            columnValues.put(++i, partUnavailabilityDetail.getUnavailableToTime()!= null ? DateTimeUtil.convertTimetoSqlTime(partUnavailabilityDetail.getUnavailableToTime()) : new Time(0));

            new MySqlWriter().WriteToTable(query, columnValues);
            
            return true;
        } catch (Exception ex)
        {
            LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public boolean updatePartUnavailabilityDetails(PartUnavailabilityModel partUnavailabilityDetail, String storageName)
    {
        String query = "UPDATE " + storageName + " "
                    + "SET "
                    + "part_no = ?, " 
                    + "unavailable_from_date = ?, " 
                    + "unavailable_from_time = ?, " 
                    + "unavailable_to_date = ?, " 
                    + "unavailable_to_time = ? "
                    + "WHERE id = ?";
        try
        {
            HashMap<Integer, Object> columnValues = new HashMap<>();
            int i = 0;
            columnValues.put(++i, partUnavailabilityDetail.getPartNo());
            columnValues.put(++i, partUnavailabilityDetail.getUnavailableFromDate()!= null ? DateTimeUtil.convertDatetoSqlDate(partUnavailabilityDetail.getUnavailableFromDate()) : new Date(0));
            columnValues.put(++i, partUnavailabilityDetail.getUnavailableFromTime()!= null ? DateTimeUtil.convertTimetoSqlTime(partUnavailabilityDetail.getUnavailableFromTime()) : new Time(0));
            columnValues.put(++i, partUnavailabilityDetail.getUnavailableToDate()!= null ? DateTimeUtil.convertDatetoSqlDate(partUnavailabilityDetail.getUnavailableToDate()) : new Date(0));
            columnValues.put(++i, partUnavailabilityDetail.getUnavailableToTime()!= null ? DateTimeUtil.convertTimetoSqlTime(partUnavailabilityDetail.getUnavailableToTime()) : new Time(0));

            columnValues.put(++i, partUnavailabilityDetail.getId());

            new MySqlWriter().WriteToTable(query, columnValues);
            
            return true;
        } catch (Exception ex)
        {
            LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public boolean addWorkCenterInterruptionDetails(WorkCenterInterruptionsModel workCenterInterruptionDetail, String storageName)
    {
        
        String query = "INSERT INTO " + storageName + " "
                + "(work_center_no, " + "interruption_from_date, " + "interruption_from_time, " + "interruption_to_date, " + "interruption_to_time) "
                + "VALUES "
                + "(?, ?, ?, ?, ? )";
        try
        {

            HashMap<Integer, Object> columnValues = new HashMap<>();
            int i = 0;
            columnValues.put(++i, workCenterInterruptionDetail.getWorkCenterNo());
            columnValues.put(++i, workCenterInterruptionDetail.getInterruptionFromDate()!= null ? DateTimeUtil.convertDatetoSqlDate(workCenterInterruptionDetail.getInterruptionFromDate()) : new Date(0));
            columnValues.put(++i, workCenterInterruptionDetail.getInterruptionFromTime()!= null ? DateTimeUtil.convertTimetoSqlTime(workCenterInterruptionDetail.getInterruptionFromTime()) : new Time(0));
            columnValues.put(++i, workCenterInterruptionDetail.getInterruptionToDate()!= null ? DateTimeUtil.convertDatetoSqlDate(workCenterInterruptionDetail.getInterruptionToDate()) : new Date(0));
            columnValues.put(++i, workCenterInterruptionDetail.getInterruptionToTime()!= null ? DateTimeUtil.convertTimetoSqlTime(workCenterInterruptionDetail.getInterruptionToTime()) : new Time(0));

            new MySqlWriter().WriteToTable(query, columnValues);
            
            return true;
        } catch (Exception ex)
        {
            LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public boolean updateWorkCenterInterruptionDetails(WorkCenterInterruptionsModel workCenterInterruptionDetail, String storageName)
    {
        String query = "UPDATE " + storageName + " "
                    + "SET "
                    + "work_center_no = ?, " 
                    + "interruption_from_date = ?, " 
                    + "interruption_from_time = ?, " 
                    + "interruption_to_date = ?, " 
                    + "interruption_to_time = ? "
                    + "WHERE id = ?";
        try
        {
            HashMap<Integer, Object> columnValues = new HashMap<>();
            int i = 0;
            columnValues.put(++i, workCenterInterruptionDetail.getWorkCenterNo());
            columnValues.put(++i, workCenterInterruptionDetail.getInterruptionFromDate()!= null ? DateTimeUtil.convertDatetoSqlDate(workCenterInterruptionDetail.getInterruptionFromDate()) : new Date(0));
            columnValues.put(++i, workCenterInterruptionDetail.getInterruptionFromTime()!= null ? DateTimeUtil.convertTimetoSqlTime(workCenterInterruptionDetail.getInterruptionFromTime()) : new Time(0));
            columnValues.put(++i, workCenterInterruptionDetail.getInterruptionToDate()!= null ? DateTimeUtil.convertDatetoSqlDate(workCenterInterruptionDetail.getInterruptionToDate()) : new Date(0));
            columnValues.put(++i, workCenterInterruptionDetail.getInterruptionToTime()!= null ? DateTimeUtil.convertTimetoSqlTime(workCenterInterruptionDetail.getInterruptionToTime()) : new Time(0));

            columnValues.put(++i, workCenterInterruptionDetail.getId());

            new MySqlWriter().WriteToTable(query, columnValues);
            
            return true;
        } catch (Exception ex)
        {
            LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public boolean changeOperationStatusToUnschedule(String orderNo)
    {
        ArrayList<Object> parameters = new ArrayList<>();
        String storedProcedure = MySqlUtil.getStoredProcedureName(DataModelEnums.StoredProcedures.ChangeOperationStatusToUnschedule);
        parameters.add(orderNo);
        int result;
        try
        {
            result = new MySqlWriter().invokeUpdateStoredProcedure(storedProcedure, parameters);

        } catch (Exception ex)
        {
            LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
        }
        return true;
    }
    
    
    
    public void UpdateTestColumn()
    {
        try
        {
            String query = "UPDATE gantt_tasks SET test_column = ? WHERE id = 1";
            HashMap<Integer, Object> columnValues = new HashMap<>();

            java.sql.Date date = java.sql.Date.valueOf("2018-08-08");
            columnValues.put(1, date);

            new MySqlWriter().WriteToTable(query, columnValues);

        } catch (Exception ex)
        {
            LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
        }
    }

}
