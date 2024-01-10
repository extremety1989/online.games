// package com.online.games.app;

// package com.projetmongodb.m1.immobilier.model;

// import lombok.Data;
// import org.springframework.data.annotation.Id;
// import org.springframework.data.mongodb.core.mapping.Document;
// import java.time.LocalDateTime;

// @Document(collection = "transactions")
// @Data
// public class Transaction {

//     public enum TransactionStatus {
//         PENDING,
//         COMPLETED,
//         FAILED,
//         REVERSED
//     }

//     public enum TransactionType {
//         DEBIT,
//         CREDIT
//     }

//     public enum TransactionMode {
//         CARD,
//         NET_BANKING,
//         UPI
//     }

//     @Id
//     private String id;

//     private String clientId;
//     private String reservationId;

//     private double amount;
//     private String currency;
//     private String bankName;
//     private String accountNumber;
//     private TransactionType transactionType;
//     private TransactionMode transactionMode;
//     private TransactionStatus transactionStatus;
//     private LocalDateTime transactionDateTime;
    
// }


// import java.time.LocalDateTime;
// import java.util.Arrays;
// import java.util.List;
// import java.util.Locale;
// import com.github.javafaker.Faker;

// public class Transaction {
//         private final List<String> bankNames = Arrays.asList(
//             "Bank of America",
//             "JPMorgan Chase",
//             "Wells Fargo",
//             "Citigroup",
//             "Goldman Sachs",
//             "Morgan Stanley",
//             "HSBC",
//             "Barclays",
//             "Royal Bank of Canada",
//             "BNP Paribas"
//         );
//     public Transaction createMockTransaction() {
//         Faker faker = new Faker(new Locale("en-US")); // Adjust Locale as needed

//         Transaction transaction = new Transaction();

//         transaction.setClientId(faker.idNumber().valid());
//         transaction.setReservationId(faker.idNumber().valid());
//         transaction.setAmount(faker.number().randomDouble(2, 1, 10000));
//         transaction.setCurrency("USD");
//         transaction.setBankName(new Faker().random().nextInt(bankNames.size()));
//         transaction.setAccountNumber(faker.finance().iban());
//         transaction.setTransactionType(faker.number().numberBetween(0, Transaction.TransactionType.values().length));
//         transaction.setTransactionMode(faker.number().numberBetween(0, Transaction.TransactionMode.values().length));
//         transaction.setTransactionStatus(faker.number().numberBetween(0, Transaction.TransactionStatus.values().length));
//         transaction.setTransactionDateTime(LocalDateTime.now());
//     }
// }