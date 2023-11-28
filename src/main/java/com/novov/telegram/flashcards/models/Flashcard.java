package com.novov.telegram.flashcards.models;

import jakarta.persistence.*;

@Entity
public class Flashcard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "id")
    private Glossary glossary;

    @Column
    private int shown;

    @Column
    private int guessed;

    public Flashcard() {
    }

    public Flashcard(Glossary glossary, int shown, int guessed) {
        this.glossary = glossary;
        this.shown = shown;
        this.guessed = guessed;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Glossary getGlossary() {
        return glossary;
    }

    public void setGlossary(Glossary glossary) {
        this.glossary = glossary;
    }

    public int getShown() {
        return shown;
    }

    public void setShown(int shown) {
        this.shown = shown;
    }

    public int getGuessed() {
        return guessed;
    }

    public void setGuessed(int guessed) {
        this.guessed = guessed;
    }
}