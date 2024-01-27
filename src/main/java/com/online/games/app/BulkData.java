package com.online.games.app;

import java.util.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.github.javafaker.Faker;

public class BulkData {
        private final List<String> bankNames = Arrays.asList(
            "Bank of America",
            "JPMorgan Chase",
            "Wells Fargo",
            "Citigroup",
            "Goldman Sachs",
            "Morgan Stanley",
            "HSBC",
            "Barclays",
            "Royal Bank of Canada",
            "BNP Paribas"
        );

    public void createMock() {
        Faker faker = new Faker();
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String email = faker.internet().emailAddress();
        String bankName = bankNames.get(faker.random().nextInt(bankNames.size()));
        Double amout = faker.number().randomDouble(2, 1, 10000);
       
    }
}