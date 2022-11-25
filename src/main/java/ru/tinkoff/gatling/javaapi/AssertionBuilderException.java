package ru.tinkoff.gatling.javaapi;

import java.util.Objects;

public class AssertionBuilderException extends RuntimeException{

    private final String msg;
    private final Throwable cause;

    AssertionBuilderException(String msg, Throwable cause) {
        this.msg = msg;
        this.cause = cause;
    }

    public String msg() {
        return msg;
    }

    public Throwable cause() {
        return cause;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (AssertionBuilderException) obj;
        return Objects.equals(this.msg, that.msg) &&
                Objects.equals(this.cause, that.cause);
    }

    @Override
    public int hashCode() {
        return Objects.hash(msg, cause);
    }

    @Override
    public String toString() {
        return "AssertionBuilderException[" +
                "msg=" + msg + ", " +
                "cause=" + cause + ']';
    }

}
