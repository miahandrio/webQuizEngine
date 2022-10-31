package engine.JSONEntities;

import java.util.Arrays;


public class QuizWebEntity {
    private String title;
    private String text;
    private String[] options;
    private int[] answers;

    public QuizWebEntity() {
    }

    public QuizWebEntity(String title, String text, String[] options, int[] answers) {
        this.title = title;
        this.text = text;
        this.options = options;
        this.answers = answers;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public int[] getAnswers() {
        return answers;
    }

    public void setAnswers(int[] answers) {
        this.answers = answers;
    }

    @Override
    public String toString() {
        return "QuizWebEntity{" +
                "title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", options=" + Arrays.toString(options) +
                ", answer=" + Arrays.toString(answers) +
                '}';
    }
}
