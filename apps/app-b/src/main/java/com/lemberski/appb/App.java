package com.lemberski.appb;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import io.javalin.Javalin;

public class App {

    private static final String PORT = System.getenv().getOrDefault("PORT", "8081");
    private static final String APP_C = System.getenv().getOrDefault("APP_C", "http://localhost:8082");

    public static void main(String[] args) {
        Javalin.create().get("/", ctx -> {
            String appC;
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(APP_C))
                        .GET()
                        .build();

                HttpResponse<String> response = HttpClient
                        .newHttpClient()
                        .send(request, BodyHandlers.ofString());

                appC = response.body();
            } catch (Exception e) {
                appC = e.toString();
            }

            ctx.result("App B <- " + appC);
        }).start("0.0.0.0", Integer.parseInt(PORT));
    }
}