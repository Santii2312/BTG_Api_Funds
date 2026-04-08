package com.btgpactual.exception;

public class FundAlreadySubscribedException extends RuntimeException {
    public FundAlreadySubscribedException(String message) {
        super(message);
    }
}
