/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dyno.scheduler.restservice;

import dyno.scheduler.data.DataReader;
import dyno.scheduler.data.DataWriter;
import dyno.scheduler.datamodels.PartModel;
import dyno.scheduler.datamodels.PartUnavailabilityModel;
import dyno.scheduler.datamodels.WorkCenterUtil;
import static dyno.scheduler.restservice.WorkCenterService.getAffectedOrderDetails;
import dyno.scheduler.utils.DateTimeUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
@Path("/part-details")
public class PartService implements IDynoGetService
{
    @Override
    public Response get()
    {
        List<PartModel> partDetails = DataReader.getPartDetails();
        
        List<PartModelJson> list = new ArrayList<>();
        GenericEntity<List<PartModelJson>> entity;
        
        for (PartModel partDetail : partDetails)
        {
            List<PartUnavailabilityModelJson> unavailabilityDetails = new ArrayList<>();
            for (PartUnavailabilityModel partUnavailability : partDetail.getPartUnavailabilityDetails())
            {
                PartUnavailabilityModelJson unavailabilityJsonObj = new PartUnavailabilityModelJson(
                        partUnavailability.getId(), 
                        partUnavailability.getPartNo(), 
                        DateTimeUtil.concatenateDateTime(partUnavailability.getUnavailableFromDate(), partUnavailability.getUnavailableFromTime()).toString(DateTimeUtil.getDateTimeFormat()),
                        DateTimeUtil.concatenateDateTime(partUnavailability.getUnavailableToDate(), partUnavailability.getUnavailableToTime()).toString(DateTimeUtil.getDateTimeFormat()));
                unavailabilityDetails.add(unavailabilityJsonObj);
            }
            PartModelJson partModelJsonObj = new PartModelJson(
                    partDetail.getId(), 
                    partDetail.getPartNo(), 
                    partDetail.getPartDescription(), 
                    partDetail.getVendor(), 
                    unavailabilityDetails);
            
            list.add(partModelJsonObj);
        }
        
        entity = new GenericEntityImpl(list);
        return Response.ok(entity).build();
    }
    
    private static class GenericEntityImpl extends GenericEntity<List<PartModelJson>>
    {
        public GenericEntityImpl(List<PartModelJson> entity)
        {
            super(entity);
        }
    }
    
    @POST
    @Path("/add-part")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addPart(PartModelJson partDetailsJson)
    {
        PartModel partDetail = new PartModel();
        partDetail.setPartNo(partDetailsJson.partNo);
        partDetail.setPartDescription(partDetailsJson.partDescription);
        partDetail.setVendor(partDetailsJson.vendor);
        
        DataWriter.addPartDetails(partDetail);
        
        return Response.status(200).entity("Successfully Added").build();
    }
    
    
    @POST
    @Path("/update-part")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePart(PartModelJson partDetailsJson)
    {
        PartModel partDetail = new PartModel();
        partDetail.setId(partDetailsJson.id);
        partDetail.setPartNo(partDetailsJson.partNo);
        partDetail.setPartDescription(partDetailsJson.partDescription);
        partDetail.setVendor(partDetailsJson.vendor);
        
        DataWriter.updatePartDetails(partDetail);
        
        return Response.status(200).entity("Successfully Updated").build();
    }
    
    @POST
    @Path("/add-part-unavailability")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addPartUnavailability(PartUnavailabilityModelJson partUnavailabilityJson)
    {
        PartUnavailabilityModel partUnavailability = new PartUnavailabilityModel();
        partUnavailability.setPartNo(partUnavailabilityJson.partNo);
        partUnavailability.setUnavailableFromDate(DateTimeUtil.convertJsonDateTimeToDateTime(partUnavailabilityJson.unavailableFromDateTime));
        partUnavailability.setUnavailableFromTime(DateTimeUtil.convertJsonDateTimeToDateTime(partUnavailabilityJson.unavailableFromDateTime));
        partUnavailability.setUnavailableToDate(DateTimeUtil.convertJsonDateTimeToDateTime(partUnavailabilityJson.unavailableToDateTime));
        partUnavailability.setUnavailableToTime(DateTimeUtil.convertJsonDateTimeToDateTime(partUnavailabilityJson.unavailableToDateTime));
        
        DataWriter.addPartUnavailabilityDetails(partUnavailability);
        HashMap<String, List<Integer>> affectedOrders = WorkCenterUtil.interruptWorkCenterOnPartUnavailability(
                DateTimeUtil.convertJsonDateTimeToDateTime(partUnavailabilityJson.unavailableFromDateTime), 
                DateTimeUtil.convertJsonDateTimeToDateTime(partUnavailabilityJson.unavailableToDateTime), 
                partUnavailabilityJson.partNo);
        
        return Response.status(200).entity("Successfully Updated Part Unavailability Details. \nAffected Orders are: "
                + getAffectedOrderDetails(affectedOrders)).build();
    }
}

@XmlRootElement
class PartModelJson
{
    public int id;
    public String partNo; 
    public String partDescription;
    public String vendor;
    public List<PartUnavailabilityModelJson> partUnavailabilityDetails;

    public PartModelJson()
    {
    }

    public PartModelJson(int id, String partNo, String partDescription, String vendor, List<PartUnavailabilityModelJson> partUnavailabilityDetails)
    {
        this.id = id;
        this.partNo = partNo;
        this.partDescription = partDescription;
        this.vendor = vendor;
        this.partUnavailabilityDetails = partUnavailabilityDetails;
    }
}

@XmlRootElement
class PartUnavailabilityModelJson
{
    public int id;
    public String partNo; 
    public String unavailableFromDateTime;
    public String unavailableToDateTime;

    public PartUnavailabilityModelJson()
    {
    }

    public PartUnavailabilityModelJson(int id, String partNo, String unavailableFromDateTime, String unavailableToDateTime)
    {
        this.id = id;
        this.partNo = partNo;
        this.unavailableFromDateTime = unavailableFromDateTime;
        this.unavailableToDateTime = unavailableToDateTime;
    }
}