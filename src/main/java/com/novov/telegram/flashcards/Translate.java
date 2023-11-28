package com.novov.telegram.flashcards;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
@Component
public class Translate {



    public Translate() {

    }

    public CompletableFuture<HttpResponse<String>> doTranslate(String inputMessageTxt) {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                .build();


        URL res = getClass().getClassLoader().getResource("request.json");

        HttpRequest request = null;
        String fileBody;
//            try(FileInputStream inputStream = new FileInputStream(res.toURI().getPath())) {
//                fileBody = IOUtils.toString(inputStream).replace("#text", inputMessageTxt);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//
            fileBody = "{\n" +
                "  \"folderId\": \"b1gs44rs725uiddqm1re\",\n" +
                "  \"texts\": [\"#text\"],\n" +
                "  \"sourceLanguageCode\": \"en\",\n" +
                "  \"targetLanguageCode\": \"ru\"\n" +
                "}";

            request = HttpRequest.newBuilder()
                .uri(URI.create("https://translate.api.cloud.yandex.net/translate/v2/translate"))
                .timeout(Duration.ofMinutes(2))
                .header("Content-Type", "application/json")
                .header("Authorization", "Api-Key AQVNwZc8fuh4LTIPje95oR5TGkOzi2NjY5lwkCSn")
                .POST(HttpRequest.BodyPublishers.ofString(fileBody.replace("#text", inputMessageTxt)))
                .build();



        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

}
