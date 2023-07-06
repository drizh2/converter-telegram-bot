package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main extends TelegramLongPollingBot {
    BotCurrency source;
    BotCurrency target;

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(new Main());
    }

    @Override
    public String getBotUsername() {
        return "drizh2_convertion_bot";
    }

    @Override
    public String getBotToken() {
        return "6067316383:AAF6rhfXVGapCp_5qu2nEdpdNXWekautSN0";
    }

    @Override
    public void onUpdateReceived(Update update) {
        Long chatId = getChatId(update);

        if (update.hasMessage()) {
            String messageText = update.getMessage().getText();
            if (messageText.equals("/start")) {
                sendDefaultMessage(chatId, null, null);
            } else {
                try {
                    Integer amount = Integer.valueOf(messageText);
                    sendConvertedMessage(chatId, source, target, amount);
                } catch (NumberFormatException e) {
                    sendMessage(chatId, "You entered wrong value! Please enter normal'no!!");
                    sendDefaultMessage(chatId, source, target);
                }
            }
        }

        if (update.hasCallbackQuery()) {
            String callbackQuery = update.getCallbackQuery().getData();

            BotCurrency sourceCurrency = BotCurrency.getSourceCurrency(callbackQuery);
            BotCurrency targetCurrency = BotCurrency.getTargetCurrency(callbackQuery);

            if ((Objects.nonNull(sourceCurrency) && sourceCurrency.equals(target)) || (Objects.nonNull(targetCurrency) && targetCurrency.equals(source))) {
                sendErrorMessge(chatId);
            } else {
                source = Objects.nonNull(sourceCurrency) ? sourceCurrency : source;
                target = Objects.nonNull(targetCurrency) ? targetCurrency : target;
            }
            sendDefaultMessage(chatId, source, target);
        }
    }

    private Long getChatId(Update update) {
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom().getId();
        }

        if (update.hasMessage()) {
            return update.getMessage().getFrom().getId();
        }

        return null;
    }

    private void sendDefaultMessage(Long chatId, BotCurrency source, BotCurrency target) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();

        for (BotCurrency currency : BotCurrency.values()) {
            String sourceButtonText = currency.equals(source) ? "✔\uFE0F" + currency.name() : currency.name();
            row1.add(InlineKeyboardButton.builder().text(sourceButtonText).callbackData(currency.getSourceCallback()).build());

            String targetButtonText = currency.equals(target) ? "✔\uFE0F" + currency.name() : currency.name();
            row2.add(InlineKeyboardButton.builder().text(targetButtonText).callbackData(currency.getTargetCallback()).build());
        }

        keyboard.add(row1);
        keyboard.add(row2);

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboard);

        SendMessage message = new SendMessage();

        message.setChatId(chatId);
        message.setText("Choose correct currencies and type how much do you want to convert: ");
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();

        message.setText(new String(text.getBytes(), StandardCharsets.UTF_8));
        message.setParseMode("markdown");
        message.setChatId(chatId);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendErrorMessge(Long chatId) {
        //"You can't choose identical currencies!"
        sendMessage(chatId, "You can't choose identical currencies!");
    }

    private void sendConvertedMessage(Long chatId, BotCurrency source, BotCurrency target, Integer amount) {
        Converter converter = new Converter();
        try {
            sendMessage(chatId, converter.convert(source.name(), target.name(), amount));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}