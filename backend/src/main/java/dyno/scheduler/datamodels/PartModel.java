/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dyno.scheduler.datamodels;

import dyno.scheduler.utils.DateTimeUtil;
import dyno.scheduler.utils.LogUtil;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.joda.time.DateTime;

/**
 *
 * @author Prabash
 */
public class PartModel extends DataModel
{
    // <editor-fold defaultstate="collapsed" desc="properties">
    
    private int id;
    private String partNo; 
    private String partDescription;
    private String vendor;
    private List<PartUnavailabilityModel> partUnavailabilityDetails;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="constructors"> 

    public PartModel(int id, String partNo, String partDescription, String vendor, List<PartUnavailabilityModel> partUnavailabilityDetails)
    {
        this.id = id;
        this.partNo = partNo;
        this.partDescription = partDescription;
        this.vendor = vendor;
        this.partUnavailabilityDetails = partUnavailabilityDetails;
    }
    
    public PartModel()
    {
        partUnavailabilityDetails = new ArrayList<>();
    }
    
    // </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getters/setters">

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getPartNo()
    {
        return partNo;
    }

    public void setPartNo(String partNo)
    {
        this.partNo = partNo;
    }

    public String getPartDescription()
    {
        return partDescription;
    }

    public void setPartDescription(String partDescription)
    {
        this.partDescription = partDescription;
    }

    public String getVendor()
    {
        return vendor;
    }

    public void setVendor(String vendor)
    {
        this.vendor = vendor;
    }

    public List<PartUnavailabilityModel> getPartUnavailabilityDetails()
    {
        return partUnavailabilityDetails;
    }

    public void setPartUnavailabilityDetails(List<PartUnavailabilityModel> partUnavailabilityDetails)
    {
        this.partUnavailabilityDetails = partUnavailabilityDetails;
    }
    
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="overriden methods"> 
    
    @Override
    public PartModel getModelObject(Object row)
    {
        if (row instanceof Row)
        {
        }
        else
        {
            ResultSet resultSetRow = (ResultSet) row;
            int i = 0;
            try
            {
                this.setId(resultSetRow.getInt(++i));
                this.setPartNo(resultSetRow.getString(++i));
                this.setPartDescription(resultSetRow.getString(++i));
                this.setVendor(resultSetRow.getString(++i));
                
            } catch (SQLException ex)
            {
                LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
            }
            
        }
        return this;
    }

    @Override
    public String getPrimaryKey()
    {
        return String.valueOf(this.getId());
    }

    @Override
    public String getClassName()
    {
        return PartModel.class.getName();
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="custom methods">
    
    public boolean checkPartAvailability(DateTime checkFromDateTime, int workCenterRuntime)
    {
        boolean partAvailable = true;
        
        DateTime checkToDateTime = WorkCenterOpAllocModel.incrementTime(checkFromDateTime, workCenterRuntime);
        
        for (PartUnavailabilityModel partUnavailabilityDetail : this.getPartUnavailabilityDetails())
        {
            DateTime unavailableFrom = DateTimeUtil.concatenateDateTime(partUnavailabilityDetail.getUnavailableFromDate(), partUnavailabilityDetail.getUnavailableFromTime());
            DateTime unavailableTo = DateTimeUtil.concatenateDateTime(partUnavailabilityDetail.getUnavailableToDate(), partUnavailabilityDetail.getUnavailableToTime());
            
            // check if the from date time and the todateTime overlaps with any of the unavailable times, and if so exit the loop and return false
            if (((unavailableFrom.isBefore(checkFromDateTime) || unavailableFrom.isEqual(checkFromDateTime)) && (unavailableTo.isAfter(checkFromDateTime) && (unavailableTo.isBefore(checkToDateTime) || unavailableTo.isEqual(checkToDateTime)))) || 
                 (unavailableFrom.isBefore(checkFromDateTime) && unavailableTo.isAfter(checkToDateTime)) || 
                 (((unavailableFrom.isEqual(checkFromDateTime) || unavailableFrom.isAfter(checkFromDateTime)) && (unavailableFrom.isBefore(checkToDateTime) || unavailableFrom.isEqual(checkToDateTime))) && (unavailableTo.isEqual(checkToDateTime) || unavailableTo.isAfter(checkToDateTime))))
            {
                partAvailable = false;
                break;
            }
        }
        return partAvailable;
    }
    
    //</editor-fold>
}
