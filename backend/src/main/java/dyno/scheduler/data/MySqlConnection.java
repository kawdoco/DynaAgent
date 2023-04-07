/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dyno.scheduler.data;

import dyno.scheduler.utils.LogUtil;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Prabash
 */
public class MySqlConnection
{
    private static Connection myConnection;

    private static void init()
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            myConnection = DriverManager.getConnection(
                    "jdbc:mysql://mysql-instance.ceoqgnzkwvd7.ap-south-1.rds.amazonaws.com:3306/dynoschedule_test?zeroDateTimeBehavior=convertToNull", "mysqladmin", "mysqlpass"
            );
        } catch (ClassNotFoundException | SQLException ex)
        {
            LogUtil.logSevereErrorMessage(MySqlConnection.class, ex.getMessage(), ex);
        }
    }

    public static Connection getConnection()
    {
        if (myConnection == null)
        {
            init();
        }
        return myConnection;
    }

    public static void close(ResultSet rs)
    {

        if (rs != null)
        {
            try
            {
                rs.close();
            } catch (SQLException ex)
            {
                LogUtil.logSevereErrorMessage(MySqlConnection.class, ex.getMessage(), ex);
            }

        }
    }

    public static void close(java.sql.Statement stmt)
    {

        if (stmt != null)
        {
            try
            {
                stmt.close();
            } catch (SQLException ex)
            {
                LogUtil.logSevereErrorMessage(MySqlConnection.class, ex.getMessage(), ex);
            }

        }
    }

    public static void destroy()
    {

        if (myConnection != null)
        {

            try
            {
                myConnection.close();
            } catch (SQLException ex)
            {
                LogUtil.logSevereErrorMessage(MySqlConnection.class, ex.getMessage(), ex);
            }

        }
    }
}
