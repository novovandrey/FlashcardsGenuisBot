package com.novov.telegram.flashcards.config;

import com.novov.telegram.flashcards.utils.ResourceReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfig {

    @Bean
    public String resourceString() {
        return ResourceReader.readFileToString("stat_template.txt");
    }
}
