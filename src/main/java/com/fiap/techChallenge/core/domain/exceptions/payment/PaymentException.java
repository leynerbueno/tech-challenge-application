package com.fiap.techChallenge.core.domain.exceptions.payment;

import com.fiap.techChallenge.core.domain.exceptions.DomainException;

public class PaymentException extends DomainException {

    public PaymentException(String mensagem) {
        super(mensagem);
    }

    public PaymentException(String mensagem, Throwable causa) {
        super(mensagem);
        initCause(causa);
    }
}
