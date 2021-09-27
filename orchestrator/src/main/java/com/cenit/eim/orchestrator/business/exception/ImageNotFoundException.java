package com.cenit.eim.orchestrator.business.exception;

public class ImageNotFoundException extends BusinessException {
    public ImageNotFoundException() {
    }

    public ImageNotFoundException(String s) {
        super(s);
    }

    public ImageNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ImageNotFoundException(Throwable throwable) {
        super(throwable);
    }

    public ImageNotFoundException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
