package structures;

/**
 * Created by Pavel on 11.11.2017.
 * Entity Vocabulary
 */

public class Vocabulary {
    private int appreciationTypeId;
    private String word;

    public Vocabulary(int appreciationTypeId, String word) {
        this.appreciationTypeId = appreciationTypeId;
        this.word = word;
    }

    public int getAppreciationTypeId() {
        return appreciationTypeId;
    }

    public String getWord() {
        return word;
    }

    public void setAppreciationTypeId(int appreciationTypeId) {
        this.appreciationTypeId = appreciationTypeId;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
