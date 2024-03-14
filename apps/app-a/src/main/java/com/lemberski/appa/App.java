package com.lemberski.appa;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import io.javalin.Javalin;

public class App {

    private static final String PORT = System.getenv().getOrDefault("PORT", "8080");
    private static final String APP_B = System.getenv().getOrDefault("APP_B", "http://localhost:8081");

    public static void main(String[] args) throws URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(APP_B))
                .GET()
                .build();

        Javalin.create(/* config */)
                .get("/", ctx -> {
                    HttpResponse<String> response = HttpClient
                            .newHttpClient()
                            .send(request, BodyHandlers.ofString());
                    ctx.result("App A <- " + response.body());
                })
                .start("0.0.0.0", Integer.valueOf(PORT));
    }
}
