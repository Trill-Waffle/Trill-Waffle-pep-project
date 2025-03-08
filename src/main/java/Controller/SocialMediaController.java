package Controller;

import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Model.Account;
import Model.Message;
import Model.MiniMessage;
import Service.AccountService;
import Service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    AccountService accountService;
    MessageService messageService;

    public SocialMediaController(){
        this.accountService = new AccountService();
        this.messageService = new MessageService();
    }

    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        // DONE
        app.post("/register", this::postAccountHandler);
        // DONE
        app.post("/login", this::postLoginHandler);
        // DONE
        app.post("/messages", this::postMessageHandler);
        // DONE
        app.get("/messages", this::getAllMessagesHandler);
        // DONE
        app.get("messages/{message_id}",this::getMessageByID);
        // Done
        app.delete("/messages/{message_id}", this::deleteMessageByID);

        app.patch("/messages/{message_id}", this::patchMessageByID);
        
        app.get("/accounts/{account_id}/messages", this::getAccountMessages);
        
        return app;
    }

    /**
     * Handler to post a new account.
     * @param ctx
     * @throws JsonProcessingException
     */
    private void postAccountHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);
        if(account.getUsername() == null || account.getUsername().trim().isEmpty()){
            ctx.status(400);
            return;
        }
        if(account.getPassword().length() < 4){
            ctx.status(400);
            return;
        } 
        Account addedAccount = accountService.addAccount(account);
        if(addedAccount!=null){
            ctx.json(mapper.writeValueAsString(addedAccount));
        }else{
            ctx.status(400);
        }
    }

    /**
     * Handler to post a login
     * @param ctx
     * @throws JsonProcessingException
     */
    private void postLoginHandler(Context ctx) throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);

        List<Account> storedAccounts = accountService.getAllAccounts();
        for (Account acc : storedAccounts) {
            if(acc.getUsername().equals(account.getUsername())){
                if(acc.getPassword().equals(account.getPassword())){
                    ctx.json(mapper.writeValueAsString(acc));
                    ctx.status(200);
                    return;
                }
            }
        }
        ctx.status(401);
        
    }

    /**
     * Handler to post message
     * @param ctx
     * @throws JsonProcessingException
     */
    private void postMessageHandler(Context ctx) throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(ctx.body(), Message.class);
        if(message.getMessage_text() == null || message.getMessage_text().trim().isEmpty() || message.getMessage_text().length() > 255){
            ctx.status(400);
            return;

        }

        Message addedMessage = messageService.addMessage(message);
        if(addedMessage!=null){
            ctx.json(mapper.writeValueAsString(addedMessage));
        }else{
            ctx.status(400);
        }

        

    }
    
    /**
     * 
     * Handler to get message
     * @param ctx
     */
    private void getAllMessagesHandler(Context ctx) {
        ctx.json(messageService.getAllMessages());
    
    }

    /**
     * Handler to get message by specified ID
     * @param ctx
     */
    private void getMessageByID(Context ctx){
        int message_id = Integer.parseInt(ctx.pathParam("message_id"));
        List<Message> allMessages = messageService.getAllMessages();
        for (Message mess : allMessages) {
            if(mess.getMessage_id() == message_id){
                ctx.json(mess);
            }       
        }
    }



    /**
     * Handler to delete message by specified ID
     * @param ctx
     */
    private void deleteMessageByID(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        int message_id = Integer.parseInt(ctx.pathParam("message_id"));

        Message deletedMessage = messageService.deleteMessage(message_id);
        if(deletedMessage != null){
            ctx.json(mapper.writeValueAsString(deletedMessage));
        }

    }

    /**
     * Handler to edit message with specified ID
     * @param ctx
     */
    private void patchMessageByID(Context ctx) throws JsonProcessingException {
        int message_id = Integer.parseInt(ctx.pathParam("message_id"));
        ObjectMapper mapper = new ObjectMapper();
        MiniMessage message = mapper.readValue(ctx.body(), MiniMessage.class);
        String message_text = message.getMessage_text();
        if(message.getMessage_text().trim().isEmpty() || message_text.length() > 255){
            ctx.status(400);
            return;
 
        }

        int exists = 0;

        List<Message> allMessages = messageService.getAllMessages();
        for (Message mess : allMessages) {
            if(mess.getMessage_id() == message_id){
                messageService.editMessage(message_id, message_text);
                exists = 1;
                break;
            }   
        }
        if(exists == 0){
            ctx.status(400);
            return;
        }

        List<Message> editedMessages = messageService.getAllMessages();
        for (Message mess : editedMessages) {
            if(mess.getMessage_id() == message_id){
                ctx.json(mess);
                break;
            }       
        }
    }

    /**
     * Handler to return all messages of specified account
     * @param ctx
     */
    private void getAccountMessages(Context ctx){
        int account_id = Integer.parseInt(ctx.pathParam("account_id"));
        List<Message> userMessages = new ArrayList<>();

        List<Message> allMessages = messageService.getAllMessages();
        for (Message mess: allMessages) {
            if(mess.getPosted_by() == account_id){
                userMessages.add(mess);
            }   
        }
        ctx.json(userMessages);





    }


}