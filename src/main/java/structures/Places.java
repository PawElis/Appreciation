package structures;

/**
 * Created by Павел on 12.11.2017.
 * For BD
 */

public class Places {
    private int idPlace;
    private float longitude;
    private float latitude;
    private String dataUpdate;
    private int idUser;

    public Places(int idPlace, float longitude, float latitude, String dataUpdate, int idUser) {
        this.idPlace = idPlace;
        this.longitude = longitude;
        this.latitude = latitude;
        this.dataUpdate = dataUpdate;
        this.idUser = idUser;
    }

    public Places(float longitude, float latitude, String dataUpdate, int idUser) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.dataUpdate = dataUpdate;
        this.idUser = idUser;
    }

    public Places(float longitude, float latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public void setIdPlace(int idPlace) {
        this.idPlace = idPlace;
    }

    public void setDataUpdate(String dataUpdate) {
        this.dataUpdate = dataUpdate;
    }

    public String getDataUpdate() {
        return dataUpdate;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public int getIdPlace() {
        return idPlace;
    }

    public int getIdUser() {
        return idUser;
    }
}
