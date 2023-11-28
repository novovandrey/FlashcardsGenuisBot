package com.novov.telegram.flashcards;

import com.novov.telegram.flashcards.models.Flashcard;
import com.novov.telegram.flashcards.models.Glossary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

public class Bot extends TelegramLongPollingBot {

    @Autowired
    Translate translate;

    @Autowired
    Db db;

    Map<Integer, Flashcard> flashcardMap = new LinkedHashMap<>();
    Set<Glossary> context = new HashSet<>();
    private String curWord;

    private enum STATE {
        START,
        EDIT
    }

    private STATE curState;

    private Integer editedMessageId;

    int curIndex = 0;

    @Autowired
    @Qualifier("resourceString")
    private String statTemplate;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            User user = message.getFrom();
            String chatId = message.getChatId().toString();
            String text = message.getText();

            try {
                switch(text.toLowerCase(Locale.ROOT)) {
                    case "/start" -> execute(showCard(user.getId(), chatId, "init"));
                    case "/edit" -> execute(showError(user.getId(), chatId, "edit"));
                    case "/stats" -> execute(showStatistics(user.getId(), chatId, user.getId(), "stats"));
                    default -> execute(translateText(text, user.getId(), chatId ));
                }
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String chatId = callbackQuery.getMessage().getChatId().toString();
            Integer messageId = callbackQuery.getMessage().getMessageId();
            try {
                switch(callbackQuery.getData().toLowerCase(Locale.ROOT)) {
                    case "yes" -> execute(showCard(callbackQuery.getFrom().getId(), chatId , "yes"));
                    case "no" -> execute(showCard(callbackQuery.getFrom().getId(), chatId, "no"));
                    case "show_translation" -> execute(editMessage(callbackQuery.getFrom().getId(), chatId, "show_translation", messageId));
                    case "edit_translation" -> execute(editTranslationStart(callbackQuery.getFrom().getId(), chatId, "edit_translation", messageId));
                    case "edit_translation_db" -> execute(editTranslationEnd(callbackQuery.getFrom().getId(), chatId, callbackQuery.getMessage().getText()));
                    default -> execute(translateText("oops", callbackQuery.getFrom().getId(), chatId));
                }
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private SendMessage showStatistics(Long id, String chatId, Long userId, String stats) {

        long count = db.getCards(userId).size();
        long shownCounter = db.getCards(userId).stream().collect(Collectors.summarizingInt(Flashcard::getShown)).getSum();
        long guessedCounter = db.getCards(userId).stream().collect(Collectors.summarizingInt(Flashcard::getGuessed)).getSum();
        int statPercentage = Math.toIntExact(guessedCounter / shownCounter);

        String statTemplateCur = statTemplate
                .replace("words_count", String.valueOf(count))
                .replace("shown_count", String.valueOf(shownCounter))
                .replace("guessed_count",String.valueOf(guessedCounter))
                .replace("percentage_count", String.valueOf(statPercentage));


        SendMessage sendStats = new SendMessage();
        sendStats.setChatId(chatId);
        sendStats.setText(statTemplateCur);
        return sendStats;
    }

    private SendMessage showError(Long id, String chatId, String edit) {
        return new SendMessage(chatId, "Oops, not implemented yet");
    }

    private SendMessage translateText(String text, Long userId, String chatId) {


        CompletableFuture<HttpResponse<String>> result = translate.doTranslate(text);

        String translation = null;
        //translation = "тест";
        try {
            translation = Parser.getResult(result.get().body());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }


        db.saveCard(userId, text, translation);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("translation: " + translation);
        return sendMessage;
    }

    private SendMessage showCard(Long userId, String chatId, String state ) {
        Flashcard flashcardEntry = null;
        String messageText = null;
        String btnText = null;

        EditMessageText new_message = null;
        switch (state.toLowerCase()) {
            case "init" -> {
                AtomicInteger i = new AtomicInteger();
                flashcardMap = db.getCards(userId).stream()
                        .collect(Collectors.toMap(x-> i.getAndIncrement(), Function.identity()));
                flashcardEntry = flashcardMap.get(curIndex);
                context.add(flashcardEntry.getGlossary());
                if (flashcardEntry == null) {
                    curIndex = 0;
                    messageText = "Вы все выучили! Можно начать сначала отправив /start";
                } else {
                    messageText = flashcardEntry.getGlossary().getWord();
                }
            }
            case "yes" -> {
                flashcardEntry = flashcardMap.get(curIndex);
                messageText = flashcardEntry.getGlossary().getWord();
                context.add(flashcardEntry.getGlossary());
            }
            case "no" -> {
                flashcardEntry = flashcardMap.get(curIndex);
                messageText = flashcardEntry.getGlossary().getWord();
                context.add(flashcardEntry.getGlossary());
            }
            default -> messageText = "oops";
        }
        curIndex++;

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(KeyboardFactory.getWordBtns(false));
        sendMessage.setText(messageText);
        return sendMessage;
    }


    private EditMessageText editMessage(Long userId, String chatId, String state, Integer messageId ) {
        String btnText = null;
        EditMessageText new_message = new EditMessageText();

        Flashcard flashcardEntry = flashcardMap.get(curIndex-1);
        btnText = flashcardEntry.getGlossary().getWord() + "/" + flashcardEntry.getGlossary().getTranslation();
        curWord = flashcardEntry.getGlossary().getWord();
        new_message.setReplyMarkup(KeyboardFactory.getWordBtns(true));
        new_message.setChatId(chatId);
        new_message.setMessageId(messageId);
        new_message.setText(btnText);

        return new_message;
    }


    private SendMessage editTranslationStart(Long id, String chatId, String editTranslation, Integer messageId) {
        editedMessageId = messageId;
        curState = STATE.EDIT;
        String msgTxt = null;
        SendMessage new_message = new SendMessage();

        msgTxt = "Введите новый перевод для слова {} и нажмите enter".formatted(curWord);

        new_message.setReplyMarkup(KeyboardFactory.editMenu());
        new_message.setChatId(chatId);
        new_message.setText(msgTxt);

        return new_message;
    }

    private SendMessage editTranslationEnd(Long userId, String chatId, String newTranslation) {

        String msgTxt = null;
        SendMessage new_message = new SendMessage();

        db.updateGlossaryByWord(userId, curWord, newTranslation);

        msgTxt = "Сохранено";

        new_message.setReplyMarkup(KeyboardFactory.editMenu());
        new_message.setChatId(chatId);
        new_message.setText(msgTxt);

        curState = STATE.START;

        return new_message;
    }
    @Override
    public String getBotUsername() {
        return "FlashcardsGenuisBot";
    }

    @Override
    public String getBotToken() {
        return System.getenv("BOT_TOKEN");
    }
}
