/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dyno.scheduler.restservice;

import dyno.scheduler.data.DataReader;
import dyno.scheduler.data.DataWriter;
import dyno.scheduler.datamodels.DataModelEnums;
import dyno.scheduler.datamodels.ShopOrderModel;
import dyno.scheduler.datamodels.ShopOrderOperationModel;
import dyno.scheduler.datamodels.WorkCenterModel;
import dyno.scheduler.utils.DateTimeUtil;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;
import org.joda.time.DateTime;

/**
 *
 * @author Prabash
 */
@Path("/shop-order")
public class ShopOrderService implements IDynoGetService
{

    @Override
    public Response get()
    {
        List<ShopOrderModel> shopOrders = DataReader.getShopOrderDetails(true);

        List<ShopOrderModelJson> list = getShopOrdersJsonList(shopOrders);
        GenericEntity<List<ShopOrderModelJson>> entity;

        entity = new GenericEntityImpl(list);
        return Response.ok(entity).build();
    }

    private static List<ShopOrderModelJson> getShopOrdersJsonList(List<ShopOrderModel> shopOrders)
    {
        List<ShopOrderModelJson> list = new ArrayList<>();
        for (ShopOrderModel shopOrder : shopOrders)
        {
            List<ShopOrderOperationModelJson> operations = new ArrayList<ShopOrderOperationModelJson>();
            for (ShopOrderOperationModel operation : shopOrder.getOperations())
            {

                //ShopOrderOperationModelJson operation = new ShopOrderOperationModelJson("S01", 1, 1, "WC1", "Milling", "Test", 0, 1, 0, DateTime.now(), DateTime.now(), DateTime.now().plusDays(2), DateTime.now().plusDays(2), 0, DataModelEnums.OperationStatus.Created);
                DateTime opStartDateTime = null;
                DateTime opFinishDateTime = null;

                if (operation.getOpStartDate() != null && operation.getOpStartTime() != null)
                {
                    opStartDateTime = DateTimeUtil.concatenateDateTime(operation.getOpStartDate(), operation.getOpStartTime());
                }
                if (operation.getOpFinishDate() != null && operation.getOpFinishTime() != null)
                {
                    opFinishDateTime = DateTimeUtil.concatenateDateTime(operation.getOpFinishDate(), operation.getOpFinishTime());
                }

                ShopOrderOperationModelJson shopOrderOpJsonObj = new ShopOrderOperationModelJson(
                        operation.getOrderNo(),
                        operation.getOperationId(),
                        operation.getOperationNo(),
                        operation.getWorkCenterNo(),
                        operation.getWorkCenterType(),
                        operation.getOperationDescription(),
                        operation.getOperationSequence(),
                        operation.getPrecedingOperationId(),
                        operation.getWorkCenterRuntimeFactor(),
                        operation.getWorkCenterRuntime(),
                        operation.getLaborRuntimeFactor(),
                        operation.getLaborRunTime(),
                        opStartDateTime != null ? opStartDateTime.toString(DateTimeUtil.getDateTimeFormatJson()) : "",
                        opFinishDateTime != null ? opFinishDateTime.toString(DateTimeUtil.getDateTimeFormatJson()) : "",
                        operation.getQuantity(),
                        operation.getOperationStatus());
                operations.add(shopOrderOpJsonObj);
            }

            ShopOrderModelJson shopOrderJsonObj = new ShopOrderModelJson(
                    shopOrder.getId(),
                    shopOrder.getOrderNo(),
                    shopOrder.getDescription(),
                    shopOrder.getCreatedDate() != null ? shopOrder.getCreatedDate().toString(DateTimeUtil.getDateTimeFormatJson()) : "",
                    shopOrder.getPartNo(),
                    shopOrder.getStructureRevision(),
                    shopOrder.getRoutingRevision(),
                    shopOrder.getRequiredDate() != null ? shopOrder.getRequiredDate().toString(DateTimeUtil.getDateTimeFormatJson()) : "",
                    shopOrder.getStartDate() != null ? shopOrder.getStartDate().toString(DateTimeUtil.getDateTimeFormatJson()) : "",
                    shopOrder.getFinishDate() != null ? shopOrder.getFinishDate().toString(DateTimeUtil.getDateTimeFormatJson()) : "",
                    shopOrder.getSchedulingDirection().toString(),
                    shopOrder.getCustomerNo(),
                    shopOrder.getSchedulingStatus().toString(),
                    shopOrder.getShopOrderStatus().toString(),
                    shopOrder.getPriority().toString(),
                    shopOrder.getRevenueValue(),
                    operations);

            list.add(shopOrderJsonObj);
        }

        return list;
    }

    private static class GenericEntityImpl extends GenericEntity<List<ShopOrderModelJson>>
    {

        public GenericEntityImpl(List<ShopOrderModelJson> entity)
        {
            super(entity);
        }
    }
    
    @GET
    @Path("get-scheduled-orders/{skip}/{take}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getScheduledOrders(@PathParam("skip") int skip,
                                       @PathParam("take") int take)
    {
        List<ShopOrderModel> shopOrders = DataReader.getScheduledOrders(skip, take);

        List<ShopOrderModelJson> list = getShopOrdersJsonList(shopOrders);
        GenericEntity<List<ShopOrderModelJson>> entity;

        entity = new GenericEntityImpl(list);
        return Response.ok(entity).build();
    }

    @POST
    @Path("/add-shop-order")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addShopOrder(ShopOrderModelJson shopOrderJson)
    {
        ShopOrderModel shopOrder = new ShopOrderModel();
        shopOrder.setOrderNo(shopOrderJson.orderNo);
        shopOrder.setDescription(shopOrderJson.description);
        shopOrder.setCreatedDate(DateTime.now());
        shopOrder.setPartNo(shopOrderJson.partNo);
        shopOrder.setStructureRevision(shopOrderJson.structureRevision);
        shopOrder.setRoutingRevision(shopOrderJson.routingRevision);
        shopOrder.setRequiredDate(DateTimeUtil.convertStringDateToDateTime(shopOrderJson.requiredDate));
        shopOrder.setStartDate(new DateTime());
        shopOrder.setFinishDate(new DateTime());
        shopOrder.setSchedulingDirection(DataModelEnums.ShopOrderSchedulingDirection.valueOf(shopOrderJson.schedulingDirection));
        shopOrder.setCustomerNo(shopOrderJson.customerNo);
        shopOrder.setSchedulingStatus(DataModelEnums.ShopOrderScheduleStatus.Unscheduled);
        shopOrder.setShopOrderStatus(DataModelEnums.ShopOrderStatus.Created);
        shopOrder.setPriority(DataModelEnums.ShopOrderPriority.valueOf(shopOrderJson.priority));
        shopOrder.setRevenueValue(shopOrderJson.revenueValue);

        DataWriter.addShopOrder(shopOrder);

        return Response.status(200).entity("Successfully Added").build();
    }

    @POST
    @Path("/update-shop-order")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateShopOrder(ShopOrderModelJson shopOrderJson)
    {
        ShopOrderModel shopOrder = new ShopOrderModel();
        shopOrder.setId(shopOrderJson.id); // ID added for update only
        shopOrder.setOrderNo(shopOrderJson.orderNo);
        shopOrder.setDescription(shopOrderJson.description);
        shopOrder.setPartNo(shopOrderJson.partNo);
        shopOrder.setStructureRevision(shopOrderJson.structureRevision);
        shopOrder.setRoutingRevision(shopOrderJson.routingRevision);
        shopOrder.setRequiredDate(DateTimeUtil.convertStringDateToDateTime(shopOrderJson.requiredDate));
        shopOrder.setStartDate(DateTimeUtil.convertStringDateToDateTime(shopOrderJson.startDate));
        shopOrder.setFinishDate(DateTimeUtil.convertStringDateToDateTime(shopOrderJson.finishDate));
        shopOrder.setSchedulingDirection(DataModelEnums.ShopOrderSchedulingDirection.valueOf(shopOrderJson.schedulingDirection));
        shopOrder.setCustomerNo(shopOrderJson.customerNo);
        shopOrder.setSchedulingStatus(DataModelEnums.ShopOrderScheduleStatus.valueOf(shopOrderJson.schedulingStatus));
        shopOrder.setShopOrderStatus(DataModelEnums.ShopOrderStatus.valueOf(shopOrderJson.shopOrderStatus));
        shopOrder.setPriority(DataModelEnums.ShopOrderPriority.valueOf(shopOrderJson.priority));
        shopOrder.setRevenueValue(shopOrderJson.revenueValue);

        DataWriter.updateShopOrder(shopOrder);

        return Response.status(200).entity("Successfully Updated").build();
    }

    @POST
    @Path("/add-operation")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addShopOrderOperation(ShopOrderOperationModelJson shopOrderOperationJson)
    {
        ShopOrderOperationModel shopOrderOperation = new ShopOrderOperationModel();
        shopOrderOperation.setOrderNo(shopOrderOperationJson.orderNo);
        shopOrderOperation.setOperationNo(shopOrderOperationJson.operationNo);
        shopOrderOperation.setWorkCenterNo(shopOrderOperationJson.workCenterNo);
        shopOrderOperation.setWorkCenterType(shopOrderOperationJson.workCenterType);
        shopOrderOperation.setOperationDescription(shopOrderOperationJson.operationDescription);
        shopOrderOperation.setOperationSequence(shopOrderOperationJson.operationSequence);
        shopOrderOperation.setPrecedingOperationId(shopOrderOperationJson.precedingOperationId);
        shopOrderOperation.setWorkCenterRuntimeFactor(shopOrderOperationJson.workCenterRuntimeFactor);
        shopOrderOperation.setWorkCenterRuntime(shopOrderOperationJson.workCenterRuntime);
        shopOrderOperation.setLaborRuntimeFactor(shopOrderOperationJson.laborRuntimeFactor);
        shopOrderOperation.setLaborRunTime(shopOrderOperationJson.laborRunTime);
        shopOrderOperation.setOpStartDate(null);
        shopOrderOperation.setOpStartTime(null);
        shopOrderOperation.setOpFinishDate(null);
        shopOrderOperation.setOpFinishTime(null);
        shopOrderOperation.setQuantity(shopOrderOperationJson.quantity);
        shopOrderOperation.setOperationStatus(DataModelEnums.OperationStatus.Created);

        DataWriter.addShopOrderOperation(shopOrderOperation);

        return Response.status(200).entity("Successfully Added").build();
    }

    @POST
    @Path("/update-operation")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatehopOrderOperation(ShopOrderOperationModelJson shopOrderOperationJson)
    {
        ShopOrderOperationModel shopOrderOperation = new ShopOrderOperationModel();
        shopOrderOperation.setOperationId(shopOrderOperationJson.operationId);  // ID added for update only
        shopOrderOperation.setOrderNo(shopOrderOperationJson.orderNo);
        shopOrderOperation.setOperationNo(shopOrderOperationJson.operationNo);
        shopOrderOperation.setWorkCenterNo(shopOrderOperationJson.workCenterNo);
        shopOrderOperation.setWorkCenterType(shopOrderOperationJson.workCenterType);
        shopOrderOperation.setOperationDescription(shopOrderOperationJson.operationDescription);
        shopOrderOperation.setOperationSequence(shopOrderOperationJson.operationSequence);
        shopOrderOperation.setPrecedingOperationId(shopOrderOperationJson.precedingOperationId);
        shopOrderOperation.setWorkCenterRuntimeFactor(shopOrderOperationJson.workCenterRuntimeFactor);
        shopOrderOperation.setWorkCenterRuntime(shopOrderOperationJson.workCenterRuntime);
        shopOrderOperation.setLaborRuntimeFactor(shopOrderOperationJson.laborRuntimeFactor);
        shopOrderOperation.setLaborRunTime(shopOrderOperationJson.laborRunTime);
        shopOrderOperation.setOpStartDate(DateTimeUtil.convertJsonDateTimeToDateTime(shopOrderOperationJson.opStartDateTime));
        shopOrderOperation.setOpStartTime(DateTimeUtil.convertJsonDateTimeToDateTime(shopOrderOperationJson.opStartDateTime));
        shopOrderOperation.setOpFinishDate(DateTimeUtil.convertJsonDateTimeToDateTime(shopOrderOperationJson.opFinishDateTime));
        shopOrderOperation.setOpFinishTime(DateTimeUtil.convertJsonDateTimeToDateTime(shopOrderOperationJson.opFinishDateTime));
        shopOrderOperation.setQuantity(shopOrderOperationJson.quantity);
        shopOrderOperation.setOperationStatus(DataModelEnums.OperationStatus.valueOf(shopOrderOperationJson.operationStatus));

        DataWriter.updateShopOrderOperation(shopOrderOperation);

        return Response.status(200).entity("Successfully Updated").build();
    }

    @POST
    @Path("/unschedule-op-status")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changeOperationStatusToUnschedule(ShopOrderModelJson shopOrderJson)
    {
        DataWriter.changeOpStatusToUnschedule(shopOrderJson.orderNo);
        return Response.status(200).entity("Successfully Updated Status to Unschedule").build();
    }
    
    
    @GET
    @Path("/get-scheduled-orders-by-wc/{skip}/{take}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getScheduledOrdersByWorkCenters(@PathParam("skip") int skip,
                                                    @PathParam("take") int take)
    {
        List<WorkCenterModel> workCenterDetails = DataReader.getWorkCenterDetails(skip, take);
        String workCenters = WorkCenterService.getWorkCentersQueryString(workCenterDetails);
        List<ShopOrderModel> shopOrders = DataReader.getScheduledOrdersByWorkCentre(workCenters);
        
        List<ShopOrderModelJson> list = getShopOrdersJsonList(shopOrders);
        GenericEntity<List<ShopOrderModelJson>> entity;

        entity = new GenericEntityImpl(list);
        return Response.ok(entity).build();
    }
    
    @GET
    @Path("cancel/{orderNo}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response cancelShopOrder(@PathParam("orderNo") String orderNo)
    {
        ShopOrderModel shopOrder = DataReader.getShopOrderByPrimaryKey(orderNo);
        shopOrder.cancel();
        return Response.status(200).entity("Successfully Cancelled order " + orderNo).build();
    }
}

@XmlRootElement
class ShopOrderModelJson
{

    public int id;
    public String orderNo;
    public String description;
    public String createdDate;
    public String partNo;
    public String structureRevision;
    public String routingRevision;
    public String requiredDate;
    public String startDate;
    public String finishDate;
    public String schedulingDirection;
    public String customerNo;
    public String schedulingStatus;
    public String shopOrderStatus;
    public String priority;
    public int revenueValue;
    public List<ShopOrderOperationModelJson> operations;

    public ShopOrderModelJson(int id, String orderNo, String description, String createdDate, String partNo, String structureRevision, String routingRevision, String requiredDate,
            String startDate, String finishDate, String schedulingDirection, String customerNo, String schedulingStatus, String shopOrderStatus, String priority, int revenueValue, List<ShopOrderOperationModelJson> operations)
    {
        this.id = id;
        this.orderNo = orderNo;
        this.description = description;
        this.createdDate = createdDate;
        this.partNo = partNo;
        this.structureRevision = structureRevision;
        this.routingRevision = routingRevision;
        this.requiredDate = requiredDate;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.schedulingDirection = schedulingDirection;
        this.customerNo = customerNo;
        this.schedulingStatus = schedulingStatus;
        this.shopOrderStatus = shopOrderStatus;
        this.priority = priority;
        this.revenueValue = revenueValue;
        this.operations = operations;
    }

    public ShopOrderModelJson()
    {
    }
}

@XmlRootElement
class ShopOrderOperationModelJson
{

    public String orderNo;
    public int operationId;
    public int operationNo;
    public String workCenterNo;
    public String workCenterType;
    public String operationDescription;
    public double operationSequence;
    public int precedingOperationId;
    public int workCenterRuntimeFactor;
    public int workCenterRuntime;
    public int laborRuntimeFactor;
    public int laborRunTime;
    public String opStartDateTime;
    public String opFinishDateTime;
    public String latestOpFinishDate;
    public String latestOpFinishTime;
    public int quantity;
    public String operationStatus;

    public ShopOrderOperationModelJson()
    {
    }

    public ShopOrderOperationModelJson(String orderNo, int operationId, int operationNo, String workCenterNo, String workCenterType, String operationDescription, double operationSequence, int precedingOperationId,
            int workCenterRuntimeFactor, int workCenterRunTime, int laborRuntimeFactor, int laborRunTime, String opStartDateTime, String opFinishDateTime, int quantity, DataModelEnums.OperationStatus operationStatus)
    {
        this.orderNo = orderNo;
        this.operationId = operationId;
        this.operationNo = operationNo;
        this.workCenterNo = workCenterNo;
        this.workCenterType = workCenterType;
        this.operationDescription = operationDescription;
        this.operationSequence = operationSequence;
        this.precedingOperationId = precedingOperationId;
        this.workCenterRuntimeFactor = workCenterRuntimeFactor;
        this.workCenterRuntime = workCenterRunTime;
        this.laborRuntimeFactor = laborRuntimeFactor;
        this.laborRunTime = laborRunTime;
        this.opStartDateTime = opStartDateTime;
        this.opFinishDateTime = opFinishDateTime;
        this.quantity = quantity;
        this.operationStatus = operationStatus.toString();
    }
}
