package com.lemberski.appb;

import io.javalin.Javalin;

public class App {

    private static final String PORT = System.getenv().getOrDefault("PORT", "8081");

    public static void main(String[] args) {
        Javalin.create(/* config */)
                .get("/", ctx -> ctx.result("App B"))
                .start("0.0.0.0", Integer.valueOf(PORT));
    }
}