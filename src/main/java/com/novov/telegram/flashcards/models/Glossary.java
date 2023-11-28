package com.novov.telegram.flashcards.models;

import jakarta.persistence.*;

@Entity
public class Glossary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic
    private long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String word;

    @Column(nullable = false)
    private String translation;

    @OneToOne(mappedBy = "glossary")
    private Flashcard flashcard;

    public Glossary() {
    }

    public Glossary(Long userId, String word, String translation) {
        super();
        this.userId = userId;
        this.word = word;
        this.translation = translation;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }


    public Flashcard getFlashcard() {
        return flashcard;
    }

    public void setFlashcard(Flashcard flashcard) {
        this.flashcard = flashcard;
    }

}