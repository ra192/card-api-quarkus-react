package com.card.resource;

import com.card.entity.Merchant;
import com.card.service.TokenService;
import io.smallrye.mutiny.Uni;
import org.jboss.logging.Logger;

import javax.inject.Inject;

public abstract class WithAuthMerchantResource {
    private static final Logger logger= Logger.getLogger(WithAuthMerchantResource.class);

    @Inject
    TokenService tokenService;

    protected Uni<Merchant> validateToken(String authHeader) {
        logger.infof("Authorization header: %s", authHeader);
        final var token = authHeader.replaceFirst("Bearer","").trim();
        return tokenService.validate(token).flatMap(Merchant::findById);
    }
}
