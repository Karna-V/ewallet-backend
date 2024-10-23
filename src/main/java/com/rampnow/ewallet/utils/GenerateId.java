package com.rampnow.ewallet.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class GenerateId {

    private GenerateId() {
        throw new IllegalStateException("Utility class");
    }

    public static String withPrefix(String prefix) {
        // current date in yyyyMMdd format
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String dateString = currentDate.format(formatter);
        return prefix + dateString + UUID.randomUUID().toString().replace("-", "").substring(0, 6);
    }
}
