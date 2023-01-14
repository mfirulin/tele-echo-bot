package mfirulin.echobot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {

    @Override
    public String getBotUsername() {
        return "mfirulin_echo_bot";
    }

    @Override
    public String getBotToken() {
        return "5914438112:AAHltPCX6W4Ws6PfrTJOyyBYYrJr76_Zcj8";
    }

    @Override
    public void onUpdateReceived(Update update) {
        var msg = update.getMessage();
        var msgId = msg.getMessageId();
        var userId = msg.getFrom().getId();
    
        try {
            // sendText(userId, msg.getText());
            copyMessage(userId, msgId);
        } catch (TelegramApiException e) {
            System.err.println(e);
        }
    }

    public void sendText(Long who, String what) throws TelegramApiException {
        var msg = SendMessage.builder()
            .chatId(who.toString()) // Who we are sending a message to
            .text(what) // Message content
            .build();

        execute(msg);
    }

    public void copyMessage(Long who, Integer msgId) throws TelegramApiException {
        var msg = CopyMessage.builder()
            .fromChatId(who.toString()) // We copy from the user
            .chatId(who.toString()) // And send it back to him
            .messageId(msgId) // Specifying what message
            .build();

        execute(msg);
    }

}
