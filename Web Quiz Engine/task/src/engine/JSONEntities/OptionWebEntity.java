package engine.JSONEntities;

public class OptionWebEntity {
    int id;
    String text;

    public OptionWebEntity(int id, String text) {
        this.id = id;
        this.text = text;
    }

    public int getId() {
        return id;
    }
    public String getText() {
        return text;
    }
}
