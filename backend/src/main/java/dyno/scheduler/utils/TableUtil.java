/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dyno.scheduler.utils;

import java.util.ArrayList;

/**
 *
 * @author Prabash
 */
public class TableUtil
{

    /**
     * create a table filter with the given conditions and return an
     * ArrayList<String> filter
     *
     * @param filterColumnName filter column name (ex: "order_no")
     * @param filterExpression filter expression (ex: <, =, >)
     * @param filterCriteria filter criteria (ex: 1, "test")
     * @return filter array list
     */
    public static ArrayList<String> createTableFilter(String filterColumnName, String filterExpression, String filterCriteria)
    {
        ArrayList<String> filter = new ArrayList<>();
        filter.add(filterColumnName);
        filter.add(filterExpression);
        filter.add("'" + filterCriteria + "'");

        return filter;
    }

    /**
     * create the orderBy list by taking a set of orderBy columns
     *
     * @param columnNames set of column names
     * @return order by list
     */
    public static ArrayList<String> createOrderByFilters(String... columnNames)
    {
        ArrayList<String> orderBy = new ArrayList<>();
        for (String columnName : columnNames)
        {
            orderBy.add(columnName);
        }
        return orderBy;
    }
}
