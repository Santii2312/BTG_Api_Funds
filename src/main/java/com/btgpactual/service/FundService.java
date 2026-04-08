package com.btgpactual.service;

import com.btgpactual.domain.Fund;
import com.btgpactual.domain.Transaction;
import com.btgpactual.domain.TransactionType;
import com.btgpactual.domain.User;
import com.btgpactual.dto.SubscribeRequest;
import com.btgpactual.exception.ActiveSubscriptionNotFoundException;
import com.btgpactual.exception.FundAlreadySubscribedException;
import com.btgpactual.exception.InsufficientBalanceException;
import com.btgpactual.exception.ResourceNotFoundException;
import com.btgpactual.repository.FundRepository;
import com.btgpactual.repository.TransactionRepository;
import com.btgpactual.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FundService {

    @Autowired
    private FundRepository fundRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private NotificationService notificationService;

    public List<Fund> getAllFunds() {
        return fundRepository.findAll();
    }

    public Transaction subscribe(String username, SubscribeRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Fund fund = fundRepository.findById(request.getFundId())
                .orElseThrow(() -> new ResourceNotFoundException("Fund not found"));

        if (request.getAmount().compareTo(fund.getMontoMinimo()) < 0) {
            throw new InsufficientBalanceException(
                    "El monto debe ser al menos el mínimo del fondo: " + fund.getMontoMinimo());
        }

        if (user.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException(
                    "No tiene saldo disponible para vincularse al fondo " + fund.getNombre());
        }

        BigDecimal activeBalance = calculateActiveSubscriptionBalance(user.getId(), fund.getId());
        if (activeBalance.compareTo(BigDecimal.ZERO) > 0) {
            throw new FundAlreadySubscribedException(
                    "Ya se encuentra suscrito actualmente al fondo " + fund.getNombre() + ". Debe cancelar la suscripción actual antes de volver a suscribirse.");
        }

        // Resta el disponible
        user.setBalance(user.getBalance().subtract(request.getAmount()));
        userRepository.save(user);

        // Guardar la transacción
        Transaction transaction = Transaction.builder()
                .userId(user.getId())
                .fundId(fund.getId())
                .type(TransactionType.SUBSCRIBE)
                .amount(request.getAmount())
                .timestamp(LocalDateTime.now())
                .build();
        transactionRepository.save(transaction);

        // Envía la notificación
        notificationService.sendNotification(user, "Se ha suscrito exitosamente al fondo " + fund.getNombre());

        return transaction;
    }

    public Transaction unsubscribe(String username, String fundId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> new ResourceNotFoundException("Fund not found"));

        // Calculamos el saldo neto: lo suscrito menos lo previamente cancelado utilizando el método auxiliar
        BigDecimal amountToReturn = calculateActiveSubscriptionBalance(user.getId(), fundId);

        if (amountToReturn.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ActiveSubscriptionNotFoundException(
                    "No posee suscripción activa para cancelar en el fondo " + fund.getNombre());
        }

        // Sumar al disponible
        user.setBalance(user.getBalance().add(amountToReturn));
        userRepository.save(user);

        // Guardar transacción
        Transaction unsubscription = Transaction.builder()
                .userId(user.getId())
                .fundId(fund.getId())
                .type(TransactionType.UNSUBSCRIBE)
                .amount(amountToReturn)
                .timestamp(LocalDateTime.now())
                .build();
        transactionRepository.save(unsubscription);

        notificationService.sendNotification(user,
                "Se ha cancelado exitosamente la suscripción al fondo " + fund.getNombre());

        return unsubscription;
    }

    public List<Transaction> getHistory(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return transactionRepository.findByUserId(user.getId());
    }

    private BigDecimal calculateActiveSubscriptionBalance(String userId, String fundId) {
        List<Transaction> transactions = transactionRepository.findByUserIdAndFundId(userId, fundId);
        BigDecimal balance = BigDecimal.ZERO;
        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.SUBSCRIBE) {
                balance = balance.add(t.getAmount());
            } else if (t.getType() == TransactionType.UNSUBSCRIBE) {
                balance = balance.subtract(t.getAmount());
            }
        }
        return balance;
    }
}
