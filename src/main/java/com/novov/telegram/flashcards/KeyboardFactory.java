package com.novov.telegram.flashcards;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class KeyboardFactory {
    public static InlineKeyboardMarkup getWordBtns(boolean isEditMode) {

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton yesBtn = new InlineKeyboardButton("yes");
        yesBtn.setCallbackData("yes");
        InlineKeyboardButton noBtn = new InlineKeyboardButton("no");
        noBtn.setCallbackData("no");
        rowInline.add(yesBtn);
        rowInline.add(noBtn);

        InlineKeyboardButton translationBtn = new InlineKeyboardButton();
        if (isEditMode) {
            translationBtn.setText("edit");
            translationBtn.setCallbackData("edit_translation");
        } else {
            translationBtn.setText("show");
            translationBtn.setCallbackData("show_translation");
        }


        rowInline.add(translationBtn);

        // Set the keyboard to the markup
        rowsInline.add(rowInline);
        // Add it to the message
        markupInline.setKeyboard(rowsInline);
        return markupInline;

    }

    public static InlineKeyboardMarkup editMenu() {

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton nextBtn = new InlineKeyboardButton("next");
        nextBtn.setCallbackData("next");
        nextBtn.setText("show next word");
        InlineKeyboardButton allWords = new InlineKeyboardButton("all_words");
        allWords.setCallbackData("all_words");
        nextBtn.setText("show all words");

        rowInline.add(nextBtn);
        rowInline.add(allWords);

        // Set the keyboard to the markup
        rowsInline.add(rowInline);
        // Add it to the message
        markupInline.setKeyboard(rowsInline);
        return markupInline;

    }
}
