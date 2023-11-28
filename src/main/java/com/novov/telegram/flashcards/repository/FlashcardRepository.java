package com.novov.telegram.flashcards.repository;

import com.novov.telegram.flashcards.models.Flashcard;
import com.novov.telegram.flashcards.models.Glossary;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FlashcardRepository extends CrudRepository<Flashcard, Long> {
    List<Flashcard> findAllByGlossaryUserIdAndShownEquals(long userId, int shown);

}