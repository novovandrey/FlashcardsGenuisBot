package com.novov.telegram.flashcards;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

public class Parser {

    public static class Root{
        public ArrayList<Translation> translations;
    }

    public static class Translation{
        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getDetectedLanguageCode() {
            return detectedLanguageCode;
        }

        public void setDetectedLanguageCode(String detectedLanguageCode) {
            this.detectedLanguageCode = detectedLanguageCode;
        }

        public String text;
        public String detectedLanguageCode;
    }


    public static String getResult (String body){
        ObjectMapper objectMapper = new ObjectMapper();
        Root root;
        try {
            root = objectMapper.readValue(body, Root.class);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return root.translations.get(0).getText();

    }
}
