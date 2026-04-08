package com.btgpactual.controller;

import com.btgpactual.domain.Fund;
import com.btgpactual.domain.Transaction;
import com.btgpactual.dto.CancelSubscriptionRequest;
import com.btgpactual.dto.SubscribeRequest;
import com.btgpactual.service.FundService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FundControllerTest {

    @Mock
    private FundService fundService;

    @InjectMocks
    private FundController fundController;

    private Authentication authentication;

    @BeforeEach
    void setUp() {
        authentication = mock(Authentication.class);
    }

    @Test
    void testGetAllFunds() {
        when(fundService.getAllFunds()).thenReturn(List.of(new Fund()));
        
        ResponseEntity<List<Fund>> response = fundController.getAllFunds();
        
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testSubscribe() {
        SubscribeRequest req = new SubscribeRequest("1", new BigDecimal("1000"));
        Transaction t = new Transaction();
        
        when(authentication.getName()).thenReturn("demo");
        when(fundService.subscribe("demo", req)).thenReturn(t);
        
        ResponseEntity<Transaction> response = fundController.subscribe(authentication, req);
        
        assertEquals(200, response.getStatusCode().value());
        assertEquals(t, response.getBody());
    }

    @Test
    void testUnsubscribe() {
        CancelSubscriptionRequest req = new CancelSubscriptionRequest("1");
        Transaction t = new Transaction();
        
        when(authentication.getName()).thenReturn("demo");
        when(fundService.unsubscribe("demo", "1")).thenReturn(t);
        
        ResponseEntity<Transaction> response = fundController.unsubscribe(authentication, req);
        
        assertEquals(200, response.getStatusCode().value());
        assertEquals(t, response.getBody());
    }

    @Test
    void testGetHistory() {
        when(authentication.getName()).thenReturn("demo");
        when(fundService.getHistory("demo")).thenReturn(List.of(new Transaction()));
        
        ResponseEntity<List<Transaction>> response = fundController.getHistory(authentication);
        
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }
}
