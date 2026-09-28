package com.halil.dvdrental;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptTest {
    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Kullanım: şifrenizi program argümanı olarak verin.");
            System.out.println("IntelliJ'de: Run Configuration → Program arguments alanına şifreyi yazın.");
            return;
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        for (String password : args) {
            String hashed = encoder.encode(password);
            System.out.println(password + " -> " + hashed);
        }
    }
}