package engine.JSONEntities;

/**
 * This class is used to send the answer to a quiz
 * @author Mykhailo Bubnov
 */
public class Answer {
    private int[] answer;

    public int[] getAnswer() {
        return answer;
    }

    public void setAnswer(int[] answer) {
        this.answer = answer;
    }
}
