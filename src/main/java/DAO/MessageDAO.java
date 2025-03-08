package DAO;


import Model.Message;
import Util.ConnectionUtil;

import java.sql.*;
import java.util.*;

public class MessageDAO {

    public Message insertMessage(Message message) {
        Connection connection = ConnectionUtil.getConnection();
        try {
            String sql = "insert into message(posted_by, message_text, time_posted_epoch) values(?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);


            preparedStatement.setInt(1, message.getPosted_by());
            preparedStatement.setString(2, message.getMessage_text());
            preparedStatement.setLong(3, message.getTime_posted_epoch());

            preparedStatement.executeUpdate();
            ResultSet pkeyResultSet = preparedStatement.getGeneratedKeys();
            if(pkeyResultSet.next()){
                int generated_account_id = (int) pkeyResultSet.getLong(1);
                return new Message(generated_account_id, message.getPosted_by(), message.getMessage_text(), message.getTime_posted_epoch());
            }

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public List<Message> getAllMessages(){
        Connection connection = ConnectionUtil.getConnection();
        List<Message> messages = new ArrayList<>();
        try{
            String sql = "select * from message";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                Message message = new Message(rs.getInt("message_id"), rs.getInt("posted_by"),rs.getString("message_text"),rs.getLong("time_posted_epoch"));
                messages.add(message);
            }

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return messages;

    }

    public Message deleteMessage(int message_id){
        Connection connection = ConnectionUtil.getConnection();
        Message delMessage = null;
        try{
            String sql = "select * from message where message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, message_id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                delMessage = new Message(message_id,resultSet.getInt("posted_by"),resultSet.getString("message_text"),resultSet.getLong("time_posted_epoch"));
            }

            if(delMessage != null){
                String delSql = "delete from message where id = ?";
                PreparedStatement delPreparedStatement = connection.prepareStatement(delSql);
                delPreparedStatement.setInt(1,message_id);
                delPreparedStatement.executeUpdate();
            }

            
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return delMessage;
    }

    public int editMessage(int message_id, String message_text){
        Connection connection = ConnectionUtil.getConnection();
        int rowsEffected = -1;
        try{
            String sql = "update message set message_text = ? where message_id = ? ";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,message_text);
            preparedStatement.setInt(2,message_id);
            rowsEffected = preparedStatement.executeUpdate();


        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return rowsEffected;


    }



    
}
