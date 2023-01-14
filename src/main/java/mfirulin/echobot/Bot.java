package mfirulin.echobot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {

    private boolean screaming = false;

    @Override
    public String getBotUsername() {
        return "mfirulin_echo";
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

        if (msg.isCommand()) {
            handleCommand(msg.getText());
            return; // We don't want to echo commands, so we exit
        }
    
        try {
            // sendText(userId, msg.getText());
            // copyMessage(userId, msgId);
            if (screaming) {
                scream(userId, update.getMessage()); // Call a custom method
            } else {
                copyMessage(userId, msg.getMessageId()); // Else proceed normally
            }
        } catch (TelegramApiException e) {
            System.err.println(e);
        }
    }

    private void sendText(Long who, String what) throws TelegramApiException {
        var msg = SendMessage.builder()
            .chatId(who.toString()) // Who we are sending a message to
            .text(what) // Message content
            .build();

        execute(msg);
    }

    private void copyMessage(Long who, Integer msgId) throws TelegramApiException {
        var msg = CopyMessage.builder()
            .fromChatId(who.toString()) // We copy from the user
            .chatId(who.toString()) // And send it back to him
            .messageId(msgId) // Specifying what message
            .build();

        execute(msg);
    }

    private void handleCommand(String cmd) {
        screaming = switch(cmd) {
            case "/scream" -> true; // If the command was /scream, we switch gears
            case "/whisper" -> false; // Otherwise, we return to normal
            default -> false;
        };
    }

    private void scream(Long id, Message msg) throws TelegramApiException {
        if (msg.hasText()) {
            sendText(id, msg.getText().toUpperCase());
        } else {
            copyMessage(id, msg.getMessageId());  // We can't really scream a sticker
        }
    }

}
