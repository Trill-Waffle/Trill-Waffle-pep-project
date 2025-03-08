package Service;

import java.util.List;

import DAO.MessageDAO;
import Model.Message;

public class MessageService {
    private MessageDAO messageDAO;

    public MessageService(){
        messageDAO = new MessageDAO();
        
    }

    public MessageService(MessageDAO messageDAO){
        this.messageDAO = messageDAO;
    }

    public Message addMessage(Message message){
        return this.messageDAO.insertMessage(message);
    }

    public List<Message> getAllMessages(){
        return this.messageDAO.getAllMessages();
        
    }

    public Message deleteMessage(int message_id){
        return this.messageDAO.deleteMessage(message_id);
    }

    public int editMessage(int message_id, String message_text){
        return this.messageDAO.editMessage(message_id, message_text);
    }
    
}
