package engine.exceptions;

public class QuizOptionContainingIllegalCharacterExeption extends RuntimeException {
    public QuizOptionContainingIllegalCharacterExeption(String message) {
        super(message);
    }
}
