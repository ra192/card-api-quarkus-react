package com.card.service;

import com.card.entity.Account;
import com.card.entity.Card;
import com.card.entity.Transaction;
import com.card.entity.enums.TransactionType;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class CardService {
    private static final Long CARD_ACCOUNT_ID = 2L;
    private static final Long FEE_ACCOUNT_ID = 3L;

    @Inject
    TransactionService transactionService;

    public Uni<Transaction> deposit(Card card, Long amount, String orderId) {
        return Uni.combine().all().unis(Account.findActive(CARD_ACCOUNT_ID), Account.findActive(FEE_ACCOUNT_ID))
                .asTuple().flatMap(tup -> transactionService.withdraw(card.getAccount(), tup.getItem1(), tup.getItem2()
                        , amount, TransactionType.VIRTUAL_CARD_DEPOSIT, orderId, card));

    }
}
