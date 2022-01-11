package com.card.service;

import com.card.entity.*;
import com.card.entity.enums.TransactionStatus;
import com.card.entity.enums.TransactionType;
import com.card.service.exception.TransactionException;
import io.smallrye.mutiny.Uni;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TransactionService {
    private final Logger logger = Logger.getLogger(TransactionService.class);

    public Uni<Transaction> withdraw(Account srcAccount, Account destAccount, Account feeAccount, Long amount,
                                     TransactionType type, String orderId, Card card) {
        return calculateFee(amount, type, srcAccount).flatMap(fee -> sumByAccount(srcAccount).flatMap(sum -> {
            if (sum - amount - fee < 0)
                return Uni.createFrom().failure(new TransactionException("Source account does not have enough funds"));

            final var transUni = createTransaction(srcAccount, destAccount, amount, type, orderId, card);
            if (fee > 0)
                return transUni.flatMap(trans ->
                        TransactionItem.persist(new TransactionItem(trans, fee, srcAccount, feeAccount, null))
                                .map(v -> trans)).invoke(tr -> logger.infof("Transaction %s was created", tr.getType()));
            else return transUni.invoke(tr -> logger.infof("Transaction %s was created", tr.getType()));
        }));
    }

    private Uni<Transaction> createTransaction(Account srcAccount, Account destAccount, Long amount, TransactionType type,
                                               String orderId, Card card) {
        if (!srcAccount.getCurrency().equals(destAccount.getCurrency()))
            return Uni.createFrom().failure(
                    new TransactionException("Source account currency doesn't match destination account currency"));

        final var transaction = new Transaction(orderId, type, TransactionStatus.COMPLETED);
        return Transaction.persist(transaction).flatMap(v ->
                TransactionItem.persist(new TransactionItem(transaction, amount, srcAccount, destAccount, card))
                        .map(v1 -> transaction));
    }

    private Uni<Long> calculateFee(Long amount, TransactionType type, Account destAccount) {
        return TransactionFee.find(type, destAccount).map(fee -> amount * fee.getRate().longValue());
    }

    private Uni<Long> sumByAccount(Account account) {
        return Uni.combine().all().unis(TransactionItem.sumByDestAccount(account), TransactionItem.sumBySrcAccount(account))
                .combinedWith((destSum, srcSum) -> destSum - srcSum);
    }
}
