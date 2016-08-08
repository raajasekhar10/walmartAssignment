package com.walmart.ticketservice;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class SeatCounterAnswer implements Answer<Integer> {

    int counter = 0;

    @Override
    public Integer answer(InvocationOnMock invocation) throws Throwable {
        return counter++;
    }
}
