package ru.otus.java.pro.unittests.bank.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.java.pro.unittests.bank.entity.Account;
import ru.otus.java.pro.unittests.bank.entity.Agreement;
import ru.otus.java.pro.unittests.bank.service.AccountService;
import ru.otus.java.pro.unittests.bank.service.exception.AccountException;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentProcessorImplTest {

    @Mock
    AccountService accountService;

    @InjectMocks
    PaymentProcessorImpl paymentProcessor;

    @Test
    public void testTransfer() {
        Agreement sourceAgreement = new Agreement();
        sourceAgreement.setId(1L);

        Agreement destinationAgreement = new Agreement();
        destinationAgreement.setId(2L);

        Account sourceAccount = new Account();
        sourceAccount.setAmount(BigDecimal.TEN);
        sourceAccount.setType(0);

        Account destinationAccount = new Account();
        destinationAccount.setAmount(BigDecimal.ZERO);
        destinationAccount.setType(0);

        when(accountService.getAccounts(argThat(argument ->
                argument != null && argument.getId() == 1L))).thenReturn(List.of(sourceAccount));

        when(accountService.getAccounts(argThat(argument ->
                argument != null && argument.getId() == 2L))).thenReturn(List.of(destinationAccount));

        paymentProcessor.makeTransfer(sourceAgreement, destinationAgreement,
                0, 0, BigDecimal.ONE);

    }

    @Test
    public void testTransferWithComission() {
        Agreement sourceAgreement = new Agreement();
        sourceAgreement.setId(1L);

        Agreement destinationAgreement = new Agreement();
        destinationAgreement.setId(2L);

        Account sourceAccount = new Account();
        sourceAccount.setAmount(BigDecimal.TEN);
        sourceAccount.setType(0);

        Account destinationAccount = new Account();
        destinationAccount.setAmount(BigDecimal.ZERO);
        destinationAccount.setType(0);

        when(accountService.getAccounts(argThat(argument ->
                argument != null && argument.getId() == 1L))).thenReturn(List.of(sourceAccount));

        when(accountService.getAccounts(argThat(argument ->
                argument != null && argument.getId() == 2L))).thenReturn(List.of(destinationAccount));

        when(accountService.charge(anyLong(), any(BigDecimal.class))).thenReturn(true);

        when(accountService.makeTransfer(anyLong(), anyLong(), any(BigDecimal.class))).thenReturn(true);

        BigDecimal comissionPercent = new BigDecimal(1);
        BigDecimal amount = BigDecimal.ONE;

        boolean result = paymentProcessor.makeTransferWithComission(sourceAgreement, destinationAgreement,
                0, 0, amount, comissionPercent);

        assertTrue(result);

        verify(accountService).charge(sourceAccount.getId(), amount.negate().multiply(comissionPercent));
        verify(accountService).makeTransfer(sourceAccount.getId(), destinationAccount.getId(), amount);
    }


    @Test
    public void testTransferSourceAccountNotFound() {
        Agreement sourceAgreement = new Agreement();
        sourceAgreement.setId(1L);

        Agreement destinationAgreement = new Agreement();
        destinationAgreement.setId(2L);

        when(accountService.getAccounts(argThat(argument ->
                argument != null && argument.getId() == 1L))).thenReturn(List.of());

        assertThrows(AccountException.class, () -> {
            paymentProcessor.makeTransfer(sourceAgreement, destinationAgreement,
                    0, 0, BigDecimal.ONE);
        });
    }

    @Test
    public void testTransferDestinationAccountNotFound() {
        Agreement sourceAgreement = new Agreement();
        sourceAgreement.setId(1L);

        Agreement destinationAgreement = new Agreement();
        destinationAgreement.setId(2L);

        Account sourceAccount = new Account();
        sourceAccount.setAmount(BigDecimal.TEN);
        sourceAccount.setType(0);

        when(accountService.getAccounts(argThat(argument ->
                argument != null && argument.getId() == 1L))).thenReturn(List.of(sourceAccount));

        when(accountService.getAccounts(argThat(argument ->
                argument != null && argument.getId() == 2L))).thenReturn(List.of());

        assertThrows(AccountException.class, () -> {
            paymentProcessor.makeTransfer(sourceAgreement, destinationAgreement,
                    0, 0, BigDecimal.ONE);
        });
    }
}
