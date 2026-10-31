package com.spotify.demo.exceptionHandler;

public class InvalidCredentialException extends RuntimeException{
    public InvalidCredentialException() {
        super("Invalid Credentials");
    }
}
