/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dyno.scheduler.utils;

import dyno.scheduler.datamodels.DataModelEnums;

/**
 *
 * @author Prabash
 */
public class ExcelUtil
{
    public static String getStorageName(DataModelEnums.DataModelType dataModel)
    {
        switch (dataModel)
        {
            case ShopOrder:
                return "ShopOrders";
            case ShopOrderOperation:
                return "ShopOrderOperations";
            case WorkCenter:
                return "WorkCenters";
            case WorkCenterAllocationFinite:
                return "WorkCenterOpAllocations";
            default:
                return "";
        }
    }
}
