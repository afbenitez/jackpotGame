package com.jackpot.game.exception;

public class JackpotAlreadyExistsException extends RuntimeException {
    public JackpotAlreadyExistsException(String message) {
        super(message);
    }
}