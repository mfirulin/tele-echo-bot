package tele.echobot.service;

import java.util.List;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import tele.echobot.config.Config;

@Component
public class Bot extends TelegramLongPollingBot {

    private Config config;

    private boolean screaming = false;
    private InlineKeyboardButton nextButton;
    private InlineKeyboardButton backButton;
    private InlineKeyboardButton urlButton;
    private InlineKeyboardMarkup keyboardM1;
    private InlineKeyboardMarkup keyboardM2;

    public Bot(Config config) {

        this.config = config;

        // Create buttons
        nextButton = InlineKeyboardButton.builder()
            .text("Next")
            .callbackData("next")           
            .build();

        backButton = InlineKeyboardButton.builder()
            .text("Back")
            .callbackData("back")
            .build();

        urlButton = InlineKeyboardButton.builder()
            .text("Tutorial")
            .url("https://core.telegram.org/bots/api")
            .build();

        // Create keyboards. Buttons are wrapped in lists 
        // since each keyboard is a set of button rows.
        keyboardM1 = InlineKeyboardMarkup.builder()
            .keyboardRow(List.of(nextButton))
            .build();  
  
        keyboardM2 = InlineKeyboardMarkup.builder()
            .keyboardRow(List.of(backButton, urlButton))
            // .keyboardRow(List.of(urlButton))
            .build();
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
 
        try {
            // Handle buttons
            if (update.hasCallbackQuery()) {
                handleButton(update.getCallbackQuery());
                return;
            }

            if (update.hasMessage()) {
                var msg = update.getMessage();

                // Handle commands
                if (msg.isCommand()) {
                    handleCommand(msg);
                    return; // We don't want to echo commands, so we exit
                }

                // Handle ordinary messages
                handleMessage(msg);
            }
        } catch (TelegramApiException e) {
            System.err.println(e);
        }
    }

    private void handleMessage(Message msg) throws TelegramApiException {
        var chatId = msg.getFrom().getId();
        var msgId = msg.getMessageId();

        if (msg.hasText()) {
            String text = msg.getText();
            if (screaming) {
                text = text.toUpperCase();
            }
            sendText(chatId, text);
        } else {
            copyMessage(chatId, msgId);  // We can't really scream a sticker
        }
    }

    private void sendText(Long chatId, String text) throws TelegramApiException {
        var msg = SendMessage.builder()
            .chatId(chatId.toString()) // Who we are sending a message to
            .text(text) // Message content
            .build();

        execute(msg);
    }

    private void copyMessage(Long chatId, Integer msgId) throws TelegramApiException {
        var msg = CopyMessage.builder()
            .fromChatId(chatId.toString()) // We copy from the user
            .chatId(chatId.toString()) // And send it back to him
            .messageId(msgId) // Specifying what message
            .build();

        execute(msg);
    }

    private void handleCommand(Message msg) throws TelegramApiException {
        var chatId = msg.getFrom().getId();

        switch (msg.getText()) {
            case "/scream" -> screaming = true; // If the command was /scream, we switch gears
            case "/whisper" -> screaming = false; // Otherwise, we return to normal
            case "/start" -> sendText(chatId, "Hello! I'm a test bot.");
            case "/menu" -> sendMenu(chatId, "<b>Menu 1</b>", keyboardM1);
        };
    }

    private void sendMenu(Long chatId, String txt, InlineKeyboardMarkup kb) throws TelegramApiException {
        var msg = SendMessage.builder()
            .chatId(chatId.toString())
            .parseMode("HTML")
            .text(txt)
            .replyMarkup(kb)
            .build();
    
        execute(msg);
    }

    private void handleButton(CallbackQuery cb) throws TelegramApiException {
        var id = cb.getFrom().getId();
        var queryId = cb.getId();
        var data = cb.getData();
        var msgId = cb.getMessage().getMessageId();

        var newTxt = EditMessageText.builder()
            .chatId(id.toString())
            .messageId(msgId)
            .text("")
            .build();
    
        var newKb = EditMessageReplyMarkup.builder()
            .chatId(id.toString())
            .messageId(msgId)
            .build();                           
    
        switch (data) {
            case "next" -> {
                newTxt.setText("Menu 2");
                newKb.setReplyMarkup(keyboardM2);
            } case "back" -> {
                newTxt.setText("Menu 1");
                newKb.setReplyMarkup(keyboardM1);
            }
        };
    
        var close = AnswerCallbackQuery.builder()
            .callbackQueryId(queryId)
            .build();
    
        execute(close);
        execute(newTxt);
        execute(newKb);
    }
}
