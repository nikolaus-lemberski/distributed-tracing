package com.lemberski.appc;

import io.quarkus.runtime.annotations.QuarkusMain;
import io.quarkus.runtime.Quarkus;

@QuarkusMain
public class App {

    public static void main(String... args) {
        Quarkus.run(args);
    }

}
