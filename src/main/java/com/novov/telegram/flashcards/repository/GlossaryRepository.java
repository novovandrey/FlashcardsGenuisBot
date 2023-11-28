package com.novov.telegram.flashcards.repository;

import com.novov.telegram.flashcards.models.Flashcard;
import com.novov.telegram.flashcards.models.Glossary;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GlossaryRepository extends CrudRepository<Glossary, Long> {
    Glossary findByUserIdAndWord(long userId, String word);

}