package com.card.resource;

import com.card.entity.Merchant;
import com.card.resource.dto.CreateTokenDto;
import com.card.resource.dto.TokenDto;
import com.card.service.TokenService;
import io.smallrye.mutiny.Uni;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api/token")
public class TokenResource {
    private static final Logger logger = Logger.getLogger(TokenResource.class);

    @Inject
    TokenService tokenService;

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Uni<TokenDto> create(CreateTokenDto requestObj) {
        logger.info("Create token method was called with params:");
        logger.info(requestObj.toString());

        final Uni<Merchant> merchant = Merchant.findById(requestObj.getMerchantId());
        return merchant.flatMap(m -> tokenService.create(m, requestObj.getSecret())).map(TokenDto::new);
    }
}
