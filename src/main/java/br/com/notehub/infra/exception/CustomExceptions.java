package br.com.notehub.infra.exception;

public class CustomExceptions {

    public static abstract class BusinessException extends RuntimeException {
        public BusinessException(String message) {
            super(message);
        }
    }

    public static class SamePasswordException extends BusinessException {
        public SamePasswordException() {
            super("Senha atual.");
        }
    }

    public static class SameEmailExpection extends BusinessException {
        public SameEmailExpection() {
            super("Email atual.");
        }
    }

    public static class SelfFollowException extends BusinessException {
        public SelfFollowException() {
            super("Carência é foda.");
        }
    }

    public static class AlreadyFollowingException extends BusinessException {
        public AlreadyFollowingException() {
            super("Você já segue.");
        }
    }

    public static class NotFollowingException extends BusinessException {
        public NotFollowingException() {
            super("Você já não segue.");
        }
    }

}