package com.novov.telegram.flashcards;

import com.novov.telegram.flashcards.models.Flashcard;
import com.novov.telegram.flashcards.models.Glossary;
import com.novov.telegram.flashcards.repository.FlashcardRepository;
import com.novov.telegram.flashcards.repository.GlossaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class Db {

    @Autowired
    public GlossaryRepository glossaryRepository;

    @Autowired
    public FlashcardRepository flashcardRepository;

    public void saveCard(Long userId, String word, String translation) {

        Glossary glossary = new Glossary(userId, word, translation);

        glossaryRepository.save(glossary);
        flashcardRepository.save(new Flashcard(glossary,0,0));
    }

    public Glossary getGlossaryByUserId(Long userId, String word) {
        return glossaryRepository.findByUserIdAndWord(userId, word);
    }

    public void updateGlossaryByWord(Long userId, String word, String translation) {
        Glossary glossaryToUpdate = glossaryRepository.findByUserIdAndWord(userId, word);
        glossaryToUpdate.setTranslation(translation);
        glossaryRepository.save(glossaryToUpdate);
    }

    public List<Flashcard> getCards(Long userId) {

        return flashcardRepository.findAllByGlossaryUserIdAndShownEquals(userId, 0);

    }
}
