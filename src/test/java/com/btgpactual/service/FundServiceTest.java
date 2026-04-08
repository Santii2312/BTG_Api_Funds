package com.btgpactual.service;

import com.btgpactual.domain.Fund;
import com.btgpactual.domain.Transaction;
import com.btgpactual.domain.TransactionType;
import com.btgpactual.domain.User;
import com.btgpactual.dto.SubscribeRequest;
import com.btgpactual.exception.ActiveSubscriptionNotFoundException;
import com.btgpactual.exception.FundAlreadySubscribedException;
import com.btgpactual.exception.InsufficientBalanceException;
import com.btgpactual.repository.FundRepository;
import com.btgpactual.repository.TransactionRepository;
import com.btgpactual.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FundServiceTest {

    @Mock
    private FundRepository fundRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private FundService fundService;

    private User testUser;
    private Fund testFund;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("1")
                .username("test")
                .balance(new BigDecimal("500000"))
                .build();

        testFund = Fund.builder()
                .id("1")
                .nombre("FPV_BTG_PACTUAL_RECAUDADORA")
                .montoMinimo(new BigDecimal("75000"))
                .build();
    }

    @Test
    void testSubscribeSuccess() {
        SubscribeRequest request = new SubscribeRequest("1", new BigDecimal("100000"));
        
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(testUser));
        when(fundRepository.findById("1")).thenReturn(Optional.of(testFund));
        when(transactionRepository.findByUserIdAndFundId("1", "1")).thenReturn(Collections.emptyList());
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);

        Transaction result = fundService.subscribe("test", request);

        assertNotNull(result);
        assertEquals(TransactionType.SUBSCRIBE, result.getType());
        assertEquals(new BigDecimal("400000"), testUser.getBalance());
        verify(notificationService, times(1)).sendNotification(any(), any());
    }

    @Test
    void testSubscribeAlreadySubscribed() {
        SubscribeRequest request = new SubscribeRequest("1", new BigDecimal("100000"));
        
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(testUser));
        when(fundRepository.findById("1")).thenReturn(Optional.of(testFund));
        
        Transaction previousSub = Transaction.builder().type(TransactionType.SUBSCRIBE).amount(new BigDecimal("10000")).build();
        when(transactionRepository.findByUserIdAndFundId("1", "1")).thenReturn(List.of(previousSub));

        assertThrows(FundAlreadySubscribedException.class, () -> {
            fundService.subscribe("test", request);
        });
    }

    @Test
    void testSubscribeInsufficientBalance() {
        SubscribeRequest request = new SubscribeRequest("1", new BigDecimal("600000"));
        testUser.setBalance(new BigDecimal("500000"));

        when(userRepository.findByUsername("test")).thenReturn(Optional.of(testUser));
        when(fundRepository.findById("1")).thenReturn(Optional.of(testFund));

        Exception exception = assertThrows(InsufficientBalanceException.class, () -> {
            fundService.subscribe("test", request);
        });

        assertTrue(exception.getMessage().contains("No tiene saldo disponible"));
    }

    @Test
    void testSubscribeBelowMinimumAmount() {
        SubscribeRequest request = new SubscribeRequest("1", new BigDecimal("50000"));

        when(userRepository.findByUsername("test")).thenReturn(Optional.of(testUser));
        when(fundRepository.findById("1")).thenReturn(Optional.of(testFund));

        Exception exception = assertThrows(InsufficientBalanceException.class, () -> {
            fundService.subscribe("test", request);
        });

        assertTrue(exception.getMessage().contains("El monto debe ser al menos el mínimo"));
    }
    
    @Test
    void testGetAllFunds() {
        when(fundRepository.findAll()).thenReturn(List.of(testFund));
        List<Fund> result = fundService.getAllFunds();
        assertEquals(1, result.size());
        assertEquals("FPV_BTG_PACTUAL_RECAUDADORA", result.get(0).getNombre());
    }
    
    @Test
    void testUnsubscribeSuccess() {
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(testUser));
        when(fundRepository.findById("1")).thenReturn(Optional.of(testFund));
        
        Transaction previousSub = Transaction.builder().type(TransactionType.SUBSCRIBE).amount(new BigDecimal("100000")).build();
        when(transactionRepository.findByUserIdAndFundId("1", "1")).thenReturn(List.of(previousSub));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);

        Transaction result = fundService.unsubscribe("test", "1");
        
        assertNotNull(result);
        assertEquals(TransactionType.UNSUBSCRIBE, result.getType());
        assertEquals(new BigDecimal("100000"), result.getAmount());
        assertEquals(new BigDecimal("600000"), testUser.getBalance());
    }

    @Test
    void testUnsubscribeNoActiveSubscription() {
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(testUser));
        when(fundRepository.findById("1")).thenReturn(Optional.of(testFund));
        when(transactionRepository.findByUserIdAndFundId("1", "1")).thenReturn(Collections.emptyList());

        assertThrows(ActiveSubscriptionNotFoundException.class, () -> {
            fundService.unsubscribe("test", "1");
        });
    }
    
    @Test
    void testGetHistory() {
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(testUser));
        Transaction t = Transaction.builder().type(TransactionType.SUBSCRIBE).amount(new BigDecimal("10000")).build();
        when(transactionRepository.findByUserId("1")).thenReturn(List.of(t));
        
        List<Transaction> result = fundService.getHistory("test");
        assertEquals(1, result.size());
    }
}
