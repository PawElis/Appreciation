package structures;



/**
 * Created by Pavel on 12.11.2017.
 * Entity Appreciation
 */

public class Appreciation {
    private int idAppreciation;
    private int idPlace;
    private int appreciationTypeId;
    private byte weight;
    private int numberOfAppreciation;
    private int idUser;

    public Appreciation(int idAppreciation, int idPlace, int appreciationTypeId, byte weight, int numberOfAppreciation, int idUser) {
        this.idAppreciation = idAppreciation;
        this.idPlace = idPlace;
        this.appreciationTypeId = appreciationTypeId;
        this.weight = weight;
        this.numberOfAppreciation = numberOfAppreciation;
        this.idUser = idUser;
    }

    public Appreciation(int idPlace, int appreciationTypeId, byte weight, int numberOfAppreciation, int idUser) {
        // this.idAppreciation = 0; // Или убрать вовсе, это нужно только для добавления в бд
        this.idPlace = idPlace;
        this.appreciationTypeId = appreciationTypeId;
        this.weight = weight;
        this.numberOfAppreciation = numberOfAppreciation;
        this.idUser = idUser;
    }

    public int getIdUser() {
        return idUser;
    }

    public int getAppreciationTypeId() {
        return appreciationTypeId;
    }

    public int getIdAppreciation() {
        return idAppreciation;
    }

    public int getIdPlace() {
        return idPlace;
    }

    public int getNumberOfAppreciation() {
        return numberOfAppreciation;
    }

    public byte getWeight() {
        return weight;
    }

    public void setAppreciationTypeId(int appreciationTypeId) {
        this.appreciationTypeId = appreciationTypeId;
    }

    public void setIdAppreciation(int idAppreciation) {
        this.idAppreciation = idAppreciation;
    }

    public void setIdPlace(int idPlace) {
        this.idPlace = idPlace;
    }

    public void setNumberOfAppreciation(int numberOfAppreciation) {
        this.numberOfAppreciation = numberOfAppreciation;
    }

    public void setWeight(byte weight) {
        this.weight = weight;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }
}
