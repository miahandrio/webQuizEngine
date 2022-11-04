package engine.JSONEntities;

public class AnswerWebEntity {
    String[] answer;

    public void setAnswer(String[] answer) {
        this.answer = answer;
    }

    public String[] getAnswer() {
        return answer;
    }

    public Answer toAnswer() {
        Answer answer = new Answer();
        if (this.answer != null) {
            int[] answerArray = new int[this.answer.length];
            for (int i = 0; i < this.answer.length; i++) {
                answerArray[i] = Integer.parseInt(this.answer[i]);
            }
            answer.setAnswer(answerArray);
        } else {
            answer.setAnswer(null);
        }
        return answer;
    }
}
