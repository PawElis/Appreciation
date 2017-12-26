package structures;

/**
 * Created by Павел on 12.11.2017.
 * Entity Type_Of_Appreciation
 */
public class TypeOfAppreciation {
    private int appreciationTypeId;
    private String nameVocabulary;
    private String nameVocabularyEng;
    private String color;

    public TypeOfAppreciation(int appreciationTypeId, String nameVocabulary, String nameVocabularyEng, String color) {
        this.appreciationTypeId = appreciationTypeId;
        this.nameVocabulary = nameVocabulary;
        this.nameVocabularyEng = nameVocabularyEng;
        this.color = color;
    }

    public int getAppreciationTypeId() {
        return appreciationTypeId;
    }

    public String getNameVocabulary() {
        return nameVocabulary;
    }

    public String getColor() {
        return color;
    }

    public String getNameVocabularyEng() {
        return nameVocabularyEng;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setNameVocabularyEng(String nameVocabularyEng) {
        this.nameVocabularyEng = nameVocabularyEng;
    }

    public void setAppreciationTypeId(int appreciationTypeId) {
        this.appreciationTypeId = appreciationTypeId;
    }

    public void setNameVocabulary(String nameVocabulary) {
        this.nameVocabulary = nameVocabulary;
    }
}
