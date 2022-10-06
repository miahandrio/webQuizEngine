package engine.JSONEntities;

/**
 * This class is used to respond to the client with the result of the quiz
 * completion.
 * Used in solveQuiz endpoint
 */
public class ServerFeedback {
    private final boolean success;
    private final String feedback;

    public ServerFeedback(boolean success, String feedback) {
        this.success = success;
        this.feedback = feedback;
    }


    @SuppressWarnings("unused")
    public boolean isSuccess() {
        return success;
    }

    @SuppressWarnings("unused")
    public String getFeedback() {
        return feedback;
    }
}
