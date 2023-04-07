/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dyno.scheduler.utils;

/**
 *
 * @author prabash.dharshanapri
 */
public class StringUtil
{
    private final static String DELIMITER = "|";
    private final static String DELIMITER_REGEX = "\\|";
    
    /**
     * generate message to be passed between Agents by taking a set of objects
     * @param values set of values to be passed
     * @return generated message
     */
    public static String generateMessageContent(String... values)
    {
        return String.join(DELIMITER, values);
    }
    
    /**
     * get message content as an array of strings by splitting the received message
     * @param message message received
     * @return message content array
     */
    public static String[] readMessageContent(String message)
    {
        return message.split(DELIMITER_REGEX);
    }
}
