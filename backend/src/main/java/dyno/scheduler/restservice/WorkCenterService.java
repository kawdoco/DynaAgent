/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dyno.scheduler.restservice;

import dyno.scheduler.data.DataReader;
import dyno.scheduler.data.DataWriter;
import dyno.scheduler.datamodels.WorkCenterInterruptionsModel;
import dyno.scheduler.datamodels.WorkCenterModel;
import dyno.scheduler.datamodels.WorkCenterUtil;
import dyno.scheduler.utils.DateTimeUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Prabash
 */
@Path("/work-center")
public class WorkCenterService implements IDynoGetService
{

    @Override
    public Response get()
    {
        List<WorkCenterModel> workCenterDetails = DataReader.getWorkCenterDetails(true);

        List<WorkCenterModelJson> list = new ArrayList<>();
        GenericEntity<List<WorkCenterModelJson>> entity;

        for (WorkCenterModel workCenterDetail : workCenterDetails)
        {
            List<WorkCenterInterruptionsModelJson> interruptionDetails = new ArrayList<>();
            for (WorkCenterInterruptionsModel workCenterInterruption : workCenterDetail.getWorkCenterInterruptions())
            {
                WorkCenterInterruptionsModelJson interruptionJsonObj = new WorkCenterInterruptionsModelJson(
                        workCenterInterruption.getId(),
                        workCenterInterruption.getWorkCenterNo(),
                        DateTimeUtil.concatenateDateTime(workCenterInterruption.getInterruptionFromDate(), workCenterInterruption.getInterruptionFromTime()).toString(DateTimeUtil.getDateTimeFormat()),
                        DateTimeUtil.concatenateDateTime(workCenterInterruption.getInterruptionToDate(), workCenterInterruption.getInterruptionToTime()).toString(DateTimeUtil.getDateTimeFormat()));
                interruptionDetails.add(interruptionJsonObj);
            }
            WorkCenterModelJson workCenterJsonObj = new WorkCenterModelJson(
                    workCenterDetail.getId(),
                    workCenterDetail.getWorkCenterNo(),
                    workCenterDetail.getWorkCenterType(),
                    workCenterDetail.getWorkCenterDescription(),
                    workCenterDetail.getWorkCenterCapacity(),
                    interruptionDetails);

            list.add(workCenterJsonObj);
        }

        entity = new GenericEntityImpl(list);
        return Response.ok(entity).build();
    }

    private static class GenericEntityImpl extends GenericEntity<List<WorkCenterModelJson>>
    {

        public GenericEntityImpl(List<WorkCenterModelJson> entity)
        {
            super(entity);
        }
    }

    @POST
    @Path("/add-wc")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addWorkCenter(WorkCenterModelJson workCenterJson)
    {
        WorkCenterModel workCenter = new WorkCenterModel();
        workCenter.setWorkCenterNo(workCenterJson.workCenterNo);
        workCenter.setWorkCenterType(workCenterJson.workCenterType);
        workCenter.setWorkCenterDescription(workCenterJson.workCenterDescription);
        workCenter.setWorkCenterCapacity(workCenterJson.workCenterCapacity);

        DataWriter.addWorkCenter(workCenter);

        return Response.status(200).entity("Successfully Added").build();
    }

    @POST
    @Path("/update-wc")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateWorkCenter(WorkCenterModelJson workCenterJson)
    {
        WorkCenterModel workCenter = new WorkCenterModel();
        workCenter.setId(workCenterJson.id);
        workCenter.setWorkCenterNo(workCenterJson.workCenterNo);
        workCenter.setWorkCenterType(workCenterJson.workCenterType);
        workCenter.setWorkCenterDescription(workCenterJson.workCenterDescription);
        workCenter.setWorkCenterCapacity(workCenterJson.workCenterCapacity);

        DataWriter.updateWorkCenter(workCenter);

        return Response.status(200).entity("Successfully Updated").build();
    }

    @POST
    @Path("/interrupt")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response interruptWorkCenter(WorkCenterInterruptionsModelJson wcInterruptionJson)
    {
        WorkCenterInterruptionsModel workCenterInterruption = new WorkCenterInterruptionsModel();
        workCenterInterruption.setWorkCenterNo(wcInterruptionJson.workCenterNo);
        workCenterInterruption.setInterruptionFromDate(DateTimeUtil.convertJsonDateTimeToDateTime(wcInterruptionJson.interruptionFromDateTime));
        workCenterInterruption.setInterruptionFromTime(DateTimeUtil.convertJsonDateTimeToDateTime(wcInterruptionJson.interruptionFromDateTime));
        workCenterInterruption.setInterruptionToDate(DateTimeUtil.convertJsonDateTimeToDateTime(wcInterruptionJson.interruptionToDateTime));
        workCenterInterruption.setInterruptionToTime(DateTimeUtil.convertJsonDateTimeToDateTime(wcInterruptionJson.interruptionToDateTime));

        DataWriter.addWCInterruptionDetails(workCenterInterruption);
        HashMap<String, List<Integer>> affectedOrders = WorkCenterUtil.interruptWorkCenter(
                DateTimeUtil.convertJsonDateTimeToDateTime(wcInterruptionJson.interruptionFromDateTime),
                DateTimeUtil.convertJsonDateTimeToDateTime(wcInterruptionJson.interruptionToDateTime),
                wcInterruptionJson.workCenterNo);

        
        return Response.status(200).entity("Successfully Interrupted Work Center - " + wcInterruptionJson.workCenterNo + ". \nAffected Operations are: "
                + getAffectedOrderDetails(affectedOrders)).build();
    }

    public static String getWorkCentersQueryString(List<WorkCenterModel> workCenterDetails)
    {
        StringBuilder workCenters = new StringBuilder();
        for (int i = 1; i <= workCenterDetails.size(); i++)
        {
            workCenters.append(workCenterDetails.get(i - 1).getId());
            if (i < workCenterDetails.size())
            {
                workCenters.append(",");
            }
        }

        return workCenters.toString();
    }
    
    public static String getAffectedOrderDetails(HashMap<String, List<Integer>> affectedOrders)
    {
        StringBuilder affectedOrderDetails = new StringBuilder();
        for (Map.Entry<String, List<Integer>> affectedOrder : affectedOrders.entrySet())
        {
            affectedOrderDetails.append(" Order " + affectedOrder.getKey() + ":");
            affectedOrderDetails.append(" Operation IDs: ");
            for (int i = 0; i < affectedOrder.getValue().size(); i++)
            {
                if (i + 1 < affectedOrder.getValue().size())
                {
                    affectedOrderDetails.append(affectedOrder.getValue().get(i) + ", ");
                } else
                {
                    affectedOrderDetails.append(affectedOrder.getValue().get(i));
                }
            }
            affectedOrderDetails.append("\n");
        }
        return affectedOrderDetails.toString();
    }
}

@XmlRootElement
class WorkCenterModelJson
{

    public int id;
    public String workCenterNo;
    public String workCenterType;
    public String workCenterDescription;
    public String workCenterCapacity;
    public List<WorkCenterInterruptionsModelJson> workCenterInterruptions;

    public WorkCenterModelJson()
    {
    }

    public WorkCenterModelJson(int id, String workCenterNo, String workCenterType, String workCenterDescription, String workCenterCapacity, List<WorkCenterInterruptionsModelJson> workCenterInterruptions)
    {
        this.id = id;
        this.workCenterNo = workCenterNo;
        this.workCenterType = workCenterType;
        this.workCenterDescription = workCenterDescription;
        this.workCenterCapacity = workCenterCapacity;
        this.workCenterInterruptions = workCenterInterruptions;
    }
}

@XmlRootElement
class WorkCenterInterruptionsModelJson
{

    public int id;
    public String workCenterNo;
    public String interruptionFromDateTime;
    public String interruptionToDateTime;

    public WorkCenterInterruptionsModelJson()
    {
    }

    public WorkCenterInterruptionsModelJson(int id, String workCenterNo, String interruptionFromDateTime, String interruptionToDateTime)
    {
        this.id = id;
        this.workCenterNo = workCenterNo;
        this.interruptionFromDateTime = interruptionFromDateTime;
        this.interruptionToDateTime = interruptionToDateTime;
    }
}
