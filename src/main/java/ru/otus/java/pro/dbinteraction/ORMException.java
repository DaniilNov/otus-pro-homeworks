package ru.otus.java.pro.dbinteraction;

public class ORMException extends RuntimeException {
    public ORMException(String message){
        super(message);
    }
    public ORMException(String message, Exception e) {
        super(message);
    }
}
