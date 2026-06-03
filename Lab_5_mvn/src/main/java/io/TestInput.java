package io;

import java.io.*;

public class TestInput {
    public static void main(String[] args) throws IOException {
        System.out.println("=== ТЕСТ ВВОДА ===");
        System.out.print("Введите что-нибудь: ");
        System.out.flush();
        String line = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while (!line.equals("exit")) {

            if (br.ready()) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                line = br.readLine();
            }

        }

        System.out.println("Вы ввели: [" + line + "]");
        System.out.println("=== ТЕСТ ЗАВЕРШЁН ===");
    }
}