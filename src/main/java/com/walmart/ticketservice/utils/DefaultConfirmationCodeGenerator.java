package com.walmart.ticketservice.utils;


import org.apache.commons.lang3.RandomStringUtils;

public class DefaultConfirmationCodeGenerator implements ConfirmationCodeGenerator {

    private int characterCount = 10;

    public DefaultConfirmationCodeGenerator() {
        this(10);
    }

    public DefaultConfirmationCodeGenerator(int characterCount) {
        this.characterCount = characterCount;
    }

    @Override
    public String generate() {
        return RandomStringUtils.randomAlphabetic(characterCount);
    }
}
