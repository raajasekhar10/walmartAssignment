package com.walmart.ticketservice.utils;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultConfirmationCodeGeneratorTest {

    ConfirmationCodeGenerator confirmationCodeGenerator = new DefaultConfirmationCodeGenerator();

    @Test
    public void testGenerate() throws Exception {
        assertThat(confirmationCodeGenerator.generate()).hasSize(10);
    }
}