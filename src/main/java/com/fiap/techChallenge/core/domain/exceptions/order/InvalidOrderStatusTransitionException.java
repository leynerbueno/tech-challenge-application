package com.fiap.techChallenge.core.domain.exceptions.order;

import com.fiap.techChallenge.core.domain.exceptions.DomainException;

public class InvalidOrderStatusTransitionException extends DomainException {

    public InvalidOrderStatusTransitionException(String message) {
        super(message);
    }
}
