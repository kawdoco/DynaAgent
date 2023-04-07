/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dyno.scheduler.data;

import dyno.scheduler.utils.LogUtil;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;

/**
 *
 * @author Prabash
 */
public class MySqlReader
{

    public void testConnection()
    {

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try
        {
            connection = MySqlConnection.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM shop_order_tab");
            while (resultSet.next())
            {
                System.out.printf("%s\t%s\t%s\t%s\t",
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getDate(4));
            }
            

        } catch (SQLException ex)
        {
            LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
        } finally
        {
            try
            {
                if (resultSet != null)
                {
                    resultSet.close();
                }
                if (statement != null)
                {
                    statement.close();
                }

            } catch (SQLException ex)
            {
                LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
            }
        }
    }

    /**
     * Read a given table name with the provided filters and order by columns
     * @param tableName
     * @param filters
     * @param orderBy
     * @return 
     */
    public ResultSet ReadTable(String tableName, ArrayList<ArrayList<String>> filters, ArrayList<String> orderBy)
    {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        CachedRowSet rowset = null;
        
        try
        {
            connection = MySqlConnection.getConnection();
            statement = connection.createStatement();
            
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT * FROM ").append(tableName);
                
            // if there are filters to be added
            if (filters != null && filters.size() > 0)
            {
                int filtersCount = 1;
                queryBuilder.append(" WHERE ");
                for (ArrayList<String> filter : filters)
                {
                    // in the filter arraylist, there are 3 values
                    // 1 - filter ColumnName (ex: operationSequence ), 2 - filter criteria (ex: = , >, <), 3 - filter value (ex: 1)
                    for (String filterValue : filter)
                    {
                        queryBuilder.append(filterValue).append(" ");
                    }
                    
                    // AND should be added only to the middle
                    if (filtersCount < filters.size())
                    {
                        queryBuilder.append("AND ");
                    }
                    filtersCount++;
                }
            }
            
            // if there are orderBy to be added
            if (orderBy != null && orderBy.size()> 0)
            {
                int orderByCount = 1;
                queryBuilder.append(" ORDER BY ");
                for (String orderByColumn : orderBy)
                {
                    queryBuilder.append(orderByColumn);
                    
                    // commas should be added in the middle of each order by clause
                    if (orderByCount < orderBy.size())
                    {
                        queryBuilder.append(" , ");
                    }
                    orderByCount++;
                }
            }
            
            resultSet = statement.executeQuery(queryBuilder.toString());
            RowSetFactory factory = RowSetProvider.newFactory();
            rowset = factory.createCachedRowSet();

            rowset.populate(resultSet);

        } catch (SQLException ex)
        {
            LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
        } finally
        {
            try
            {
                if (resultSet != null)
                {
                    resultSet.close();
                }            
                if (statement != null)
                {
                    statement.close();
                }

            } catch (SQLException ex)
            {
                LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
            }

            return rowset;
        }
    }
    
    public ResultSet invokeGetStoreProcedure(String storedProcedureName, ArrayList<Object> parameters)
    {
        Connection connection;
        CallableStatement statement = null;
        ResultSet resultSet = null;
        CachedRowSet rowset = null;
        
        try
        {
            connection = MySqlConnection.getConnection();
            
            // Start building the query
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("CALL ").append(storedProcedureName);
            
            // Set parameter holders to the query if there are parameters available
            queryBuilder.append("(");
            int noOfParams = parameters.size();
            if (noOfParams > 0)
            {
                for (int i = 1; i <= noOfParams; i++)
                {
                    queryBuilder.append("?");
                    if(i != noOfParams)
                        queryBuilder.append(",");
                }
            }
            queryBuilder.append(")");
            
            // Prepare the statement
            statement = connection.prepareCall(queryBuilder.toString());
            
            // Set parameters to the statement
            for (int i = 1; i <= noOfParams; i++)
            {
                Object parameter = parameters.get(i-1);
                if(parameter instanceof Integer)
                {
                    statement.setInt(i, (int)parameter);
                }
                else if(parameter instanceof Double)
                {
                    statement.setDouble(i, (double)parameter);
                }
                else if(parameter instanceof String)
                {
                    statement.setString(i, (String)parameter);
                }
                else if(parameter instanceof Date)
                {
                    statement.setDate(i, (Date)parameter);
                }
                else if(parameter instanceof Time)
                {
                    statement.setTime(i, (Time)parameter);
                }
            }
            
            resultSet = statement.executeQuery();
            RowSetFactory factory = RowSetProvider.newFactory();
            rowset = factory.createCachedRowSet();

            rowset.populate(resultSet);

        } catch (SQLException ex)
        {
            LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
        } finally
        {
            try
            {
                if (resultSet != null)
                {
                    resultSet.close();
                }            
                if (statement != null)
                {
                    statement.close();
                }

            } catch (SQLException ex)
            {
                LogUtil.logSevereErrorMessage(this, ex.getMessage(), ex);
            }

            return rowset;
        }
    }
}
