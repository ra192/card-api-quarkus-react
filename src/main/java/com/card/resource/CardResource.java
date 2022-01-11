package com.card.resource;

import com.card.entity.Card;
import com.card.resource.dto.CreateCardTransactionDto;
import com.card.resource.dto.TransactionDto;
import com.card.service.CardService;
import io.smallrye.mutiny.Uni;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/api/card")
public class CardResource extends  WithAuthMerchantResource {
    private static final Logger logger = Logger.getLogger(CardResource.class);

    @Inject
    CardService cardService;

    @POST
    @Path("/deposit")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<TransactionDto> deposit(@HeaderParam("Authorization") String authHeader,CreateCardTransactionDto requestObj) {
        logger.info("Card deposit method was called with params:");
        logger.info(requestObj.toString());

        final Uni<Card> cardUni = validateToken(authHeader).flatMap(merch-> Card.findById(requestObj.getCardId()));

        return cardUni.flatMap(card->cardService.deposit(card,requestObj.getAmount(),requestObj.getOrderId()))
                .map(trans->new TransactionDto(trans.getId(),trans.getStatus()));
    }
}